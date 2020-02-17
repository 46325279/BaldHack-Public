package club.baldhack.module.modules.combat;

import club.baldhack.BaldHack;
import club.baldhack.module.Module;
import club.baldhack.module.ModuleManager;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@Module.Info(name="Indicators", category=Module.Category.COMBAT, description ="Shows Indicators for various things during PvP")
public class Indicators extends Module {
    private Setting<Integer> optionX;
    private Setting<Integer> optionY;
    public Indicators() {
        optionX = register((Setting<Integer>) Settings.integerBuilder("X").withMinimum(0).withValue(125).build());
        optionY = register((Setting<Integer>)Settings.integerBuilder("Y").withMinimum(0).withValue(125).build());
    }
    @Override
    public void onRender() {
        float yCount = (float)this.optionY.getValue();
        float xCount = (float)this.optionX.getValue();
        int color = 20244146;
        int totems = Indicators.mc.player.inventory.mainInventory.stream().filter(itemStack -> {
            if (itemStack.getItem() != Items.TOTEM_OF_UNDYING) return false;
            return true;
        }).mapToInt(ItemStack::getCount).sum();
        if (Indicators.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            ++totems;
        }
            BaldHack.cFontRenderer.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), xCount, yCount - (float)BaldHack.cFontRenderer.getHeight() - 1.0f, color);
            BaldHack.cFontRenderer.drawStringWithShadow("PING: " + (mc.getCurrentServerData() != null ? Long.valueOf(Indicators.mc.getCurrentServerData().pingToServer) : "0"), xCount, (yCount += 10.0f) - (float)BaldHack.cFontRenderer.getHeight() - 1.0f, color);
            BaldHack.cFontRenderer.drawStringWithShadow("TOTEMS: " + totems, xCount, (yCount += 10.0f) - (float)BaldHack.cFontRenderer.getHeight() - 1.0f, color);
            BaldHack.cFontRenderer.drawStringWithShadow("AT: " + this.getAutoTrap(), xCount, (yCount += 10.0f) - (float)BaldHack.cFontRenderer.getHeight() - 1.0f, color);
            BaldHack.cFontRenderer.drawStringWithShadow("SU: " + this.getSurround(), xCount, (yCount += 10.0f) - (float)BaldHack.cFontRenderer.getHeight() - 1.0f, color);
            BaldHack.cFontRenderer.drawStringWithShadow("CA: " + this.getCaura(), xCount, (yCount += 10.0f) - (float)BaldHack.cFontRenderer.getHeight() - 1.0f, color);
            return;
        }

    private String getAutoTrap() {
        String x = "FALSE";
        if (ModuleManager.getModuleByName("AutoTrap") != null) {
            x = Boolean.toString(ModuleManager.getModuleByName("AutoTrap").isEnabled()).toUpperCase();
        }
        if (ModuleManager.getModuleByName("AutoTrap") == null) return x;
        return Boolean.toString(ModuleManager.getModuleByName("AutoTrap").isEnabled()).toUpperCase();
    }

    private String getSurround() {
        String x = "FALSE";
        if (ModuleManager.getModuleByName("AutoObby") != null) {
            x = Boolean.toString(ModuleManager.getModuleByName("AutoObby").isEnabled()).toUpperCase();
        }
        if (ModuleManager.getModuleByName("AutoObby") == null) return x;
        return Boolean.toString(ModuleManager.getModuleByName("AutoObby").isEnabled()).toUpperCase();
    }

    private String getCaura() {
        String x = "FALSE";
        if (ModuleManager.getModuleByName("CrystalAura") != null) {
            x = Boolean.toString(ModuleManager.getModuleByName("CrystalAura").isEnabled()).toUpperCase();
        }
        if (ModuleManager.getModuleByName("CrystalAura") == null) return x;
        return Boolean.toString(ModuleManager.getModuleByName("CrystalAura").isEnabled()).toUpperCase();
    }
}
