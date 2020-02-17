package club.baldhack.module.modules.misc;

import club.baldhack.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
@Module.Info(name = "FakeCreative", description = "Fake Creative", category = Module.Category.MISC)
public class FakeCreative extends Module{


	public void onUpdate() {
		Minecraft.getMinecraft();
		mc.playerController.setGameType(GameType.CREATIVE);
	}

	public void onEnable() {
		mc.player.sendMessage((new TextComponentString("�7�o[Server: Opped " + mc.player.getName() + "]")));
	}

	public void onDisable() {
		mc.playerController.setGameType(GameType.SURVIVAL);
	}
}

