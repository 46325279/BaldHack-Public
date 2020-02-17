package club.baldhack.module.modules.combat;

import club.baldhack.util.BlockInteractionHelper;
import net.minecraft.entity.player.*;
import club.baldhack.setting.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import club.baldhack.command.*;
import club.baldhack.module.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.block.*;
import club.baldhack.util.*;

@Module.Info(name = "AutoTrap", category = Module.Category.COMBAT)
public class AutoTrap extends Module
{
    private final Vec3d[] offsetsDefault;
    private Setting<Double> range;
    private Setting<Integer> blockPerTick;
    private EntityPlayer closestTarget;
    private String lastTickTargetName;
    private int playerHotbarSlot;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int offsetStep;
    private boolean firstRun;

    public AutoTrap() {
        offsetsDefault = new Vec3d[] { new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0) };
        range = register(Settings.d("Range", 5.5));
        blockPerTick = register(Settings.i("Blocks per Tick", 4));
        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
        isSneaking = false;
        offsetStep = 0;
    }

    @Override
    protected void onEnable() {
        if (AutoTrap.mc.player == null) {
            disable();
            return;
        }
        firstRun = true;
        playerHotbarSlot = Wrapper.getPlayer().inventory.currentItem;
        lastHotbarSlot = -1;
    }

    @Override
    protected void onDisable() {
        if (AutoTrap.mc.player == null) {
            return;
        }
        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            Wrapper.getPlayer().inventory.currentItem = playerHotbarSlot;
        }
        if (isSneaking) {
            AutoTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoTrap.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }
        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
    }

    @Override
    public void onUpdate() {
        if (AutoTrap.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        findClosestTarget();
        if (closestTarget == null) {
            if (firstRun) {
                firstRun = false;
            }
            return;
        }
        if (firstRun) {
            firstRun = false;
            lastTickTargetName = closestTarget.getName();
        }
        else if (!lastTickTargetName.equals(closestTarget.getName())) {
            lastTickTargetName = closestTarget.getName();
            offsetStep = 0;
        }
        final List<Vec3d> placeTargets = new ArrayList<Vec3d>();
        Collections.addAll(placeTargets, offsetsDefault);
        int blocksPlaced = 0;
        while (blocksPlaced < blockPerTick.getValue()) {
            if (offsetStep >= placeTargets.size()) {
                offsetStep = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos((Vec3d)placeTargets.get(offsetStep));
            final BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).down().add(offsetPos.x, offsetPos.y, offsetPos.z);
            boolean shouldTryToPlace = true;
            if (!Wrapper.getWorld().getBlockState(targetPos).getMaterial().isReplaceable()) {
                shouldTryToPlace = false;
            }
            for (final Entity entity : AutoTrap.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(targetPos))) {
                if (entity instanceof EntityLivingBase) {
                    shouldTryToPlace = false;
                    break;
                }
            }
            if (shouldTryToPlace && placeBlock(targetPos)) {
                ++blocksPlaced;
            }
            ++offsetStep;
        }
        if (blocksPlaced > 0) {
            if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                Wrapper.getPlayer().inventory.currentItem = playerHotbarSlot;
                lastHotbarSlot = playerHotbarSlot;
            }
            if (isSneaking) {
                AutoTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoTrap.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
        }
    }

    private boolean placeBlock(final BlockPos pos) {
        if (!AutoTrap.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }
        if (!BlockInteractionHelper.checkForNeighbours(pos)) {
            return false;
        }
        final Vec3d eyesPos = new Vec3d(Wrapper.getPlayer().posX, Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(), Wrapper.getPlayer().posZ);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (AutoTrap.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(AutoTrap.mc.world.getBlockState(neighbor), false)) {
                final Vec3d hitVec = new Vec3d((Vec3i)neighbor).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.distanceTo(hitVec) <= range.getValue()) {
                    final int obiSlot = findObiInHotbar();
                    if (obiSlot == -1) {
                        disable();
                        return false;
                    }
                    if (lastHotbarSlot != obiSlot) {
                        Wrapper.getPlayer().inventory.currentItem = obiSlot;
                        lastHotbarSlot = obiSlot;
                    }
                    final Block neighborPos = AutoTrap.mc.world.getBlockState(neighbor).getBlock();
                    if (BlockInteractionHelper.blackList.contains(neighborPos) || BlockInteractionHelper.shulkerList.contains(neighborPos)) {
                        AutoTrap.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoTrap.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                        isSneaking = true;
                    }
                    BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                    AutoTrap.mc.playerController.processRightClickBlock(AutoTrap.mc.player, AutoTrap.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    AutoTrap.mc.player.swingArm(EnumHand.MAIN_HAND);
                    return true;
                }
            }
        }
        return false;
    }

    private int findObiInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockObsidian) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }

    private void findClosestTarget() {
        final List<EntityPlayer> playerList = (List<EntityPlayer>)Wrapper.getWorld().playerEntities;
        closestTarget = null;
        for (final EntityPlayer target : playerList) {
            if (target == AutoTrap.mc.player) {
                continue;
            }
            if (Friends.isFriend(target.getName())) {
                continue;
            }
            if (!EntityUtil.isLiving((Entity)target)) {
                continue;
            }
            if (target.getHealth() <= 0.0f) {
                continue;
            }
            if (closestTarget == null) {
                closestTarget = target;
            }
            else {
                if (Wrapper.getPlayer().getDistance((Entity)target) >= Wrapper.getPlayer().getDistance((Entity)closestTarget)) {
                    continue;
                }
                closestTarget = target;
            }
        }
    }
}
