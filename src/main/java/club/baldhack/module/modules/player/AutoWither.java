package club.baldhack.module.modules.player;

import club.baldhack.event.events.ServerTick;
import club.baldhack.module.Module;
import club.baldhack.module.ModuleManager;
import club.baldhack.util.WorldStuff;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static club.baldhack.util.printMsg.printMsg;
// def doesnt work
//pasted from backdoored
@Module.Info(name = "AutoWither", category = Module.Category.PLAYER)

public class AutoWither extends Module {
    public static final Minecraft mc = Minecraft.getMinecraft();
    private Item SOUL_SAND = new ItemStack(Blocks.SOUL_SAND).getItem();
    private Item WITHER_SKULL = new ItemStack(Blocks.SKULL).getItem();
    private BlockPos basePos = new BlockPos(0, 0, 0);
    private int stage = -1;
    private ServerTick event;

    public void onEnabled() {
        ++this.stage;
        this.stage0();
        ++this.stage;
    }

    @SubscribeEvent
    public void onServerTick(ServerTick event) {
        if (!ModuleManager.isModuleEnabled("AutoWither") || this.stage > 1) {
            this.stage = -1;
            this.setEnabled(false);
            return;
        }
        if (this.stage == 0) {
            this.stage0();
        }
        if (this.stage == 1) {
            this.stage1();
            this.stage = -1;
            this.setEnabled(false);
            return;
        }
        ++this.stage;
    }
    private boolean stage0() {
        this.basePos = AutoWither.mc.objectMouseOver == null || AutoWither.mc.objectMouseOver.sideHit == null ? AutoWither.mc.player.getPosition().add(2, 0, 0) : AutoWither.mc.objectMouseOver.getBlockPos().offset(AutoWither.mc.objectMouseOver.sideHit);
        int soulSandI = WorldStuff.findItem(this.SOUL_SAND);
        int skullI = this.getSkull();
        if (skullI == -1 || soulSandI == -1) {
            String missing = skullI == -1 ? "Wither Skull" : "Soul Sand";
            printMsg(missing + " was not found in your hotbar!", "red");
            this.setEnabled(false);
            return false;
        }
        AutoWither.mc.player.inventory.currentItem = WorldStuff.findItem(this.SOUL_SAND);
        WorldStuff.placeBlockMainHand(this.basePos);
        if (this.isX()) {
            WorldStuff.placeBlockMainHand(this.basePos.add(0, 1, 0));
            WorldStuff.placeBlockMainHand(this.basePos.add(1, 1, 0));
            WorldStuff.placeBlockMainHand(this.basePos.add(-1, 1, 0));
        } else {
            WorldStuff.placeBlockMainHand(this.basePos.add(0, 1, 0));
            WorldStuff.placeBlockMainHand(this.basePos.add(0, 1, 1));
            WorldStuff.placeBlockMainHand(this.basePos.add(0, 1, -1));
        }
        return true;
    }
    private boolean stage1() {
        int skullI = this.getSkull();
        if (skullI != -1) {
            AutoWither.mc.player.inventory.currentItem = skullI;
            if (this.isX()) {
                WorldStuff.placeBlockMainHand(this.basePos.add(0, 2, 0));
                WorldStuff.placeBlockMainHand(this.basePos.add(1, 2, 0));
                WorldStuff.placeBlockMainHand(this.basePos.add(-1, 2, 0));
            } else {
                WorldStuff.placeBlockMainHand(this.basePos.add(0, 2, 0));
                WorldStuff.placeBlockMainHand(this.basePos.add(0, 2, 1));
                WorldStuff.placeBlockMainHand(this.basePos.add(0, 2, -1));
            }
            return true;
        }
        return false;
    }
    private int getSkull() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.getItem().getItemStackDisplayName(stack).equals("Wither Skeleton Skull")) continue;
            return i;
        }
        return -1;
    }

    private boolean isX() {
        EnumFacing facing = AutoWither.mc.player.getHorizontalFacing();
        return facing != EnumFacing.EAST && facing != EnumFacing.WEST;
    }
}
