package club.baldhack.module.modules.combat;

import club.baldhack.event.events.PacketEvent;
import club.baldhack.event.events.RenderEvent;
import club.baldhack.module.Module;
import club.baldhack.module.ModuleManager;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import club.baldhack.util.*;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
//doesnt work lol
@Module.Info(name = "CrystalAura", category = Module.Category.COMBAT)
public class CrystalAura extends Module
{
    private static boolean togglePitch;
    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;
    private Setting<Boolean> players;
    private Setting<Boolean> mobs;
    private Setting<Boolean> animals;
    private Setting<Boolean> place;
    private Setting<Boolean> explode;
    private Setting<Boolean> autoSwitch;
    private Setting<Boolean> antiWeakness;
    private Setting<Integer> hitDelay;
    private Setting<Double> hitRange;
    private Setting<Double> placeRange;
    private Setting<Integer> red;
    private Setting<Integer> green;
    private Setting<Integer> blue;
    private Setting<Integer> alpha;
    private BlockPos renderBlock;
    private Entity renderEnt;
    private long systemTime;
    private boolean switchCooldown;
    private boolean isAttacking;
    private int oldSlot;
    private int newSlot;
    @EventHandler
    private Listener<PacketEvent.Send> packetListener;

    public CrystalAura() {
        players = register(Settings.b("Players", true));
        mobs = register(Settings.b("Mobs", false));
        animals = register(Settings.b("Animals", false));
        place = register(Settings.b("Place", true));
        explode = register(Settings.b("Explode", true));
        autoSwitch = register(Settings.b("Auto Switch", true));
        antiWeakness = register(Settings.b("Anti Weakness", true));
        hitDelay = register((Setting<Integer>)Settings.integerBuilder("Hit Delay").withMinimum(0).withValue(125).build());
        hitRange = register((Setting<Double>)Settings.doubleBuilder("Hit Range").withMinimum(0.0).withValue(5.5).build());
        placeRange = register((Setting<Double>)Settings.doubleBuilder("Place Range").withMinimum(0.0).withValue(3.5).build());
        red = register(Settings.integerBuilder("Red").withRange(0, 255).withValue(255));
        green = register(Settings.integerBuilder("Green").withRange(0, 255).withValue(255));
        blue = register(Settings.integerBuilder("Blue").withRange(0, 255).withValue(255));
        alpha = register(Settings.integerBuilder("Transparency").withRange(0, 255).withValue(70));
        systemTime = -1L;
        switchCooldown = false;
        isAttacking = false;
        oldSlot = -1;
        final Packet[] packet = new Packet[1];
        packetListener = new Listener<PacketEvent.Send>(event -> {
            packet[0] = event.getPacket();
            if (packet[0] instanceof CPacketPlayer && CrystalAura.isSpoofingAngles) {
                ((CPacketPlayer) packet[0]).yaw = (float)CrystalAura.yaw;
                ((CPacketPlayer) packet[0]).pitch = (float)CrystalAura.pitch;
            }
        });
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(CrystalAura.mc.player.posX), Math.floor(CrystalAura.mc.player.posY), Math.floor(CrystalAura.mc.player.posZ));
    }

    public static float calculateDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        final float doubleExplosionSize = 12.0f;
        final double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double v = (1.0 - distancedsize) * blockDensity;
        final float damage = (float)(int)((v * v + v) / 2.0 * 8.0 * doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion(CrystalAura.mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finald;
    }

    public static float getBlastReduction(final EntityLivingBase entity, float damage, final Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer)entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float)ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            final int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            final float f = MathHelper.clamp((float)k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float)entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private static float getDamageMultiplied(final float damage) {
        final int diff = CrystalAura.mc.world.getDifficulty().getDifficultyId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(final EntityEnderCrystal crystal, final Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    private static void setYawAndPitch(final float yaw1, final float pitch1) {
        CrystalAura.yaw = yaw1;
        CrystalAura.pitch = pitch1;
        CrystalAura.isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (CrystalAura.isSpoofingAngles) {
            CrystalAura.yaw = CrystalAura.mc.player.rotationYaw;
            CrystalAura.pitch = CrystalAura.mc.player.rotationPitch;
            CrystalAura.isSpoofingAngles = false;
        }
    }

    @Override
    public void onWorldRender(final RenderEvent event) {
        final IBlockState[] iBlockState3 = new IBlockState[1];
        final Vec3d[] interp3 = new Vec3d[1];
        final IBlockState[] iBlockState4 = new IBlockState[1];
        final Vec3d[] interp4 = new Vec3d[1];
        final IBlockState[] iBlockState5 = new IBlockState[1];
        final Vec3d[] interp5 = new Vec3d[1];
        if (renderBlock != null) {
            interp3[0] = MathUtil.interpolateEntity(HoleESP.mc.player, HoleESP.mc.getRenderPartialTicks());
            KamiTessellator.drawFullBox(iBlockState3[0].getSelectedBoundingBox(CrystalAura.mc.world, renderBlock).grow(0.0020000000949949026).offset(-interp3[0].x, -interp3[0].y, -interp3[0].z), renderBlock, 1.5f, red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
            if (renderEnt != null) {
                EntityUtil.getInterpolatedRenderPos(renderEnt, CrystalAura.mc.getRenderPartialTicks());
            }
        }
    }

    @Override
    public void onUpdate() {
        final EntityEnderCrystal crystal = (EntityEnderCrystal)CrystalAura.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).map(entity -> entity).min(Comparator.comparing(c -> CrystalAura.mc.player.getDistance(c))).orElse(null);
        if (explode.getValue() && crystal != null && CrystalAura.mc.player.getDistance(crystal) <= hitRange.getValue()) {
            if (System.nanoTime() / 1000000L - systemTime >= hitDelay.getValue()) {
                if (antiWeakness.getValue() && CrystalAura.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                    if (!isAttacking) {
                        oldSlot = Wrapper.getPlayer().inventory.currentItem;
                        isAttacking = true;
                    }
                    newSlot = -1;
                    for (int i = 0; i < 9; ++i) {
                        final ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
                        if (stack != ItemStack.EMPTY) {
                            if (stack.getItem() instanceof ItemSword) {
                                newSlot = i;
                                break;
                            }
                            if (stack.getItem() instanceof ItemTool) {
                                newSlot = i;
                                break;
                            }
                        }
                    }
                    if (newSlot != -1) {
                        Wrapper.getPlayer().inventory.currentItem = newSlot;
                        switchCooldown = true;
                    }
                }
                lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, CrystalAura.mc.player);
                CrystalAura.mc.playerController.attackEntity(CrystalAura.mc.player, crystal);
                CrystalAura.mc.player.swingArm(EnumHand.MAIN_HAND);
                systemTime = System.nanoTime() / 1000000L;
            }
            return;
        }
        resetRotation();
        if (oldSlot != -1) {
            Wrapper.getPlayer().inventory.currentItem = oldSlot;
            oldSlot = -1;
        }
        isAttacking = false;
        int crystalSlot = (CrystalAura.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? CrystalAura.mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (CrystalAura.mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }
        boolean offhand = false;
        if (CrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        }
        else if (crystalSlot == -1) {
            return;
        }
        final List<BlockPos> blocks = findCrystalBlocks();
        final List<Entity> entities = new ArrayList<Entity>();
        if (players.getValue()) {
            entities.addAll(CrystalAura.mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).collect(Collectors.toList()));
        }
        final boolean b2 = false;
        entities.addAll(CrystalAura.mc.world.loadedEntityList.stream().filter(entity -> {
            if (EntityUtil.isLiving(entity)) {
                if (EntityUtil.isPassive(entity) ? animals.getValue() : mobs.getValue()) {
                    return b2;
                }
            }
            return b2;
        }).collect(Collectors.toList()));
        BlockPos q = null;
        Entity lastTarget = null;
        double damage = 0.5;
        for (final Entity entity2 : entities) {
            if (entity2 != CrystalAura.mc.player) {
                if (((EntityLivingBase)entity2).getHealth() <= 0.0f) {
                    continue;
                }
                for (final BlockPos blockPos : blocks) {
                    final double b = entity2.getDistanceSq(blockPos);
                    if (b >= 169.0) {
                        continue;
                    }
                    final double d = calculateDamage(blockPos.x + 0.5, blockPos.y + 1, blockPos.z + 0.5, entity2);
                    if (d <= damage) {
                        continue;
                    }
                    final double self = calculateDamage(blockPos.x + 0.5, blockPos.y + 1, blockPos.z + 0.5, CrystalAura.mc.player);
                    if (self > d && d >= ((EntityLivingBase)entity2).getHealth()) {
                        continue;
                    }
                    if (self - 0.5 > CrystalAura.mc.player.getHealth()) {
                        continue;
                    }
                    damage = d;
                    q = blockPos;
                    lastTarget = entity2;
                    renderEnt = entity2;
                }
            }
        }
        if (damage == 0.5) {
            renderBlock = null;
            renderEnt = null;
            resetRotation();
            return;
        }
        renderBlock = q;
        if (lastTarget instanceof EntityPlayer && ModuleManager.getModuleByName("AutoGF").isEnabled()) {
            final club.baldhack.module.modules.combat.AutoGF autoGF = (AutoGF)ModuleManager.getModuleByName("AutoGF");
            autoGF.addTargetedPlayer(lastTarget.getName());
        }
        if (place.getValue()) {
            if (!offhand && CrystalAura.mc.player.inventory.currentItem != crystalSlot) {
                if (autoSwitch.getValue()) {
                    CrystalAura.mc.player.inventory.currentItem = crystalSlot;
                    resetRotation();
                    switchCooldown = true;
                }
                return;
            }
            lookAtPacket(q.x + 0.5, q.y - 0.5, q.z + 0.5, CrystalAura.mc.player);
            final RayTraceResult result = CrystalAura.mc.world.rayTraceBlocks(new Vec3d(CrystalAura.mc.player.posX, CrystalAura.mc.player.posY + CrystalAura.mc.player.getEyeHeight(), CrystalAura.mc.player.posZ), new Vec3d(q.x + 0.5, q.y - 0.5, q.z + 0.5));
            EnumFacing f;
            if (result == null || result.sideHit == null) {
                f = EnumFacing.UP;
            }
            else {
                f = result.sideHit;
            }
            if (switchCooldown) {
                switchCooldown = false;
                return;
            }
            CrystalAura.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        }
        if (CrystalAura.isSpoofingAngles) {
            if (CrystalAura.togglePitch) {
                final EntityPlayerSP player = CrystalAura.mc.player;
                player.rotationPitch += (float)4.0E-4;
                CrystalAura.togglePitch = false;
            }
            else {
                final EntityPlayerSP player2 = CrystalAura.mc.player;
                player2.rotationPitch -= (float)4.0E-4;
                CrystalAura.togglePitch = true;
            }
        }
    }

    private void lookAtPacket(final double px, final double py, final double pz, final EntityPlayer me) {
        final double[] v = EntityUtil.calculateLookAt(px, py, pz, me);
        setYawAndPitch((float)v[0], (float)v[1]);
    }

    private boolean canPlaceCrystal(final BlockPos blockPos) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        return (CrystalAura.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || CrystalAura.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && CrystalAura.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && CrystalAura.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && CrystalAura.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost)).isEmpty() && CrystalAura.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    private List<BlockPos> findCrystalBlocks() {
        final NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), placeRange.getValue().floatValue(), placeRange.getValue().intValue(), false, true, 0));
        return positions;
    }

    public List<BlockPos> getSphere(final BlockPos loc, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cy = loc.getY();
        final int cz = loc.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - (int)r) : cy; y < (sphere ? (cy + r) : ((float)(cy + h))); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public void onEnable() {
    }

    public void onDisable() {
        renderBlock = null;
        resetRotation();
    }

    static {
        CrystalAura.togglePitch = false;
    }

}
