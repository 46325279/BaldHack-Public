package club.baldhack.module.modules.misc;

import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import club.baldhack.module.Module;

/**
 * Created by 086 on 8/04/2018.
 */
@Module.Info(name = "NoEntityTrace", category = Module.Category.MISC, description = "Blocks entities from stopping you from mining")
public class NoEntityTrace extends Module {

    private Setting<TraceMode> mode = register(Settings.e("Mode", TraceMode.DYNAMIC));

    private static NoEntityTrace INSTANCE;

    public NoEntityTrace() {
        NoEntityTrace.INSTANCE = this;
    }

    public static boolean shouldBlock() {
        return INSTANCE.isEnabled() && (INSTANCE.mode.getValue() == TraceMode.STATIC || mc.playerController.isHittingBlock);
    }

    private enum TraceMode {
        STATIC, DYNAMIC
    }
}
