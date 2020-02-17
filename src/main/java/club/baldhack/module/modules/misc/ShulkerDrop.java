package club.baldhack.module.modules.misc;

import club.baldhack.module.Module;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;

@Module.Info(name = "ShulkerDrop", description = "Anti Book Ban", category = Module.Category.MISC)
public class ShulkerDrop extends Module {

	public void onUpdate() {
		for (int i = 0; i < 9; i++) {
			ItemStack item = mc.player.inventory.getStackInSlot(i);
			if (item == null) {
				continue;
			}
			if (item.getItem() instanceof ItemShulkerBox) {
				mc.player.inventory.dropAllItems();
			}
		}

	}
}
