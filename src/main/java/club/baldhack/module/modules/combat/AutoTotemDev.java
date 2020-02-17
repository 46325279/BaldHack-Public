/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  net.minecraft.client.multiplayer.PlayerControllerMP
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.NonNullList
 */
package club.baldhack.module.modules.combat;

import club.baldhack.module.Module;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Module.Info(name="AutoTotemDev", category=Module.Category.COMBAT, description="Auto Totem")
public class AutoTotemDev
extends Module {
    private int numOfTotems;
    private int preferredTotemSlot;
    private Setting<Boolean> soft = this.register(Settings.b("Soft", false));
    private Setting<Boolean> pauseInContainers = this.register(Settings.b("PauseInContainers", true));
    private Setting<Boolean> pauseInInventory = this.register(Settings.b("PauseInInventory", true));

    @Override
    public void onUpdate() {
        if (AutoTotemDev.mc.player == null) {
            return;
        }
        if (!this.findTotems()) {
            return;
        }
        if (this.pauseInContainers.getValue().booleanValue() && AutoTotemDev.mc.currentScreen instanceof GuiContainer && !(AutoTotemDev.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (this.pauseInInventory.getValue().booleanValue() && AutoTotemDev.mc.currentScreen instanceof GuiInventory && AutoTotemDev.mc.currentScreen instanceof GuiInventory) {
            return;
        }
        if (this.soft.getValue().booleanValue()) {
            if (!AutoTotemDev.mc.player.getHeldItemOffhand().getItem().equals((Object)Items.AIR)) return;
            AutoTotemDev.mc.playerController.windowClick(0, this.preferredTotemSlot, 0, ClickType.PICKUP, (EntityPlayer)AutoTotemDev.mc.player);
            AutoTotemDev.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)AutoTotemDev.mc.player);
            AutoTotemDev.mc.playerController.updateController();
            return;
        }
        if (AutoTotemDev.mc.player.getHeldItemOffhand().getItem().equals((Object)Items.TOTEM_OF_UNDYING)) return;
        boolean offhandEmptyPreSwitch = false;
        if (AutoTotemDev.mc.player.getHeldItemOffhand().getItem().equals((Object)Items.AIR)) {
            offhandEmptyPreSwitch = true;
        }
        AutoTotemDev.mc.playerController.windowClick(0, this.preferredTotemSlot, 0, ClickType.PICKUP, (EntityPlayer)AutoTotemDev.mc.player);
        AutoTotemDev.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)AutoTotemDev.mc.player);
        if (!offhandEmptyPreSwitch) {
            AutoTotemDev.mc.playerController.windowClick(0, this.preferredTotemSlot, 0, ClickType.PICKUP, (EntityPlayer)AutoTotemDev.mc.player);
        }
        AutoTotemDev.mc.playerController.updateController();
    }

    private boolean findTotems() {
        this.numOfTotems = 0;
        AtomicInteger preferredTotemSlotStackSize = new AtomicInteger();
        preferredTotemSlotStackSize.set(Integer.MIN_VALUE);
        AutoTotemDev.getInventoryAndHotbarSlots().forEach((slotKey, slotValue) -> {
            int numOfTotemsInStack = 0;
            if (slotValue.getItem().equals((Object)Items.TOTEM_OF_UNDYING)) {
                numOfTotemsInStack = slotValue.getCount();
                if (preferredTotemSlotStackSize.get() < numOfTotemsInStack) {
                    preferredTotemSlotStackSize.set(numOfTotemsInStack);
                    this.preferredTotemSlot = slotKey;
                }
            }
            this.numOfTotems += numOfTotemsInStack;
        });
        if (AutoTotemDev.mc.player.getHeldItemOffhand().getItem().equals((Object)Items.TOTEM_OF_UNDYING)) {
            this.numOfTotems += AutoTotemDev.mc.player.getHeldItemOffhand().getCount();
        }
        if (this.numOfTotems == 0) return false;
        return true;
    }

    private static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        return AutoTotemDev.getInventorySlots(9, 44);
    }

    private static Map<Integer, ItemStack> getInventorySlots(int current, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        while (current <= last) {
            fullInventorySlots.put(current, (ItemStack)AutoTotemDev.mc.player.inventoryContainer.getInventory().get(current));
            ++current;
        }
        return fullInventorySlots;
    }

    public void disableSoft() {
        this.soft.setValue(false);
    }
}

