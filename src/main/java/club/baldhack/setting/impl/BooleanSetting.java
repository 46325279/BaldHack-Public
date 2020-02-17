package club.baldhack.setting.impl;

import club.baldhack.setting.converter.BooleanConverter;
import club.baldhack.setting.AbstractSetting;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Created by 086 on 12/10/2018.
 */
public class BooleanSetting extends AbstractSetting<Boolean> {

    private static final BooleanConverter converter = new BooleanConverter();

    public BooleanSetting(Boolean value, Predicate<Boolean> restriction, BiConsumer<Boolean, Boolean> consumer, String name, Predicate<Boolean> visibilityPredicate) {
        super(value, restriction, consumer, name, visibilityPredicate);
    }

    @Override
    public BooleanConverter converter() {
        return converter;
    }

}
