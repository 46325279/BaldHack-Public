/*
 * Decompiled with CFR <Could not determine version>.
 *
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.multiplayer.PlayerControllerMP
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.NonNullList
 */
package club.baldhack.module.modules.combat;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;


import club.baldhack.module.Module;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

@Module.Info(name="AutoTotem", category=Module.Category.COMBAT)
public class AutoTotem
        extends Module {
    int totems;
    boolean moving = false;
    boolean returnI = false;
    private Setting<Boolean> soft = this.register(Settings.b("Soft"));

    @Override
    public void onUpdate() {
        int i;
        int t;
        if (AutoTotem.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.returnI) {
            t = -1;
            for (i = 0; i < 45; ++i) {
                if (!AutoTotem.mc.player.inventory.getStackInSlot((int)i).isEmpty) continue;
                t = i;
                break;
            }
            if (t == -1) {
                return;
            }
            AutoTotem.mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, (EntityPlayer)AutoTotem.mc.player);
            this.returnI = false;
        }
        this.totems = AutoTotem.mc.player.inventory.mainInventory.stream().filter(itemStack -> {
            if (itemStack.getItem() != Items.TOTEM_OF_UNDYING) return false;
            return true;
        }).mapToInt(ItemStack::getCount).sum();
        if (AutoTotem.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            ++this.totems;
            return;
        }
        if (this.soft.getValue().booleanValue() && !AutoTotem.mc.player.getHeldItemOffhand().isEmpty) {
            return;
        }
        if (this.moving) {
            AutoTotem.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)AutoTotem.mc.player);
            this.moving = false;
            if (AutoTotem.mc.player.inventory.itemStack.isEmpty()) return;
            this.returnI = true;
            return;
        }
        if (!AutoTotem.mc.player.inventory.itemStack.isEmpty()) {
            if (this.soft.getValue() != false) return;
            t = -1;
        } else {
            if (this.totems == 0) {
                return;
            }
            t = -1;
            for (i = 0; i < 45; ++i) {
                if (AutoTotem.mc.player.inventory.getStackInSlot(i).getItem() != Items.TOTEM_OF_UNDYING) continue;
                t = i;
                break;
            }
            if (t == -1) {
                return;
            }
            AutoTotem.mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, (EntityPlayer)AutoTotem.mc.player);
            this.moving = true;
            return;
        }
        for (i = 0; i < 45; ++i) {
            if (!AutoTotem.mc.player.inventory.getStackInSlot((int)i).isEmpty) continue;
            t = i;
            break;
        }
        if (t == -1) {
            return;
        }
        AutoTotem.mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, (EntityPlayer)AutoTotem.mc.player);
    }

    public void disableSoft() {
        this.soft.setValue(false);
    }

    @Override
    public String getHudInfo() {
        return String.valueOf(this.totems);
    }
}

