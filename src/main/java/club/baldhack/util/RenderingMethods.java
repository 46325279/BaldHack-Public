package club.baldhack.util;

import java.awt.Rectangle;

import javax.vecmath.Vector2f;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderingMethods {

    private static float FADING_HUE = 1F;

    public static float getFadingHue() {
        return FADING_HUE;
    }

    public static void drawBorderedRect(final int x, final int y, final int x1, final int y1, final float width, final int internalColor, final int borderColor) {
        enableGL2D();
        Gui.drawRect(x + (int) width, y + (int) width, x1 - (int) width, y1 - (int) width, internalColor);
        Gui.drawRect(x + (int) width, y, x1 - (int) width, y + (int) width, borderColor);
        Gui.drawRect(x, y, x + (int) width, y1, borderColor);
        Gui.drawRect(x1 - (int) width, y, x1, y1, borderColor);
        Gui.drawRect(x + (int) width, y1 - (int) width, x1 - (int) width, y1, borderColor);
        disableGL2D();
    }

    public static void drawBorderedRectGui(final int x, final int y, final int x1, final int y1, final float width, final int internalColor, final int borderColor) {
        enableGL2D();
        Gui.drawRect(x + (int) width, y + (int) width, x1 - (int) width, y1 - (int) width, internalColor);
        Gui.drawRect(x + (int) width - 1, y, x1 - (int) width + 1, y + (int) width, borderColor);
        Gui.drawRect(x, y, x + (int) width, y1, borderColor);
        Gui.drawRect(x1 - (int) width, y, x1, y1, borderColor);
        Gui.drawRect(x + (int) width - 1, y1 - (int) width, x1 - (int) width + 1, y1, borderColor);
        disableGL2D();
    }


    public static void drawBorderedRectReliant(float x, float y, float x1, float y1, float lineWidth, int inside, int border) {
        enableGL2D();
        drawGuiRect(x, y, x1, y1, inside);
        glColor(border);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(3);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        disableGL2D();
    }

    public static void drawBorderedRectReliantGui(double x, double y, double x1, double y1, float lineWidth, int inside, int border) {
        enableGL2D();
        fakeGuiRect(x, y, x1, y1, inside);
        glColor(border);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(3);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y1);
        GL11.glVertex2d(x1, y1 + 0.5);
        GL11.glVertex2d(x1, y);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        disableGL2D();
    }

    public static void renderCircleWithHoleInCenter(Vector2f center, float radiusOuter, float radiusInner, float[] color, float angle, float step) {
        float p1X = (float) (center.x + Math.sin(angle) * radiusOuter);
        float p1Y = (float) (center.y + Math.cos(angle) * radiusOuter);
        float p2X = (float) (center.x + Math.sin(angle) * radiusInner);
        float p2Y = (float) (center.y + Math.cos(angle) * radiusInner);
        float p3X = (float) (center.x + Math.sin(angle + step) * radiusInner);
        float p3Y = (float) (center.y + Math.cos(angle + step) * radiusInner);
        float p4X = (float) (center.x + Math.sin(angle + step) * radiusOuter);
        float p4Y = (float) (center.y + Math.cos(angle + step) * radiusOuter);
        float alpha = color[3];
        float red = color[0];
        float blue = color[1];
        float green = color[2];
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.endVertex();
        worldRenderer.pos(p1X, p1Y, 0.0D);
        worldRenderer.pos(p2X, p2Y, 0.0D);
        worldRenderer.pos(p3X, p3Y, 0.0D);
        worldRenderer.pos(p4X, p4Y, 0.0D);
    }

    public static void drawHoloRect(double x, double y, double x1, double y1, float lineWidth, int color) {
        enableGL2D();
        drawGuiRect(x + (int) lineWidth - 1, y, x1 - (int) lineWidth + 1, y + (int) lineWidth, color);
        drawGuiRect(x, y, x + (int) lineWidth, y1, color);
        drawGuiRect(x1 - (int) lineWidth, y, x1, y1, color);
        drawGuiRect(x + (int) lineWidth - 1, y1 - (int) lineWidth, x1 - (int) lineWidth + 1, y1, color);
        disableGL2D();
    }


    public static void drawRect(Rectangle rectangle, int color) {
        drawRect(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, color);
    }

    public static void drawRect(float x, float y, float x1, float y1, int color) {
        enableGL2D();
        glColor(color);
        drawRect(x, y, x1, y1);
        disableGL2D();
    }

    public static void drawRectFourColor(double d, double e, double f, double g, float red, float green, float blue, float alpha) {
        enableGL2D();
        GL11.glColor4f(red, green, blue, alpha);
        drawRectDouble(d, e, f, g);
        disableGL2D();
    }

    public static void drawRectDouble(double x, double y, double x1, double y1, int color) {
        enableGL2D();
        glColor(color);
        drawRectDouble(x, y, x1, y1);
        disableGL2D();
    }

    public static void drawRectDoubleJavaColor(double x, double y, double x1, double y1, int red, int green, int blue) {
        enableGL2D();
        GL11.glColor3f(red, green, blue);
        drawRectDouble(x, y, x1, y1);
        disableGL2D();
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void drawRect(float x, float y, float x1, float y1) {
        GL11.glBegin(7);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }

    public static void drawRectDouble(double x, double y, double x1, double y1) {
        GL11.glBegin(7);
        GL11.glVertex2d(x, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
    }

    public static void drawRectDoublePlayerESP(double x, double y, double x1, double y1, int paramColor) {
        float alpha = (paramColor >> 24 & 0xFF) / 255.0F;
        float red = (paramColor >> 16 & 0xFF) / 255.0F;
        float green = (paramColor >> 8 & 0xFF) / 255.0F;
        float blue = (paramColor & 0xFF) / 255.0F;
        GL11.glBegin(7);
        GL11.glVertex2d(x, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
    }

    public static void enableGL3D(float lineWidth) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(lineWidth);
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void enableGL3D() {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4353);
        GL11.glDisable(2896);
    }

    public static void disableGL3D() {
        GL11.glEnable(2896);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawBorderedRect(int x, int y, int x1, int y1, final int insideC, final int borderC) {
        enableGL2D();
        x *= 2.0f;
        x1 *= 2.0f;
        y *= 2.0f;
        y1 *= 2.0f;
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawVLine(x, y, y1 - 1, borderC);
        drawVLine(x1 - 1, y, y1, borderC);
        drawHLine(x, x1 - 1, y, borderC);
        drawHLine(x, x1 - 2, y1 - 1, borderC);
        Gui.drawRect(x + 1, y + 1, x1 - 1, y1 - 1, insideC);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        disableGL2D();
    }

    public static void drawHLine(int x, int y, final int x1, final int y1) {
        if (y < x) {
            final int var5 = x;
            x = y;
            y = var5;
        }
        Gui.drawRect(x, x1, y + 1, x1 + 1, y1);
    }

    public static void drawVLine(final int x, int y, int x1, final int y1) {
        if (x1 < y) {
            final int var5 = y;
            y = x1;
            x1 = var5;
        }
        Gui.drawRect(x, y + 1, x + 1, x1, y1);
    }

    public static void drawHLine(float x, float y, final float x1, final int y1, final int y2) {
        if (y < x) {
            final float var5 = x;
            x = y;
            y = var5;
        }
        drawGradientRect(x, x1, y + 1.0f, x1 + 1.0f, y1, y2);
    }


    public static void drawTracerLine(double[] pos, float[] c, float width) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(width);
        GL11.glColor4f(c[0], c[1], c[2], c[3]);
        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(0, Minecraft.getMinecraft().player.getEyeHeight(), 0);
            GL11.glVertex3d(pos[0], pos[1], pos[2]);
        }
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        float var7 = (float) (startColor >> 24 & 255) / 255.0F;
        float var8 = (float) (startColor >> 16 & 255) / 255.0F;
        float var9 = (float) (startColor >> 8 & 255) / 255.0F;
        float var10 = (float) (startColor & 255) / 255.0F;
        float var11 = (float) (endColor >> 24 & 255) / 255.0F;
        float var12 = (float) (endColor >> 16 & 255) / 255.0F;
        float var13 = (float) (endColor >> 8 & 255) / 255.0F;
        float var14 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator var15 = Tessellator.getInstance();
        BufferBuilder var16 = var15.getBuffer();
        var16.endVertex();
        var16.color(var8, var9, var10, var7);
        var16.pos(right, top, 0);
        var16.pos(left, top, 0);
        var16.color(var12, var13, var14, var11);
        var16.pos(left, bottom, 0);
        var16.pos(right, bottom, 0);
        var15.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawCircle(final int x, final int y, final float radius, final int color) {
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(9);
        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex2d(x + Math.sin(i * 3.141592653589793 / 180.0) * radius, y + Math.cos(i * 3.141592653589793 / 180.0) * radius);
        }
        GL11.glEnd();
    }

    public static void drawUnfilledCircle(final int x, final int y, final float radius, final float lineWidth, final int color) {
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(2848);
        GL11.glBegin(2);
        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex2d(x + Math.sin(i * 3.141592653589793 / 180.0) * radius, y + Math.cos(i * 3.141592653589793 / 180.0) * radius);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
    }


    public static void drawFilledCircle(int x, int y, double r, int c) {
        float f = ((c >> 24) & 0xff) / 255F;
        float f1 = ((c >> 16) & 0xff) / 255F;
        float f2 = ((c >> 8) & 0xff) / 255F;
        float f3 = (c & 0xff) / 255F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360; i++) {
            double x2 = Math.sin(((i * Math.PI) / 180)) * r;
            double y2 = Math.cos(((i * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void drawCircle(int x, int y, double radius, int color) {
        float f = ((color >> 24) & 0xff) / 255F;
        float f1 = ((color >> 16) & 0xff) / 255F;
        float f2 = ((color >> 8) & 0xff) / 255F;
        float f3 = (color & 0xff) / 255F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = 0; i <= 360; i++) {
            double x2 = Math.sin(((i * Math.PI) / 180)) * radius;
            double y2 = Math.cos(((i * Math.PI) / 180)) * radius;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void wolfRamCircle(final float x, final float y, final float radius, final float lineWidth, final int color) {
        GL11.glEnable(3042);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0f);
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, (alpha == 0.0f) ? 1.0f : alpha);
        GL11.glLineWidth(lineWidth);
        final int vertices = (int) Math.min(Math.max(radius, 45.0f), 360.0f);
        GL11.glBegin(2);
        for (int i = 0; i < vertices; ++i) {
            final double angleRadians = 6.283185307179586 * i / vertices;
            GL11.glVertex2d(x + Math.sin(angleRadians) * radius, y + Math.cos(angleRadians) * radius);
        }
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void wolfRamFilledCircle(final float x, final float y, final float radius, final int color) {
        GL11.glEnable(3042);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0f);
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, (alpha == 0.0f) ? 1.0f : alpha);
        final int vertices = (int) Math.min(Math.max(radius, 45.0f), 360.0f);
        GL11.glBegin(9);
        for (int i = 0; i < vertices; ++i) {
            final double angleRadians = 6.283185307179586 * i / vertices;
            GL11.glVertex2d(x + Math.sin(angleRadians) * radius, y + Math.cos(angleRadians) * radius);
        }
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        wolfRamCircle(x, y, radius, 1.5f, 16777215);
    }

    public static void drawGuiRect(double x1, double y1, double x2, double y2, int color) {
        float red = (color >> 24 & 0xFF) / 255.0F;
        float green = (color >> 16 & 0xFF) / 255.0F;
        float blue = (color >> 8 & 0xFF) / 255.0F;
        float alpha = (color & 0xFF) / 255.0F;

        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);

        GL11.glPushMatrix();
        GL11.glColor4f(green, blue, alpha, red);
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);

    }

    public static void fakeGuiRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}