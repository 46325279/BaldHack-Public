package club.baldhack.module.modules.combat;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import club.baldhack.module.Module;
import club.baldhack.module.Module.Category;
import club.baldhack.module.Module.Info;
import club.baldhack.util.Friends;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Info(
    name = "AutoTrapWeb",
    category = Category.COMBAT
)
public class AutoTrapWeb extends Module {
    BlockPos abovehead;
    BlockPos aboveheadpartner;
    BlockPos aboveheadpartner2;
    BlockPos aboveheadpartner3;
    BlockPos aboveheadpartner4;
    BlockPos side1;
    BlockPos side2;
    BlockPos side3;
    BlockPos side4;
    BlockPos side11;
    BlockPos side22;
    BlockPos side33;
    BlockPos side44;
    int delay;
    public static EntityPlayer target;
    public static List<EntityPlayer> targets;
    public static float yaw;
    public static float pitch;

    public AutoTrapWeb() {
    }

    public boolean isInBlockRange(Entity target) {
        return target.getDistance(mc.player) <= 4.0F;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
    }

    private static void faceVectorPacket(Vec3d vec) {
        double diffX = vec.x - mc.player.posX;
        double diffY = vec.y - mc.player.posY + (double)mc.player.getEyeHeight();
        double diffZ = vec.z - mc.player.posZ;
        double dist = (double)MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, dist)));
        mc.getConnection().sendPacket(new Rotation(mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch), mc.player.onGround));
    }

    public boolean isValid(EntityPlayer entity) {
        return entity instanceof EntityPlayer && isInBlockRange(entity) && entity.getHealth() > 0.0F && !entity.isDead && !entity.getName().startsWith("Body #") && !Friends.isFriend(entity.getName());
    }

    public void loadTargets() {
        Iterator var1 = mc.world.playerEntities.iterator();

        while(var1.hasNext()) {
            EntityPlayer player = (EntityPlayer)var1.next();
            if (!(player instanceof EntityPlayerSP)) {
                if (isValid(player)) {
                    targets.add(player);
                } else if (targets.contains(player)) {
                    targets.remove(player);
                }
            }
        }

    }

    private boolean isStackObby(ItemStack stack) {
        return stack != null && stack.getItem() == Item.getItemById(30);
    }

    private boolean doesHotbarHaveObby() {
        for(int i = 36; i < 45; ++i) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && isStackObby(stack)) {
                return true;
            }
        }

        return false;
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static boolean placeBlockLegit(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);
        Vec3d posVec = (new Vec3d(pos));
        EnumFacing[] var3 = EnumFacing.values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            EnumFacing side = var3[var5];
            BlockPos neighbor = pos.offset(side);
            if (canBeClicked(neighbor)) {
                Vec3d hitVec = posVec.add((new Vec3d(side.getDirectionVec())).scale(0.5D));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0D) {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side.getOpposite(), hitVec, EnumHand.MAIN_HAND);
                    mc.player.swingArm(EnumHand.MAIN_HAND);

                    try {
                        TimeUnit.MILLISECONDS.sleep(10L);
                    } catch (InterruptedException var10) {
                        var10.printStackTrace();
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public void onUpdate() {
        if (!mc.player.isHandActive()) {
            if (!isValid(target) || target == null) {
                updateTarget();
            }

            Iterator var1 = mc.world.playerEntities.iterator();

            while(var1.hasNext()) {
                EntityPlayer player = (EntityPlayer)var1.next();
                if (!(player instanceof EntityPlayerSP) && isValid(player) && player.getDistance(mc.player) < target.getDistance(mc.player)) {
                    target = player;
                    return;
                }
            }

            if (isValid(target) && mc.player.getDistance(target) < 4.0F) {
                trap(target);
            } else {
                delay = 0;
            }

        }
    }

    public static double roundToHalf(double d) {
        return (double)Math.round(d * 2.0D) / 2.0D;
    }

    public void onEnable() {
        delay = 0;
    }

    private void trap(EntityPlayer player) {
        if ((double)player.moveForward == 0.0D && (double)player.moveStrafing == 0.0D && (double)player.moveVertical == 0.0D) {
            ++delay;
        }

        if ((double)player.moveForward != 0.0D || (double)player.moveStrafing != 0.0D || (double)player.moveVertical != 0.0D) {
            delay = 0;
        }

        if (!doesHotbarHaveObby()) {
            delay = 0;
        }

        if (delay == 20 && doesHotbarHaveObby()) {
            abovehead = new BlockPos(player.posX, player.posY + 2.0D, player.posZ);
            aboveheadpartner = new BlockPos(player.posX + 1.0D, player.posY + 2.0D, player.posZ);
            aboveheadpartner2 = new BlockPos(player.posX - 1.0D, player.posY + 2.0D, player.posZ);
            aboveheadpartner3 = new BlockPos(player.posX, player.posY + 2.0D, player.posZ + 1.0D);
            aboveheadpartner4 = new BlockPos(player.posX, player.posY + 2.0D, player.posZ - 1.0D);
            side1 = new BlockPos(player.posX + 1.0D, player.posY, player.posZ);
            side2 = new BlockPos(player.posX, player.posY, player.posZ + 1.0D);
            side3 = new BlockPos(player.posX - 1.0D, player.posY, player.posZ);
            side4 = new BlockPos(player.posX, player.posY, player.posZ - 1.0D);
            side11 = new BlockPos(player.posX + 1.0D, player.posY + 1.0D, player.posZ);
            side22 = new BlockPos(player.posX, player.posY + 1.0D, player.posZ + 1.0D);
            side33 = new BlockPos(player.posX - 1.0D, player.posY + 1.0D, player.posZ);
            side44 = new BlockPos(player.posX, player.posY + 1.0D, player.posZ - 1.0D);

            for(int i = 36; i < 45; ++i) {
                ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
                if (stack != null && isStackObby(stack)) {
                    int oldSlot = mc.player.inventory.currentItem;
                    if (mc.world.getBlockState(abovehead).getMaterial().isReplaceable() || mc.world.getBlockState(side1).getMaterial().isReplaceable() || mc.world.getBlockState(side2).getMaterial().isReplaceable() || mc.world.getBlockState(side3).getMaterial().isReplaceable() || mc.world.getBlockState(side4).getMaterial().isReplaceable()) {
                        mc.player.inventory.currentItem = i - 36;
                        if (mc.world.getBlockState(side1).getMaterial().isReplaceable()) {
                            placeBlockLegit(side1);
                        }

                        if (mc.world.getBlockState(side2).getMaterial().isReplaceable()) {
                            placeBlockLegit(side2);
                        }

                        if (mc.world.getBlockState(side3).getMaterial().isReplaceable()) {
                            placeBlockLegit(side3);
                        }

                        if (mc.world.getBlockState(side4).getMaterial().isReplaceable()) {
                            placeBlockLegit(side4);
                        }

                        if (mc.world.getBlockState(side11).getMaterial().isReplaceable()) {
                            placeBlockLegit(side11);
                        }

                        if (mc.world.getBlockState(side22).getMaterial().isReplaceable()) {
                            placeBlockLegit(side22);
                        }

                        if (mc.world.getBlockState(side33).getMaterial().isReplaceable()) {
                            placeBlockLegit(side33);
                        }

                        if (mc.world.getBlockState(side44).getMaterial().isReplaceable()) {
                            placeBlockLegit(side44);
                        }

                        if (mc.world.getBlockState(aboveheadpartner).getMaterial().isReplaceable()) {
                            placeBlockLegit(aboveheadpartner);
                        }

                        if (mc.world.getBlockState(abovehead).getMaterial().isReplaceable()) {
                            placeBlockLegit(abovehead);
                        }

                        if (mc.world.getBlockState(aboveheadpartner2).getMaterial().isReplaceable()) {
                            placeBlockLegit(aboveheadpartner2);
                        }

                        if (mc.world.getBlockState(aboveheadpartner3).getMaterial().isReplaceable()) {
                            placeBlockLegit(aboveheadpartner3);
                        }

                        if (mc.world.getBlockState(aboveheadpartner4).getMaterial().isReplaceable()) {
                            placeBlockLegit(aboveheadpartner4);
                        }

                        mc.player.inventory.currentItem = oldSlot;
                        delay = 0;
                        break;
                    }

                    delay = 0;
                }

                delay = 0;
            }
        }

    }

    public void onDisable() {
        delay = 0;
        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
        target = null;
    }

    public void updateTarget() {
        Iterator var1 = mc.world.playerEntities.iterator();

        while(var1.hasNext()) {
            EntityPlayer player = (EntityPlayer)var1.next();
            if (!(player instanceof EntityPlayerSP) && !(player instanceof EntityPlayerSP) && isValid(player)) {
                target = player;
            }
        }

    }

    public EnumFacing getEnumFacing(float posX, float posY, float posZ) {
        return EnumFacing.getFacingFromVector(posX, posY, posZ);
    }

    public BlockPos getBlockPos(double x, double y, double z) {
        return new BlockPos(x, y, z);
    }
}
