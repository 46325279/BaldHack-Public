package club.baldhack.module.modules.combat;

import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import club.baldhack.module.Module;
import club.baldhack.module.ModuleManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.Entity;

/**
 * Created by 086 on 9/04/2018.
 */
@Module.Info(name = "AntiEndCrystal", description = "Automatically log when in danger or on low health", category = Module.Category.COMBAT)
public class AntiEndCrystal extends Module {

    private Setting<Integer> health = register(Settings.integerBuilder("Health").withRange(0, 36).withValue(6).build());
    private boolean shouldLog = false;
    long lastLog = System.currentTimeMillis();

    public void onUpdate() {

		for (Entity e : mc.world.loadedEntityList) {
			if (e instanceof EntityEnderCrystal) {
				if (mc.player.getDistanceSq(e) <= 3.8f  && e.isEntityAlive()) {
					mc.player.connection.getNetworkManager()
							.closeChannel(new TextComponentString("ENDER CRYSTAL IN RANGE"));
					break;
				}
			}
		}
	}
    private void log() {
        ModuleManager.getModuleByName("AutoReconnect").disable();
        shouldLog = true;
        lastLog = System.currentTimeMillis();
    }

}
