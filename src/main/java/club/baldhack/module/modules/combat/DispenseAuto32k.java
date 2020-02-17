package club.baldhack.module.modules.combat;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import club.baldhack.command.Command;
import club.baldhack.module.Module;
import club.baldhack.module.ModuleManager;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import club.baldhack.util.BlockInteractionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Info(name="DispenseAuto32k", category=Module.Category.COMBAT, description="Do not use with any AntiGhostBlock Mod!")
public class DispenseAuto32k extends Module {
    private static final DecimalFormat df = new DecimalFormat("#.#");
    private Setting<Boolean> rotate = this.register(Settings.b("Rotate", false));
    private Setting<Boolean> grabItem = this.register(Settings.b("Grab Item", false));
    private Setting<Boolean> autoEnableHitAura = this.register(Settings.b("Auto enable Hit Aura", false));
    private Setting<Boolean> autoEnableBypass = this.register(Settings.b("Auto enable Illegals Bypass", false));
    private Setting<Boolean> debugMessages = this.register(Settings.b("Debug Messages", false));
    private int stage;
    private BlockPos placeTarget;
    private int obiSlot;
    private int dispenserSlot;
    private int shulkerSlot;
    private int redstoneSlot;
    private int hopperSlot;
    private boolean isSneaking;

    @Override
    protected void onEnable() {
        if (DispenseAuto32k.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            this.disable();
            return;
        }
        df.setRoundingMode(RoundingMode.CEILING);
        this.stage = 0;
        this.placeTarget = null;
        this.obiSlot = -1;
        this.dispenserSlot = -1;
        this.shulkerSlot = -1;
        this.redstoneSlot = -1;
        this.hopperSlot = -1;
        this.isSneaking = false;
        for (int i = 0; i < 9 && (this.obiSlot == -1 || this.dispenserSlot == -1 || this.shulkerSlot == -1 || this.redstoneSlot == -1 || this.hopperSlot == -1); ++i) {
            ItemStack stack = DispenseAuto32k.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) continue;
            Block block = ((ItemBlock)stack.getItem()).getBlock();
            if (block == Blocks.HOPPER) {
                this.hopperSlot = i;
                continue;
            }
            if (BlockInteractionHelper.shulkerList.contains(block)) {
                this.shulkerSlot = i;
                continue;
            }
            if (block == Blocks.OBSIDIAN) {
                this.obiSlot = i;
                continue;
            }
            if (block == Blocks.DISPENSER) {
                this.dispenserSlot = i;
                continue;
            }
            if (block != Blocks.REDSTONE_BLOCK) continue;
            this.redstoneSlot = i;
        }
        if (this.obiSlot == -1 || this.dispenserSlot == -1 || this.shulkerSlot == -1 || this.redstoneSlot == -1 || this.hopperSlot == -1) {
            if (this.debugMessages.getValue().booleanValue()) {
                Command.sendChatMessage("[Auto32k] Items missing, disabling.");
            }
            this.disable();
            return;
        }
        if (DispenseAuto32k.mc.objectMouseOver == null || DispenseAuto32k.mc.objectMouseOver.getBlockPos() == null || DispenseAuto32k.mc.objectMouseOver.getBlockPos().up() == null) {
            if (this.debugMessages.getValue().booleanValue()) {
                Command.sendChatMessage("[Auto32k] Not a valid place target, disabling.");
            }
            this.disable();
            return;
        }
        this.placeTarget = DispenseAuto32k.mc.objectMouseOver.getBlockPos().up();
        if (this.autoEnableBypass.getValue().booleanValue()) {
            ModuleManager.getModuleByName("IllegalItemBypass").enable();
        }
        if (this.debugMessages.getValue() == false) return;
        Command.sendChatMessage("[Auto32k] Place Target: " + this.placeTarget.x + " " + this.placeTarget.y + " " + this.placeTarget.z + " Distance: " + df.format(DispenseAuto32k.mc.player.getPositionVector().distanceTo(new Vec3d(this.placeTarget))));
    }

    @Override
    public void onUpdate() {
        if (DispenseAuto32k.mc.player == null) return;
        if (ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (this.stage == 0) {
            DispenseAuto32k.mc.player.inventory.currentItem = this.obiSlot;
            this.placeBlock(new BlockPos(this.placeTarget), EnumFacing.DOWN);
            DispenseAuto32k.mc.player.inventory.currentItem = this.dispenserSlot;
            this.placeBlock(new BlockPos(this.placeTarget.add(0, 1, 0)), EnumFacing.DOWN);
            DispenseAuto32k.mc.player.connection.sendPacket(new CPacketEntityAction(DispenseAuto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            DispenseAuto32k.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.placeTarget.add(0, 1, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            this.stage = 1;
            return;
        }
        if (this.stage == 1) {
            if (!(DispenseAuto32k.mc.currentScreen instanceof GuiContainer)) {
                return;
            }
            DispenseAuto32k.mc.playerController.windowClick(DispenseAuto32k.mc.player.openContainer.windowId, 1, this.shulkerSlot, ClickType.SWAP, DispenseAuto32k.mc.player);
            DispenseAuto32k.mc.player.closeScreen();
            DispenseAuto32k.mc.player.inventory.currentItem = this.redstoneSlot;
            this.placeBlock(new BlockPos(this.placeTarget.add(0, 2, 0)), EnumFacing.DOWN);
            this.stage = 2;
            return;
        }
        if (this.stage == 2) {
            Block block = DispenseAuto32k.mc.world.getBlockState(this.placeTarget.offset(DispenseAuto32k.mc.player.getHorizontalFacing().getOpposite()).up()).getBlock();
            if (block instanceof BlockAir) return;
            if (block instanceof BlockLiquid) {
                return;
            }
            DispenseAuto32k.mc.player.inventory.currentItem = this.hopperSlot;
            this.placeBlock(new BlockPos(this.placeTarget.offset(DispenseAuto32k.mc.player.getHorizontalFacing().getOpposite())), DispenseAuto32k.mc.player.getHorizontalFacing());
            DispenseAuto32k.mc.player.connection.sendPacket(new CPacketEntityAction(DispenseAuto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            DispenseAuto32k.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.placeTarget.offset(DispenseAuto32k.mc.player.getHorizontalFacing().getOpposite()), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            DispenseAuto32k.mc.player.inventory.currentItem = this.shulkerSlot;
            if (!this.grabItem.getValue().booleanValue()) {
                this.disable();
                return;
            }
            this.stage = 3;
            return;
        }
        if (this.stage != 3) return;
        if (!(DispenseAuto32k.mc.currentScreen instanceof GuiContainer)) {
            return;
        }
        if (((GuiContainer) DispenseAuto32k.mc.currentScreen).inventorySlots.getSlot(0).getStack().isEmpty) {
            return;
        }
        DispenseAuto32k.mc.playerController.windowClick(DispenseAuto32k.mc.player.openContainer.windowId, 0, DispenseAuto32k.mc.player.inventory.currentItem, ClickType.SWAP, DispenseAuto32k.mc.player);
        if (this.autoEnableHitAura.getValue().booleanValue()) {
            ModuleManager.getModuleByName("Aura").enable();
        }
        this.disable();
    }

    private void placeBlock(BlockPos pos, EnumFacing side) {
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        if (!this.isSneaking) {
            DispenseAuto32k.mc.player.connection.sendPacket(new CPacketEntityAction(DispenseAuto32k.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (this.rotate.getValue().booleanValue()) {
            BlockInteractionHelper.faceVectorPacketInstant(hitVec);
        }
        DispenseAuto32k.mc.playerController.processRightClickBlock(DispenseAuto32k.mc.player, DispenseAuto32k.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        DispenseAuto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
    }
}

