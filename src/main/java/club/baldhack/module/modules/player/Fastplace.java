package club.baldhack.module.modules.player;

import club.baldhack.module.Module;

/**
 * @author 086
 */
@Module.Info(name = "Fastplace", category = Module.Category.PLAYER, description = "Nullifies block place delay")
public class Fastplace extends Module {

    @Override
    public void onUpdate() {
        mc.rightClickDelayTimer = 0;
    }
}
