package club.baldhack.module.modules.misc;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import club.baldhack.event.events.PacketEvent;
import club.baldhack.module.Module;
import club.baldhack.module.Module.Info;
import club.baldhack.util.Wrapper;
import net.minecraft.network.play.server.SPacketChat;

@Info(name = "AutoReply", category = Module.Category.MISC, description = "automatically replies to messages")
public class AutoReply extends Module {
  @EventHandler
  Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
          if (event.getPacket() instanceof SPacketChat && ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText().contains("whispers:"))
            Wrapper.getPlayer().sendChatMessage("/r BaldHack on top");
        });
    }