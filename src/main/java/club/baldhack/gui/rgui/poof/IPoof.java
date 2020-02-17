package club.baldhack.gui.rgui.poof;

import club.baldhack.gui.rgui.component.Component;

/**
 * Created by 086 on 21/07/2017.
 */
public interface IPoof<T extends Component, S extends PoofInfo> {
    public void execute(T component, S info);
    public Class getComponentClass();
    public Class getInfoClass();
}
