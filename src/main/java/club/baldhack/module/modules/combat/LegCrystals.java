package club.baldhack.module.modules.combat;

import club.baldhack.module.Module;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import club.baldhack.util.EntityUtil;
import club.baldhack.util.Friends;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Iterator;

@Module.Info(name="LegCrystals", category=Module.Category.COMBAT)
public class LegCrystals
extends Module {
    private Setting<Double> range = this.register(Settings.doubleBuilder("Range").withMinimum(1.0).withValue(5.5).withMaximum(10.0).build());
    private boolean switchCooldown = false;

    @Override
    public void onUpdate() {
        if (LegCrystals.mc.player == null) {
            return;
        }
        int crystalSlot = -1;
        if (LegCrystals.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            crystalSlot = LegCrystals.mc.player.inventory.currentItem;
        } else {
            for (int slot = 0; slot < 9; ++slot) {
                if (LegCrystals.mc.player.inventory.getStackInSlot(slot).getItem() != Items.END_CRYSTAL) continue;
                crystalSlot = slot;
                break;
            }
        }
        if (crystalSlot == -1) {
            return;
        }
        EntityPlayer closestTarget = this.findClosestTarget();
        if (closestTarget == null) {
            return;
        }
        Vec3d targetVector = this.findPlaceableBlock(closestTarget.getPositionVector());
        if (targetVector == null) {
            return;
        }
        BlockPos targetBlock = new BlockPos(targetVector);
        if (LegCrystals.mc.player.inventory.currentItem != crystalSlot) {
            LegCrystals.mc.player.inventory.currentItem = crystalSlot;
            this.switchCooldown = true;
            return;
        }
        if (this.switchCooldown) {
            this.switchCooldown = false;
            return;
        }
        LegCrystals.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(targetBlock, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
    }

    private Vec3d findPlaceableBlock(Vec3d startPos) {
        if (this.canPlaceCrystal(startPos.add(Offsets.NORTH2)) && !this.isExplosionProof(startPos.add(Offsets.NORTH1))) {
            return startPos.add(Offsets.NORTH2);
        }
        if (this.canPlaceCrystal(startPos.add(Offsets.NORTH1))) {
            return startPos.add(Offsets.NORTH1);
        }
        if (this.canPlaceCrystal(startPos.add(Offsets.EAST2)) && !this.isExplosionProof(startPos.add(Offsets.EAST1))) {
            return startPos.add(Offsets.EAST2);
        }
        if (this.canPlaceCrystal(startPos.add(Offsets.EAST1))) {
            return startPos.add(Offsets.EAST1);
        }
        if (this.canPlaceCrystal(startPos.add(Offsets.SOUTH2)) && !this.isExplosionProof(startPos.add(Offsets.SOUTH1))) {
            return startPos.add(Offsets.SOUTH2);
        }
        if (this.canPlaceCrystal(startPos.add(Offsets.SOUTH1))) {
            return startPos.add(Offsets.SOUTH1);
        }
        if (this.canPlaceCrystal(startPos.add(Offsets.WEST2)) && !this.isExplosionProof(startPos.add(Offsets.WEST1))) {
            return startPos.add(Offsets.WEST2);
        }
        if (!this.canPlaceCrystal(startPos.add(Offsets.WEST1))) return null;
        return startPos.add(Offsets.WEST1);
    }

    private EntityPlayer findClosestTarget() {
        EntityPlayer closestTarget = null;
        Iterator iterator = LegCrystals.mc.world.playerEntities.iterator();
        while (iterator.hasNext()) {
            EntityPlayer target = (EntityPlayer)iterator.next();
            if (target == LegCrystals.mc.player || Friends.isFriend(target.getName()) || !EntityUtil.isLiving((Entity)target) || target.getHealth() <= 0.0f || (double)LegCrystals.mc.player.getDistance((Entity)target) > this.range.getValue()) continue;
            if (closestTarget == null) {
                closestTarget = target;
                continue;
            }
            if (!(LegCrystals.mc.player.getDistance((Entity)target) < LegCrystals.mc.player.getDistance((Entity)closestTarget))) continue;
            closestTarget = target;
        }
        return closestTarget;
    }

    private boolean canPlaceCrystal(Vec3d vec3d) {
        BlockPos blockPos = new BlockPos(vec3d.x, vec3d.y, vec3d.z);
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        if (LegCrystals.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK) {
            if (LegCrystals.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) return false;
        }
        if (LegCrystals.mc.world.getBlockState(boost).getBlock() != Blocks.AIR) return false;
        if (LegCrystals.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) return false;
        if (!LegCrystals.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()) return false;
        if (!LegCrystals.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty()) return false;
        return true;
    }

    private boolean isExplosionProof(Vec3d vec3d) {
        BlockPos blockPos = new BlockPos(vec3d.x, vec3d.y, vec3d.z);
        Block block = LegCrystals.mc.world.getBlockState(blockPos).getBlock();
        if (block == Blocks.BEDROCK) {
            return true;
        }
        if (block == Blocks.OBSIDIAN) {
            return true;
        }
        if (block == Blocks.ANVIL) {
            return true;
        }
        if (block == Blocks.ENDER_CHEST) {
            return true;
        }
        if (block != Blocks.BARRIER) return false;
        return true;
    }

    private static class Offsets {
        private static final Vec3d NORTH1 = new Vec3d(0.0, 0.0, -1.0);
        private static final Vec3d NORTH2 = new Vec3d(0.0, 0.0, -2.0);
        private static final Vec3d EAST1 = new Vec3d(1.0, 0.0, 0.0);
        private static final Vec3d EAST2 = new Vec3d(2.0, 0.0, 0.0);
        private static final Vec3d SOUTH1 = new Vec3d(0.0, 0.0, 1.0);
        private static final Vec3d SOUTH2 = new Vec3d(0.0, 0.0, 2.0);
        private static final Vec3d WEST1 = new Vec3d(-1.0, 0.0, 0.0);
        private static final Vec3d WEST2 = new Vec3d(-2.0, 0.0, 0.0);

        private Offsets() {
        }
    }

}

