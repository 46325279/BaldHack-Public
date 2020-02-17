package club.baldhack.module.modules.misc;

import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import club.baldhack.event.events.PacketEvent;
import club.baldhack.module.Module;
import net.minecraft.network.play.client.CPacketChatMessage;

/**
 * Created by 086 on 8/04/2018.
 */
@Module.Info(name = "BaldHackChat", category = Module.Category.MISC, description = "Modifies your chat messages")
public class BaldHackChat extends Module {
    private Setting<Boolean> commands = register(Settings.b("Commands", false));

    private final String KAMI_SUFFIX = " \u23D0 \u0299\u1D00\u029F\u1D05\u029C\u1D00\u1D04\u1D0B";

    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") && !commands.getValue()) return;
            s += KAMI_SUFFIX;
            if (s.length() >= 256) s = s.substring(0,256);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    });

}
