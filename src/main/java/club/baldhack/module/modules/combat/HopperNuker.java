package club.baldhack.module.modules.combat;

import club.baldhack.util.Wrapper;
import club.baldhack.module.*;
import club.baldhack.setting.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;

@Module.Info(name = "HopperNuker", category = Module.Category.COMBAT)
public class HopperNuker extends Module
{
    private Setting<Double> range;
    private Setting<Boolean> pickswitch;
    private int oldSlot;
    private boolean isMining;

    public HopperNuker() {
        this.range = this.register(Settings.d("Range", 5.5));
        this.pickswitch = this.register(Settings.b("Auto Switch", false));
        this.oldSlot = -1;
        this.isMining = false;
    }

    @Override
    public void onUpdate() {
        final BlockPos pos = this.getNearestHopper();
        if (pos != null) {
            if (!this.isMining) {
                this.oldSlot = Wrapper.getPlayer().inventory.currentItem;
                this.isMining = true;
            }
            final float[] angle = calcAngle(Wrapper.getPlayer().getPositionEyes(Wrapper.getMinecraft().getRenderPartialTicks()), new Vec3d((double)(pos.getX() + 0.5f), (double)(pos.getY() + 0.5f), (double)(pos.getZ() + 0.5f)));
            Wrapper.getPlayer().rotationYaw = angle[0];
            Wrapper.getPlayer().rotationYawHead = angle[0];
            Wrapper.getPlayer().rotationPitch = angle[1];
            if (this.canBreak(pos)) {
                if (this.pickswitch.getValue()) {
                    int newSlot = -1;
                    for (int i = 0; i < 9; ++i) {
                        final ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
                        if (stack != ItemStack.EMPTY) {
                            if (stack.getItem() instanceof ItemPickaxe) {
                                newSlot = i;
                                break;
                            }
                        }
                    }
                    if (newSlot != -1) {
                        Wrapper.getPlayer().inventory.currentItem = newSlot;
                    }
                }
                Wrapper.getMinecraft().playerController.onPlayerDamageBlock(pos, Wrapper.getPlayer().getHorizontalFacing());
                Wrapper.getPlayer().swingArm(EnumHand.MAIN_HAND);
            }
        }
        else if (this.pickswitch.getValue() && this.oldSlot != -1) {
            Wrapper.getPlayer().inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
            this.isMining = false;
        }
    }

    private boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = Wrapper.getWorld().getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, Wrapper.getWorld(), pos) != -1.0f;
    }

    private BlockPos getNearestHopper() {
        Double maxDist = this.range.getValue();
        BlockPos ret = null;
        for (Double x = maxDist; x >= -maxDist; --x) {
            for (Double y = maxDist; y >= -maxDist; --y) {
                for (Double z = maxDist; z >= -maxDist; --z) {
                    final BlockPos pos = new BlockPos(Wrapper.getPlayer().posX + x, Wrapper.getPlayer().posY + y, Wrapper.getPlayer().posZ + z);
                    final double dist = Wrapper.getPlayer().getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                    if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.HOPPER && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY) {
                        maxDist = dist;
                        ret = pos;
                    }
                }
            }
        }
        return ret;
    }

    private static float[] calcAngle(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }
}
