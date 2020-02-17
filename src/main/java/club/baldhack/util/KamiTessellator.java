//
// Decompiled by Procyon v0.5.36
//

package club.baldhack.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;

public class KamiTessellator extends Tessellator
{
    public static KamiTessellator INSTANCE;

    public KamiTessellator() {
        super(2097152);
    }

    public static void prepare(final int mode) {
        prepareGL();
        begin(mode);
    }

    public static void prepareGL() {
        GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(1.5f);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
    }

    public static void begin(final int mode) {
        KamiTessellator.INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void release() {
        render();
        releaseGL();
    }

    public static void render() {
        KamiTessellator.INSTANCE.draw();
    }

    public static void releaseGL() {
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
    }

    public static void drawBox(final BlockPos blockPos, final int argb, final int sides) {
        final int a = argb >>> 24 & 0xFF;
        final int r = argb >>> 16 & 0xFF;
        final int g = argb >>> 8 & 0xFF;
        final int b = argb & 0xFF;
        drawBox(blockPos, r, g, b, a, sides);
    }

    public static void drawBox(final float x, final float y, final float z, final int argb, final int sides) {
        final int a = argb >>> 24 & 0xFF;
        final int r = argb >>> 16 & 0xFF;
        final int g = argb >>> 8 & 0xFF;
        final int b = argb & 0xFF;
        drawBox(KamiTessellator.INSTANCE.getBuffer(), x, y, z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawBox(final BlockPos blockPos, final int r, final int g, final int b, final int a, final int sides) {
        drawBox(KamiTessellator.INSTANCE.getBuffer(), (float)blockPos.x, (float)blockPos.y, (float)blockPos.z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static BufferBuilder getBufferBuilder() {
        return KamiTessellator.INSTANCE.getBuffer();
    }

    public static void drawBox(final BufferBuilder buffer, final float x, final float y, final float z, final float w, final float h, final float d, final int r, final int g, final int b, final int a, final int sides) {
        if ((sides & 0x1) != 0x0) {
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x2) != 0x0) {
            buffer.pos((double)(x + w), (double)(y + h), (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)(y + h), (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x4) != 0x0) {
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)(y + h), (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)(y + h), (double)z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x8) != 0x0) {
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x10) != 0x0) {
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)(y + h), (double)z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x20) != 0x0) {
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)(y + h), (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
        }
    }

    public static void drawFace(final BlockPos blockPos, final int argb, final int sides) {
        final int a = argb >>> 24 & 0xFF;
        final int r = argb >>> 16 & 0xFF;
        final int g = argb >>> 8 & 0xFF;
        final int b = argb & 0xFF;
        drawFace(blockPos, r, g, b, a, sides);
    }

    public static void drawFace(final BlockPos blockPos, final int r, final int g, final int b, final int a, final int sides) {
        drawFace(KamiTessellator.INSTANCE.getBuffer(), (float)blockPos.x, (float)blockPos.y, (float)blockPos.z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawFace(final BufferBuilder buffer, final float x, final float y, final float z, final float w, final float h, final float d, final int r, final int g, final int b, final int a, final int sides) {
        if ((sides & 0x1) != 0x0) {
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
        }
    }

    public static void drawFaceOutline(final BlockPos blockPos, final int r, final int g, final int b, final int a, final int sides) {
        drawFaceOutline(KamiTessellator.INSTANCE.getBuffer(), (float)blockPos.x, (float)blockPos.y, (float)blockPos.z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawFaceOutline(final BufferBuilder buffer, final float x, final float y, final float z, final float w, final float h, final float d, final int r, final int g, final int b, final int a, final int sides) {
        if ((sides & 0x1) != 0x0) {
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, z + 0.02).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, z + 0.02).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos(x + 0.02, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos(x + 0.02, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, z + d - 0.02).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, z + d - 0.02).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos(x + w - 0.02, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos(x + w - 0.02, (double)y, (double)z).color(r, g, b, a).endVertex();
        }
    }

    public static void drawBoxOutline(final BlockPos blockPos, final int argb, final int sides) {
        final int a = argb >>> 24 & 0xFF;
        final int r = argb >>> 16 & 0xFF;
        final int g = argb >>> 8 & 0xFF;
        final int b = argb & 0xFF;
        drawBoxOutline(blockPos, r, g, b, a, sides);
    }

    public static void drawBoxOutline(final float x, final float y, final float z, final int argb, final int sides) {
        final int a = argb >>> 24 & 0xFF;
        final int r = argb >>> 16 & 0xFF;
        final int g = argb >>> 8 & 0xFF;
        final int b = argb & 0xFF;
        drawBoxOutline(KamiTessellator.INSTANCE.getBuffer(), x, y, z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawBoxOutline(final BlockPos blockPos, final int r, final int g, final int b, final int a, final int sides) {
        drawBoxOutline(KamiTessellator.INSTANCE.getBuffer(), (float)blockPos.x, (float)blockPos.y, (float)blockPos.z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawBoxOutline(final BufferBuilder buffer, final float x, final float y, final float z, final float w, final float h, final float d, final int r, final int g, final int b, final int a, final int sides) {
        if ((sides & 0x1) != 0x0) {
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, z + 0.02).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, z + 0.02).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos(x + 0.02, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos(x + 0.02, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, z + d - 0.02).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, z + d - 0.02).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos(x + w - 0.02, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos(x + w - 0.02, (double)y, (double)z).color(r, g, b, a).endVertex();
        }
    }

    public static void drawLines(final BlockPos blockPos, final int argb, final int sides) {
        final int a = argb >>> 24 & 0xFF;
        final int r = argb >>> 16 & 0xFF;
        final int g = argb >>> 8 & 0xFF;
        final int b = argb & 0xFF;
        drawLines(blockPos, r, g, b, a, sides);
    }

    public static void drawLines(final BlockPos blockPos, final int r, final int g, final int b, final int a, final int sides) {
        drawLines(KamiTessellator.INSTANCE.getBuffer(), (float)blockPos.x, (float)blockPos.y, (float)blockPos.z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
    }

    public static void drawLines(final BufferBuilder buffer, final float x, final float y, final float z, final float w, final float h, final float d, final int r, final int g, final int b, final int a, final int sides) {
        if ((sides & 0x11) != 0x0) {
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x12) != 0x0) {
            buffer.pos((double)x, (double)(y + h), (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x21) != 0x0) {
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x22) != 0x0) {
            buffer.pos((double)(x + w), (double)(y + h), (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x5) != 0x0) {
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x6) != 0x0) {
            buffer.pos((double)x, (double)(y + h), (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)(y + h), (double)z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x9) != 0x0) {
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
        }
        if ((sides & 0xA) != 0x0) {
            buffer.pos((double)x, (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x14) != 0x0) {
            buffer.pos((double)x, (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)(y + h), (double)z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x24) != 0x0) {
            buffer.pos((double)(x + w), (double)y, (double)z).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)(y + h), (double)z).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x18) != 0x0) {
            buffer.pos((double)x, (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)x, (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
        }
        if ((sides & 0x28) != 0x0) {
            buffer.pos((double)(x + w), (double)y, (double)(z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double)(x + w), (double)(y + h), (double)(z + d)).color(r, g, b, a).endVertex();
        }
    }

    public static void drawFullBox(final AxisAlignedBB bb, final BlockPos blockPos, final float width, final int red, final int green, final int blue, final int alpha) {
        prepare(7);
        drawBox(blockPos, red, green, blue, alpha, 63);
        release();
        drawBoundingBox(bb, width, red, green, blue, 150);
    }

    public static void drawBoundingBox(final AxisAlignedBB bb, final float width, final int argb) {
        final int a = argb >>> 24 & 0xFF;
        final int r = argb >>> 16 & 0xFF;
        final int g = argb >>> 8 & 0xFF;
        final int b = argb & 0xFF;
        drawBoundingBox(bb, width, r, g, b, a);
    }

    public static void drawBoundingBox(final AxisAlignedBB bb, final float width, final int red, final int green, final int blue, final int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxFace(final AxisAlignedBB bb, final float width, final int red, final int green, final int blue, final int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    static {
        KamiTessellator.INSTANCE = new KamiTessellator();
    }
}
