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


import club.baldhack.module.Module;
import club.baldhack.module.ModuleManager;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

@Module.Info(name="OffhandGap", category=Module.Category.COMBAT, description="Auto Offhand Gapple")
public class OffhandGap
extends Module {
    private int gapples;
    private boolean moving = false;
    private boolean returnI = false;
    private Setting<Boolean> soft = this.register(Settings.b("Soft", false));
    private Setting<Boolean> totemOnDisable = this.register(Settings.b("TotemOnDisable", true));
    private Setting<TotemMode> totemMode = this.register(Settings.enumBuilder(TotemMode.class).withName("TotemMode").withValue(TotemMode.KAMI).withVisibility(v -> this.totemOnDisable.getValue()).build());

    @Override
    public void onEnable() {
        if (ModuleManager.getModuleByName("AutoTotem").isEnabled()) {
            ModuleManager.getModuleByName("AutoTotem").disable();
        }
        if (!ModuleManager.getModuleByName("AutoTotemDev").isEnabled()) return;
        ModuleManager.getModuleByName("AutoTotemDev").disable();
    }

    @Override
    public void onDisable() {
        if (!this.totemOnDisable.getValue().booleanValue()) {
            return;
        }
        if (this.totemMode.getValue().equals((TotemMode)TotemMode.KAMI)) {
            AutoTotem autoTotem = (AutoTotem)ModuleManager.getModuleByName("AutoTotem");
            autoTotem.disableSoft();
            if (autoTotem.isDisabled()) {
                autoTotem.enable();
            }
        }
        if (!this.totemMode.getValue().equals((TotemMode)TotemMode.ASIMOV)) return;
        AutoTotemDev autoTotemDev = (AutoTotemDev)ModuleManager.getModuleByName("AutoTotemDev");
        autoTotemDev.disableSoft();
        if (!autoTotemDev.isDisabled()) return;
        autoTotemDev.enable();
    }

    @Override
    public void onUpdate() {
        int i;
        int t;
        if (OffhandGap.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.returnI) {
            t = -1;
            for (i = 0; i < 45; ++i) {
                if (!OffhandGap.mc.player.inventory.getStackInSlot((int)i).isEmpty) continue;
                t = i;
                break;
            }
            if (t == -1) {
                return;
            }
            OffhandGap.mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, (EntityPlayer)OffhandGap.mc.player);
            this.returnI = false;
        }
        this.gapples = OffhandGap.mc.player.inventory.mainInventory.stream().filter(itemStack -> {
            if (itemStack.getItem() != Items.GOLDEN_APPLE) return false;
            return true;
        }).mapToInt(ItemStack::getCount).sum();
        if (OffhandGap.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            ++this.gapples;
            return;
        }
        if (this.soft.getValue().booleanValue() && !OffhandGap.mc.player.getHeldItemOffhand().isEmpty) {
            return;
        }
        if (this.moving) {
            OffhandGap.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)OffhandGap.mc.player);
            this.moving = false;
            if (OffhandGap.mc.player.inventory.itemStack.isEmpty()) return;
            this.returnI = true;
            return;
        }
        if (!OffhandGap.mc.player.inventory.itemStack.isEmpty()) {
            if (this.soft.getValue() != false) return;
            t = -1;
        } else {
            if (this.gapples == 0) {
                return;
            }
            t = -1;
            for (i = 0; i < 45; ++i) {
                if (OffhandGap.mc.player.inventory.getStackInSlot(i).getItem() != Items.GOLDEN_APPLE) continue;
                t = i;
                break;
            }
            if (t == -1) {
                return;
            }
            OffhandGap.mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, (EntityPlayer)OffhandGap.mc.player);
            this.moving = true;
            return;
        }
        for (i = 0; i < 45; ++i) {
            if (!OffhandGap.mc.player.inventory.getStackInSlot((int)i).isEmpty) continue;
            t = i;
            break;
        }
        if (t == -1) {
            return;
        }
        OffhandGap.mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, (EntityPlayer)OffhandGap.mc.player);
    }

    @Override
    public String getHudInfo() {
        return String.valueOf(this.gapples);
    }

    private static enum TotemMode {
        KAMI,
        ASIMOV;
        
    }

}

