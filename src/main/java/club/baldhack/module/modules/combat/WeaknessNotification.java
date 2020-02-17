package club.baldhack.module.modules.combat;
import club.baldhack.util.printMsg;
import club.baldhack.module.Module;
import net.minecraft.init.MobEffects;

@Module.Info(name = "WeaknessNotification", category = Module.Category.COMBAT, description = "Detects when you have weakness arrows")
public class WeaknessNotification extends Module {

	public void onEnable() {
		if (mc.player.isPotionActive(MobEffects.WEAKNESS)) {
			printMsg.printMsg("\u00A74[BaldHack] I have weakness! :<");
		}
	}
}
