package club.baldhack.gui.kami.theme.kami;

import club.baldhack.command.Command;
import club.baldhack.gui.font.CFontRenderer;
import club.baldhack.gui.kami.component.ActiveModules;
import club.baldhack.gui.rgui.component.AlignedComponent;
import club.baldhack.gui.rgui.render.AbstractComponentUI;
import club.baldhack.gui.rgui.render.font.FontRenderer;
import club.baldhack.module.Module;
import club.baldhack.module.ModuleManager;
import club.baldhack.util.Wrapper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glDisable;

/**
 * Created by 086 on 4/08/2017.
 */
public class KamiActiveModulesUI extends AbstractComponentUI<ActiveModules> {
    CFontRenderer cFontRenderer;
    private int argb;

    public KamiActiveModulesUI() {
        this.cFontRenderer = new CFontRenderer(new Font("Verdana", 0, 18), true, false);
    }
    @Override
    public void renderComponent(ActiveModules component, FontRenderer f) {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        FontRenderer renderer = Wrapper.getFontRenderer();
        List<Module> mods = ModuleManager.getModules().stream()
                .filter(Module::isEnabled)
                .sorted(Comparator.comparing(module -> renderer.getStringWidth(module.getName()+(module.getHudInfo()==null?"":module.getHudInfo()+" "))*(component.sort_up?-1:1)))
                .collect(Collectors.toList());

        final int[] y = {2};

        if (component.getParent().getY() < 26 && Wrapper.getPlayer().getActivePotionEffects().size()>0 && component.getParent().getOpacity() == 0)
            y[0] = Math.max(component.getParent().getY(), 26 - component.getParent().getY());

        final float[] hue = {(System.currentTimeMillis() % (360 * 32)) / (360f * 32)};

        boolean lAlign = component.getAlignment() == AlignedComponent.Alignment.LEFT;

        mods.stream().forEach(module -> {
            int rgb = Color.HSBtoRGB(hue[0], 1, 1);
            String s = module.getHudInfo();
            String text = module.getName() + (s==null?"" : " " + Command.SECTIONSIGN() + "7" + s);
            int textwidth = renderer.getStringWidth(text);
            int textheight = renderer.getFontHeight()+1;
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

            renderer.drawStringWithShadow(!lAlign ? (component.getWidth() - textwidth) : 0, y[0], red,green,blue, text);
            hue[0] +=.02f;
            y[0] += textheight;
        });

        component.setHeight(y[0]);

        GL11.glEnable(GL11.GL_CULL_FACE);
        glDisable(GL_BLEND);
    }

    @Override
    public void handleSizeComponent(ActiveModules component) {
        component.setWidth(100);
        component.setHeight(100);
    }
}
