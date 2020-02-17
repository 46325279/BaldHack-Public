package club.baldhack.module.modules.render;

import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import club.baldhack.event.events.PacketEvent;
import club.baldhack.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;

/**
 * Created by 086 on 4/02/2018.
 */
@Module.Info(name = "NoRender", category = Module.Category.RENDER, description = "Ignore entity spawn packets")
public class NoRender extends Module {

    private Setting<Boolean> mob = register(Settings.b("Mob"));
    private Setting<Boolean> gentity = register(Settings.b("GEntity"));
    private Setting<Boolean> object = register(Settings.b("Object"));
    private Setting<Boolean> xp = register(Settings.b("XP"));
    private Setting<Boolean> paint = register(Settings.b("Paintings"));

    @EventHandler
    public Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        Packet packet = event.getPacket();
        if ((packet instanceof SPacketSpawnMob && mob.getValue()) ||
                (packet instanceof SPacketSpawnGlobalEntity && gentity.getValue()) ||
                (packet instanceof SPacketSpawnObject && object.getValue()) ||
                (packet instanceof SPacketSpawnExperienceOrb && xp.getValue()) ||
                (packet instanceof SPacketSpawnPainting && paint.getValue()))
            event.cancel();
    });

}
