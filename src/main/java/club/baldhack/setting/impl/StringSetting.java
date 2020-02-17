package club.baldhack.setting.impl;

import club.baldhack.setting.converter.StringConverter;
import club.baldhack.setting.AbstractSetting;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Created by 086 on 12/10/2018.
 */
public class StringSetting extends AbstractSetting<String> {

    private static final StringConverter converter = new StringConverter();

    public StringSetting(String value, Predicate<String> restriction, BiConsumer<String, String> consumer, String name, Predicate<String> visibilityPredicate) {
        super(value, restriction, consumer, name, visibilityPredicate);
    }

    @Override
    public StringConverter converter() {
        return converter;
    }

}
