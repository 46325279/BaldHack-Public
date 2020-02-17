package club.baldhack.module.modules.combat;

import club.baldhack.setting.*;
import club.baldhack.command.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import club.baldhack.module.*;
import net.minecraft.entity.*;
import java.util.*;
import club.baldhack.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

@Module.Info(name = "AutoObby", category = Module.Category.COMBAT)
public class AutoObby extends Module
{
    private final Vec3d[] surroundTargets;
    private Setting<Boolean> triggerable;
    private Setting<Integer> triggerableTimeoutTicks;
    private Setting<Integer> blockPerTick;
    private Setting<Boolean> announceUsage;
    private Setting<Boolean> debugMessages;
    private int playerHotbarSlot;
    private int lastHotbarSlot;
    private int offsetStep;
    private int totalTickRuns;
    private boolean isSneaking;

    public AutoObby() {
        surroundTargets = new Vec3d[] { new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(0.0, -1.0, 0.0) };
        triggerable = register(Settings.b("Triggerable", true));
        triggerableTimeoutTicks = register(Settings.i("Triggerable Timeout (Ticks)", 20));
        blockPerTick = register(Settings.i("Blocks per Tick", 4));
        debugMessages = register(Settings.b("Debug Messages", false));
        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
        offsetStep = 0;
        totalTickRuns = 0;
        isSneaking = false;
    }

    @Override
    protected void onEnable() {
        if (AutoObby.mc.player == null) {
            disable();
            return;
        }
        playerHotbarSlot = Wrapper.getPlayer().inventory.currentItem;
        lastHotbarSlot = -1;
    }

    @Override
    protected void onDisable() {
        if (AutoObby.mc.player == null) {
            return;
        }
        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            Wrapper.getPlayer().inventory.currentItem = playerHotbarSlot;
        }
        if (isSneaking) {
            AutoObby.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoObby.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }
        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
    }

    @Override
    public void onUpdate() {
        if (AutoObby.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (triggerable.getValue() && totalTickRuns >= triggerableTimeoutTicks.getValue()) {
            totalTickRuns = 0;
            disable();
            return;
        }
        int blocksPlaced = 0;
        while (blocksPlaced < blockPerTick.getValue()) {
            if (offsetStep >= surroundTargets.length) {
                offsetStep = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(surroundTargets[offsetStep]);
            final BlockPos targetPos = new BlockPos(AutoObby.mc.player.getPositionVector()).add(offsetPos.x, offsetPos.y, offsetPos.z);
            boolean shouldTryToPlace = true;
            if (!Wrapper.getWorld().getBlockState(targetPos).getMaterial().isReplaceable()) {
                shouldTryToPlace = false;
            }
            for (final Entity entity : AutoObby.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(targetPos))) {
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
        if (blocksPlaced > 0 && lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            Wrapper.getPlayer().inventory.currentItem = playerHotbarSlot;
            lastHotbarSlot = playerHotbarSlot;
        }
        ++totalTickRuns;
    }

    private boolean placeBlock(final BlockPos pos) {
        if (!AutoObby.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }
        if (!BlockInteractionHelper.checkForNeighbours(pos)) {
            return false;
        }
        final EnumFacing[] values = EnumFacing.values();
        final int length = values.length;
        int i = 0;
        while (i < length) {
            final EnumFacing side = values[i];
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (!BlockInteractionHelper.canBeClicked(neighbor)) {
                ++i;
            }
            else {
                final int obiSlot = findObiInHotbar();
                if (obiSlot == -1) {
                    disable();
                    return false;
                }
                if (lastHotbarSlot != obiSlot) {
                    Wrapper.getPlayer().inventory.currentItem = obiSlot;
                    lastHotbarSlot = obiSlot;
                }
                final Block neighborPos = AutoObby.mc.world.getBlockState(neighbor).getBlock();
                if (BlockInteractionHelper.blackList.contains(neighborPos) || BlockInteractionHelper.shulkerList.contains(neighborPos)) {
                    AutoObby.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoObby.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    isSneaking = true;
                }
                final Vec3d hitVec = new Vec3d((Vec3i)neighbor).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                AutoObby.mc.playerController.processRightClickBlock(AutoObby.mc.player, AutoObby.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                AutoObby.mc.player.swingArm(EnumHand.MAIN_HAND);
                return true;
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
}
