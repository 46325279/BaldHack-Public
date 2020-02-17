package club.baldhack.gui.kami.theme.kami;

import club.baldhack.gui.kami.RenderHelper;
import club.baldhack.gui.rgui.component.container.Container;
import club.baldhack.gui.rgui.component.use.InputField;
import club.baldhack.gui.rgui.render.AbstractComponentUI;
import club.baldhack.gui.rgui.render.font.FontRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by 086 on 30/06/2017.
 */
public class RootInputFieldUI<T extends InputField> extends AbstractComponentUI<InputField> {

    @Override
    public void renderComponent(InputField component, FontRenderer fontRenderer) {
//        glColor3f(1,0.22f,0.22f);
//        RenderHelper.drawOutlinedRoundedRectangle(0,0,component.getWidth(),component.getHeight(),6f);
        GL11.glColor3f(0.33f,0.22f,0.22f);
        RenderHelper.drawFilledRectangle(0,0,component.getWidth(),component.getHeight());
        GL11.glLineWidth(1.5f);
        GL11.glColor4f(1,0.33f,0.33f,0.6f);
        RenderHelper.drawRectangle(0,0,component.getWidth(),component.getHeight());
    }

    @Override
    public void handleAddComponent(InputField component, Container container) {
        component.setWidth(200);
        component.setHeight(component.getTheme().getFontRenderer().getFontHeight());
    }
}
