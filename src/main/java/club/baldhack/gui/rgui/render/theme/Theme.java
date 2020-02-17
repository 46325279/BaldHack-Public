package club.baldhack.gui.rgui.render.theme;

import club.baldhack.gui.rgui.component.Component;
import club.baldhack.gui.rgui.render.ComponentUI;
import club.baldhack.gui.rgui.render.font.FontRenderer;

/**
 * Created by 086 on 25/06/2017.
 */
public interface Theme {
    public ComponentUI getUIForComponent(Component component);
    public FontRenderer getFontRenderer();
}
