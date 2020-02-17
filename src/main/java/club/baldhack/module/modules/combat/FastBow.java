package club.baldhack.module.modules.combat;

import club.baldhack.module.Module;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.math.BlockPos;

@Module.Info(name = "FastBow", category = Module.Category.COMBAT)
public class FastBow extends Module {
    public void onUpdate() {
        if (mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }

    }
}