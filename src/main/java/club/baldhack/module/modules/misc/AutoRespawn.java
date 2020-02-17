package club.baldhack.module.modules.misc;

import club.baldhack.command.Command;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import club.baldhack.event.events.GuiScreenEvent;
import club.baldhack.module.Module;
import net.minecraft.client.gui.GuiGameOver;

/**
 * Created by 086 on 9/04/2018.
 */
@Module.Info(name = "AutoRespawn", description = "Automatically respawns upon death and tells you where you died", category = Module.Category.MISC)
public class AutoRespawn extends Module {

    private Setting<Boolean> deathCoords = register(Settings.b("DeathCoords", false));
    private Setting<Boolean> respawn = register(Settings.b("Respawn", true));

    @EventHandler
    public Listener<GuiScreenEvent.Displayed> listener = new Listener<>(event -> {
        if (event.getScreen() instanceof GuiGameOver) {
            if (deathCoords.getValue())
                Command.sendChatMessage(String.format("You died at x %d y %d z %d", (int)mc.player.posX, (int)mc.player.posY, (int)mc.player.posZ));

            if (respawn.getValue()) {
                mc.player.respawnPlayer();
                mc.displayGuiScreen(null);
            }
        }
    });

}
