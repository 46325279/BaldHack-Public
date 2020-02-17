package club.baldhack.module.modules.player;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import club.baldhack.event.events.PacketEvent;
import club.baldhack.module.Module;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

/**
 * Created by 086 on 12/12/2017.
 */
@Module.Info(name = "AntiForceLook", category = Module.Category.PLAYER)
public class AntiForceLook extends Module {

    @EventHandler
    Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            packet.yaw = mc.player.rotationYaw;
            packet.pitch = mc.player.rotationPitch;
        }
    });

}
