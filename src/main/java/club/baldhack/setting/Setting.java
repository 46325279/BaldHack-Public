package club.baldhack.setting;

/**
 * Created by 086 on 12/10/2018.
 */
public abstract class Setting<T> implements ISetting<T> {
    String name;
    T value;

    public Setting(T value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean setValue(T value) {
        this.value = value;
        return true;
    }

    public String getName() {
        return this.name;
    }
}
