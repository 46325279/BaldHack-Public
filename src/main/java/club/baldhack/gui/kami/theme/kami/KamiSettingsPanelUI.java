package club.baldhack.gui.kami.theme.kami;

import club.baldhack.gui.kami.RenderHelper;
import club.baldhack.gui.kami.component.SettingsPanel;
import club.baldhack.gui.rgui.render.AbstractComponentUI;
import club.baldhack.gui.rgui.render.font.FontRenderer;
import club.baldhack.module.ModuleManager;
import club.baldhack.module.modules.GUI.GUI;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;

/**
 * Created by 086 on 16/12/2017.
 */
public class KamiSettingsPanelUI extends AbstractComponentUI<SettingsPanel> {

    @Override
    public void renderComponent(SettingsPanel component, FontRenderer fontRenderer) {
        super.renderComponent(component, fontRenderer);
        GL11.glLineWidth(2.0F);
        float red = ((GUI)ModuleManager.getModuleByName("GUI")).Bred.getValue() / 255.0F;
        float green = ((GUI)ModuleManager.getModuleByName("GUI")).Bgreen.getValue() / 255.0F;
        float blue = ((GUI)ModuleManager.getModuleByName("GUI")).Bblue.getValue() / 255.0F;
        if (ModuleManager.getModuleByName("GUI").isEnabled()) {
            GL11.glColor4f(red, green, blue, 0f);
        } else {
            glColor4f(1f,1f,1f,0f);
        }

        RenderHelper.drawFilledRectangle(0.0F, 0.0F, (float)component.getWidth(), (float)component.getHeight());
        glColor3f(1f,1f,1f);
        GL11.glLineWidth(1.5F);
        RenderHelper.drawRectangle(0.0F, 0.0F, (float)component.getWidth(), (float)component.getHeight());
    }
}
