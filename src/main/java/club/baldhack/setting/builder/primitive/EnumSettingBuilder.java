package club.baldhack.setting.builder.primitive;

import club.baldhack.setting.Setting;
import club.baldhack.setting.builder.SettingBuilder;
import club.baldhack.setting.impl.EnumSetting;

/**
 * Created by 086 on 14/10/2018.
 */
public class EnumSettingBuilder<T extends Enum> extends SettingBuilder<T> {
    Class<? extends Enum> clazz;

    public EnumSettingBuilder(Class<? extends Enum> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Setting<T> build() {
        return new EnumSetting<>(initialValue, predicate(), consumer(), name, visibilityPredicate(), clazz);
    }
}
