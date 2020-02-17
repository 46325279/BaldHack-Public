package club.baldhack.module.modules.misc;

import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import club.baldhack.event.events.GuiScreenEvent;
import club.baldhack.module.Module;
import net.minecraft.client.gui.GuiGameOver;

//made by meedou nerdddddddddddddddddddddddddddddddddddddddd
@Module.Info(name = "AutoGG", description = "Automatically shit talks in chat after you die", category = Module.Category.MISC)
public class AutoGG extends Module {

    private Setting<Boolean> respawn = register(Settings.b("Respawn", true));

    @EventHandler
    public Listener<GuiScreenEvent.Displayed> listener = new Listener<>(event -> {
        if (event.getScreen() instanceof GuiGameOver) {
            if (respawn.getValue()) {
                mc.player.respawnPlayer();
                mc.displayGuiScreen(null);
                mc.player.sendChatMessage("lag killed me not you skid");
            }
        }
    });

}