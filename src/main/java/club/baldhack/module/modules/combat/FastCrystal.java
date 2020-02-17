package club.baldhack.module.modules.combat;



import net.minecraft.init.Items;
import me.zero.alpine.listener.EventHandler;
import club.baldhack.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import club.baldhack.module.Module;


@Module.Info(name = "FastCrystal", category = Module.Category.COMBAT, description = "Allows faster crystal placement")
public class FastCrystal extends Module {
    @EventHandler
    private Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive>(event -> {
        if (FastCrystal.mc.player != null && (FastCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || FastCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)) {
            FastCrystal.mc.rightClickDelayTimer = 0;
        }
    }
    );}