package club.baldhack.setting.impl;

import club.baldhack.setting.converter.EnumConverter;
import com.google.common.base.Converter;
import club.baldhack.setting.AbstractSetting;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Created by 086 on 14/10/2018.
 */
public class EnumSetting<T extends Enum> extends AbstractSetting<T> {

    private EnumConverter converter;
    public final Class<? extends Enum> clazz;

    public EnumSetting(T value, Predicate<T> restriction, BiConsumer<T, T> consumer, String name, Predicate<T> visibilityPredicate, Class<? extends Enum> clazz) {
        super(value, restriction, consumer, name, visibilityPredicate);
        this.converter = new EnumConverter(clazz);
        this.clazz = clazz;
    }

    @Override
    public Converter converter() {
        return converter;
    }

}
