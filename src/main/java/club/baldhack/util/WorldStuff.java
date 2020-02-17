package club.baldhack.util;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
public class WorldStuff {
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static void placeBlockMainHand(BlockPos pos) {
        WorldStuff.placeBlock(EnumHand.MAIN_HAND, pos);
    }

    public static void placeBlock(EnumHand hand, BlockPos pos) {
        Vec3d eyesPos = new Vec3d(WorldStuff.mc.player.posX, WorldStuff.mc.player.posY + (double)WorldStuff.mc.player.getEyeHeight(), WorldStuff.mc.player.posZ);
        for (EnumFacing side : EnumFacing.values()) {
            Vec3d hitVec;
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (!WorldStuff.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(WorldStuff.mc.world.getBlockState(neighbor), false) || eyesPos.squareDistanceTo(hitVec = new Vec3d((Vec3i)neighbor).add(new Vec3d(side2.getDirectionVec()).scale(0.5))) > 18.0625) continue;
            double diffX = hitVec.x - eyesPos.x;
            double diffY = hitVec.y - eyesPos.y;
            double diffZ = hitVec.z - eyesPos.z;
            double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
            float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
            float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
            float[] rotations = new float[]{mc.player.rotationYaw + MathHelper.wrapDegrees((float)(yaw - mc.player.rotationYaw)), mc.player.rotationPitch + MathHelper.wrapDegrees((float)(pitch - mc.player.rotationPitch))};
            WorldStuff.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
            WorldStuff.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)WorldStuff.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            WorldStuff. mc.playerController.processRightClickBlock(WorldStuff.mc.player, WorldStuff.mc.world, neighbor, side2, hitVec, hand);
            WorldStuff. mc.player.swingArm(hand);
            WorldStuff.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)WorldStuff.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            return;
        }
    }
    public static int findItem(Item item) {
        try {
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = WorldStuff.mc.player.inventory.getStackInSlot(i);
                if (item != stack.getItem()) continue;
                return i;
            }
        } catch (Exception i) {
            // empty catch block
        }
        return -1;
    }
}
