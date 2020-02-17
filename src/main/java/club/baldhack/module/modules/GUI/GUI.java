package club.baldhack.module.modules.GUI;

import club.baldhack.module.Module;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import club.baldhack.util.ColourUtils;
// pasted from elementars.com
@Module.Info(name = "GUI", category = Module.Category.GUI)
public class GUI extends Module {
    public Setting<Boolean> Rainbow;
    public Setting<Boolean> RainbowWatermark;
    public Setting<Integer> Ared;
    public Setting<Integer> Agreen;
    public Setting<Integer> Ablue;
    public Setting<Float> Bred;
    public Setting<Float> Bgreen;
    public Setting<Float> Bblue;

    public GUI() {
        RainbowWatermark = register(Settings.booleanBuilder("Rainbow Watermark").withValue(false).build());
        Ared = register((Setting<Integer>)Settings.integerBuilder("Red").withRange(0, 255).withValue(0).build());
        Agreen = register((Setting<Integer>)Settings.integerBuilder("Green").withRange(0, 255).withValue(0).build());
        Ablue = register((Setting<Integer>)Settings.integerBuilder("Blue").withRange(0, 255).withValue(255).build());
    }

    public int getArgb() {
        final int argb = ColourUtils.toRGBA(Ared.getValue(), Agreen.getValue(), Ablue.getValue(), 255);
        return argb;
    }
}
