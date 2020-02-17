package club.baldhack.gui.kami.theme.kami;

import club.baldhack.BaldHack;
import club.baldhack.gui.kami.*;
import club.baldhack.gui.rgui.GUI;
import club.baldhack.gui.rgui.component.AlignedComponent;
import club.baldhack.gui.rgui.component.Component;
import club.baldhack.gui.rgui.component.container.Container;
import club.baldhack.gui.rgui.component.container.use.Frame;
import club.baldhack.gui.rgui.component.listen.MouseListener;
import club.baldhack.gui.rgui.component.listen.UpdateListener;
import club.baldhack.gui.rgui.poof.use.FramePoof;
import club.baldhack.gui.rgui.render.AbstractComponentUI;
import club.baldhack.gui.rgui.render.font.FontRenderer;
import club.baldhack.gui.rgui.util.ContainerHelper;
import club.baldhack.gui.rgui.util.Docking;
import club.baldhack.module.ModuleManager;
import club.baldhack.module.modules.GUI.*;
import club.baldhack.setting.Setting;
import club.baldhack.util.ColourHolder;
import club.baldhack.util.Wrapper;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 26/06/2017.
 */
public class KamiFrameUI<T extends Frame> extends AbstractComponentUI<Frame> {

    ColourHolder frameColour = KamiGUI.primaryColour.setA(100);
    ColourHolder outlineColour = frameColour.darker();

    Component yLineComponent = null;
    Component xLineComponent = null;
    int xLineOffset = 0;
    public float redForBG;
    public float greenForBG;
    public float blueForBG;
    private static final RootFontRenderer ff = new RootLargeFontRenderer();
    @Override
    public void renderComponent(Frame component, FontRenderer fontRenderer) {
        if (component.getOpacity() == 0)
            return;
        glDisable(GL_TEXTURE_2D);
        float red = ((club.baldhack.module.modules.GUI.GUI) ModuleManager.getModuleByName("GUI")).Bred.getValue() / 255.0F;
        float green = ((club.baldhack.module.modules.GUI.GUI)ModuleManager.getModuleByName("GUI")).Bgreen.getValue() / 255.0F;
        float blue = ((club.baldhack.module.modules.GUI.GUI)ModuleManager.getModuleByName("GUI")).Bblue.getValue() / 255.0F;
        if (ModuleManager.getModuleByName("GUI").isEnabled()) {
            GL11.glColor4f(red, green, blue, 0f);
        } else {
            glColor4f(1f,1f,1f,0f);
        }
        RenderHelper.drawFilledRectangle(0,0,component.getWidth(),component.getHeight());
        glColor3f(1f,1f,1f);
        glLineWidth(1.5f);
        RenderHelper.drawRectangle(0,0,component.getWidth(),component.getHeight());

        GL11.glColor3f(redForBG,greenForBG,blueForBG);
        ff.drawString(component.getWidth() / 2 - ff.getStringWidth(component.getTitle()) / 2, 1, component.getTitle());

        int top_y = 5;
        int bottom_y = component.getTheme().getFontRenderer().getFontHeight() - 9;

        if (component.isCloseable() && component.isMinimizeable()){
            top_y -= 4;
            bottom_y -= 4;
        }

        if (component.isCloseable()){
            glLineWidth(2f);
            glColor3f(1,1,1);
            glBegin(GL_LINES);
            {
                glVertex2d(component.getWidth() - 20, top_y);
                glVertex2d(component.getWidth() - 10, bottom_y);
                glVertex2d(component.getWidth() - 10, top_y);
                glVertex2d(component.getWidth() - 20, bottom_y);
            }
            glEnd();
        }

        if (component.isCloseable() && component.isMinimizeable()){
            top_y += 12;
            bottom_y += 12;
        }

        if (component.isMinimizeable()){
            glLineWidth(1.5f);
            glColor3f(1,1,1);
            if (component.isMinimized()){
                glBegin(GL_LINE_LOOP);
                {
                    glVertex2d(component.getWidth() - 15, top_y+2);
                    glVertex2d(component.getWidth() - 15, bottom_y+3);
                    glVertex2d(component.getWidth() - 10, bottom_y+3);
                    glVertex2d(component.getWidth() - 10, top_y+2);
                }
                glEnd();
            } else {
                glBegin(GL_LINES);
                {
                    glVertex2d(component.getWidth() - 15, bottom_y+4);
                    glVertex2d(component.getWidth() - 10, bottom_y+4);
                }
                glEnd();
            }
        }

        if (component.isPinneable()){
            if (component.isPinned())
                glColor3f(1,.33f,.33f);
            else
                glColor3f(0.66f,0.66f,0.66f);
            RenderHelper.drawCircle(7,4,2f);
            glLineWidth(3f);
            glBegin(GL_LINES);
            {
                glVertex2d(7,4);
                glVertex2d(4,8);
            }
            glEnd();
        }

        if (component.equals(xLineComponent)){
            glColor3f(.44f,.44f,.44f);
            glLineWidth(1f);
            glBegin(GL_LINES);
            {
                glVertex2d(xLineOffset,-GUI.calculateRealPosition(component)[1]);
                glVertex2d(xLineOffset, Wrapper.getMinecraft().displayHeight);
            }
            glEnd();
        }

        if (component.equals(yLineComponent)){
            glColor3f(.44f,.44f,.44f);
            glLineWidth(1f);
            glBegin(GL_LINES);
            {
                glVertex2d(-GUI.calculateRealPosition(component)[0],0);
                glVertex2d(Wrapper.getMinecraft().displayWidth, 0);
            }
            glEnd();
        }

        glDisable(GL_BLEND);
    }

    @Override
    public void handleMouseRelease(Frame component, int x, int y, int button) {
        yLineComponent = null;
        xLineComponent = null;
    }

    @Override
    public void handleMouseDrag(Frame component, int x, int y, int button) {
        super.handleMouseDrag(component, x, y, button);
    }

    @Override
    public void handleAddComponent(Frame component, Container container) {
        super.handleAddComponent(component, container);
        component.setOriginOffsetY(component.getTheme().getFontRenderer().getFontHeight() + 3);
        component.setOriginOffsetX(3);

        component.addMouseListener(new MouseListener() {
            @Override
            public void onMouseDown(MouseButtonEvent event) {
                int y = event.getY();
                int x = event.getX();
                if (y < 0){
                    if (x > component.getWidth() - 22){
                        if (component.isMinimizeable() && component.isCloseable()){
                            if (y > -component.getOriginOffsetY()/2){
                                if (component.isMinimized()){
                                    component.callPoof(FramePoof.class, new FramePoof.FramePoofInfo(FramePoof.Action.MAXIMIZE));
                                }else {
                                    component.callPoof(FramePoof.class, new FramePoof.FramePoofInfo(FramePoof.Action.MINIMIZE));
                                }
                            }else{
                                component.callPoof(FramePoof.class, new FramePoof.FramePoofInfo(FramePoof.Action.CLOSE));
                            }
                        }else{
                            if (component.isMinimized() && component.isMinimizeable()){
                                component.callPoof(FramePoof.class, new FramePoof.FramePoofInfo(FramePoof.Action.MAXIMIZE));
                            }else if (component.isMinimizeable()){
                                component.callPoof(FramePoof.class, new FramePoof.FramePoofInfo(FramePoof.Action.MINIMIZE));
                            }else if (component.isCloseable()) {
                                component.callPoof(FramePoof.class, new FramePoof.FramePoofInfo(FramePoof.Action.CLOSE));
                            }
                        }
                    }
                    if (x < 10 && x > 0){
                        if (component.isPinneable()){
                            component.setPinned(!component.isPinned());
                        }
                    }
                }
            }

            @Override
            public void onMouseRelease(MouseButtonEvent event) {

            }

            @Override
            public void onMouseDrag(MouseButtonEvent event) {

            }

            @Override
            public void onMouseMove(MouseMoveEvent event) {
            }

            @Override
            public void onScroll(MouseScrollEvent event) {

            }
        });

        component.addUpdateListener(new UpdateListener() {
            @Override
            public void updateSize(Component component, int oldWidth, int oldHeight) {
                if (component instanceof Frame) {
                    KamiGUI.dock((Frame) component);
                }
            }
            @Override
            public void updateLocation(Component component, int oldX, int oldY) { }
        });

        component.addPoof(new Frame.FrameDragPoof<Frame, Frame.FrameDragPoof.DragInfo>() {
            @Override
            public void execute(Frame component, DragInfo info) {
                int x = info.getX();
                int y = info.getY();
                yLineComponent = null;
                xLineComponent = null;

                component.setDocking(Docking.NONE);

                KamiGUI rootGUI = BaldHack.getInstance().getGuiManager();
                for (Component c : rootGUI.getChildren()){
                    if (c.equals(component)) continue;
                    /*// Top right
                    int xDiff = Math.abs(c.getX() + c.getWidth() - x);
                    int yDiff = Math.abs(c.getY() - y);
                    if (xDiff < 4 && yDiff < 4){
                        x = c.getX() + c.getWidth();
                        y = c.getY();
                    }

                    // Top left*/
                    int yDiff = Math.abs(y - c.getY());
                    if (yDiff < 4){
                        y = c.getY();
                        yLineComponent = component;
                    }

                    yDiff = Math.abs(y - (c.getY() + c.getHeight() + 3));
                    if (yDiff < 4) {
                        y = c.getY() + c.getHeight();
                        y += 3;
                        yLineComponent = component;
                    }

                    int xDiff = Math.abs((x + component.getWidth()) - (c.getX() + c.getWidth()));
                    if (xDiff < 4){
                        x = c.getX() + c.getWidth();
                        x -= component.getWidth();
                        xLineComponent = component;
                        xLineOffset = component.getWidth();
                    }

                    xDiff = Math.abs(x - c.getX());
                    if (xDiff < 4){
                        x = c.getX();
                        xLineComponent = component;
                        xLineOffset = 0;
                    }

                    xDiff = Math.abs(x - (c.getX() + c.getWidth() + 3));
                    if (xDiff < 4){
                        x = c.getX() + c.getWidth() + 3;
                        xLineComponent = component;
                        xLineOffset = 0;
                    }

                    /*// Bottom
                    xDiff = Math.abs(x - c.getX());
                    yDiff = Math.abs(c.getY() + c.getHeight() - y);
                    if (xDiff < 4 && yDiff < 4){
                        x = c.getX();
                        y = c.getY()+c.getHeight();
                    }*/
                }

                if (x < 5) {
                    x = 0;
                    ContainerHelper.setAlignment(component, AlignedComponent.Alignment.LEFT);
                    component.setDocking(Docking.LEFT);
                }
                int diff = (x+component.getWidth()) * DisplayGuiScreen.getScale() - Wrapper.getMinecraft().displayWidth;
                if (-diff < 5){
                    x = (Wrapper.getMinecraft().displayWidth / DisplayGuiScreen.getScale())-component.getWidth();
                    ContainerHelper.setAlignment(component, AlignedComponent.Alignment.RIGHT);
                    component.setDocking(Docking.RIGHT);
                }

                if (y < 5) {
                    y = 0;
                    if (component.getDocking().equals(Docking.RIGHT))
                        component.setDocking(Docking.TOPRIGHT);
                    else if (component.getDocking().equals(Docking.LEFT))
                        component.setDocking(Docking.TOPLEFT);
                    else
                        component.setDocking(Docking.TOP);
                }

                diff = (y+component.getHeight()) * DisplayGuiScreen.getScale() - Wrapper.getMinecraft().displayHeight;
                if (-diff < 5) {
                    y = (Wrapper.getMinecraft().displayHeight / DisplayGuiScreen.getScale()) - component.getHeight();

                    if (component.getDocking().equals(Docking.RIGHT))
                        component.setDocking(Docking.BOTTOMRIGHT);
                    else if (component.getDocking().equals(Docking.LEFT))
                        component.setDocking(Docking.BOTTOMLEFT);
                    else
                        component.setDocking(Docking.BOTTOM);
                }

                info.setX(x);
                info.setY(y);
            }
        });
    }
}
