package club.baldhack.module.modules.player;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import club.baldhack.event.events.PacketEvent;
import club.baldhack.module.Module;
import net.minecraft.network.play.client.CPacketConfirmTeleport;

/**
 * Created by GlowskiBroski on 10/14/2018.
 */
@Module.Info(name = "PortalGodMode", category = Module.Category.PLAYER)
public class PortalGodMode extends Module {

    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (isEnabled() && event.getPacket() instanceof CPacketConfirmTeleport) {
            event.cancel();
        }
    });
}
