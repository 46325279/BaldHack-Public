package club.baldhack.module.modules.combat;

import club.baldhack.module.Module;
import net.minecraft.item.ItemExpBottle;

/**
 * Made by Meedou thx rusherhack :^)
 */
@Module.Info(name = "FastEXP", category = Module.Category.COMBAT, description = "Allows you to oil up faster!")
public class FastExp extends Module {

    @Override
    public void onUpdate() {
		if (mc.player.inventory.getCurrentItem().getItem() instanceof ItemExpBottle) {
			 mc.rightClickDelayTimer = 0;
		}

	}
}