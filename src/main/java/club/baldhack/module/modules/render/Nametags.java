package club.baldhack.module.modules.render;

import club.baldhack.command.Command;
import club.baldhack.event.events.RenderEvent;
import club.baldhack.module.Module;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import club.baldhack.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 19/12/2017.
 */
@Module.Info(name = "Nametags", description = "Draws descriptive nametags above entities", category = Module.Category.RENDER)
public class Nametags extends Module {

    private Setting<Boolean> players = register(Settings.b("Players", true));
    private Setting<Boolean> animals = register(Settings.b("Animals", false));
    private Setting<Boolean> mobs = register(Settings.b("Mobs", false));
    private Setting<Double> range = register(Settings.d("Range", 200));
    private Setting<Float> scale = register(Settings.floatBuilder("Scale").withMinimum(.5f).withMaximum(10f).withValue(1f).build());
    private Setting<Boolean> health = register(Settings.b("Health", true));
    private Setting<Boolean> armor = register(Settings.b("Armor", true));
    private Setting<Boolean> enchantments = register(Settings.b("Enchantments", true));

    @Override
    public void onWorldRender(RenderEvent event) {
        Minecraft.getMinecraft().world.loadedEntityList.stream()
                .filter(EntityUtil::isLiving)
                .filter(entity -> !EntityUtil.isFakeLocalPlayer(entity))
                .filter(entity -> (entity instanceof EntityPlayer ? players.getValue() && mc.player != entity : (EntityUtil.isPassive(entity) ? animals.getValue() : mobs.getValue())))
                .filter(entity -> mc.player.getDistance(entity) < range.getValue())
                .sorted(Comparator.comparing(entity -> -mc.player.getDistance(entity)))
                .forEach(entity -> {
                    if (entity != mc.getRenderViewEntity() && entity.isEntityAlive() && mc.player.getDistance(entity) < range.getValue()) {
                        double pX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ReflectionFields.getTimer().renderPartialTicks
                                - ReflectionFields.getRenderPosX();
                        double pY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ReflectionFields.getTimer().renderPartialTicks
                                - ReflectionFields.getRenderPosY();
                        double pZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ReflectionFields.getTimer().renderPartialTicks
                                - ReflectionFields.getRenderPosZ();
                        if (!entity.getName().startsWith("Body #")) {
                            renderNametag(entity, pX, pY, pZ);
                        }
                    }
                });
    }

    public void renderNametag(Entity entity, double x, double y, double z) {
        int l4 = 0;
        GL11.glPushMatrix();
        FontRenderer fr = mc.fontRenderer;
        String name = entity.getName() + TextFormatting.RED + (health.getValue() ? " " + Command.SECTIONSIGN() + "c" + Math.round(((EntityLivingBase) entity).getHealth() + (entity instanceof EntityPlayer ? ((EntityPlayer) entity).getAbsorptionAmount() : 0)) : "");
        name = name.replace(".0", "");
        float distance = mc.player.getDistance(entity);
        float var15 = (distance / 5 <= 2 ? 2.0F : distance / 5) * 2.5f;
        float var14 = 0.016666668F * getNametagSize(entity);

        GL11.glTranslated((float) x, (float) y + 2.5D, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-ReflectionFields.getPlayerViewY(), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(ReflectionFields.getPlayerViewX(), 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-var14, -var14, var14);

        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GL11.glDisable(2929);
        int width = fr.getStringWidth(name) / 2;

        Friends.initFriends();
        drawBorderedRect(-width - 2, 10, width + 1, 20, 0,
                Friends.isFriend(name) ? new Color(0, 130, 130).getRGB() : 0xff000000, -1);
        fr.drawString(name, -width, 11, -1);

        if (armor.getValue()) {
            ArrayList<ItemStack> equipment = new ArrayList<>();
            entity.getHeldEquipment().forEach(itemStack -> {
                if (itemStack != null) equipment.add(itemStack);
            });
            ArrayList<ItemStack> armour = new ArrayList<>();
            entity.getArmorInventoryList().forEach(itemStack -> {
                if (itemStack != null) armour.add(itemStack);
            });
            Collections.reverse(armour);
            equipment.addAll(armour);

            Collection<ItemStack> a = equipment.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());

            if (armour.size() != 0) {
                int xOffset = 0;
                for (ItemStack armourStack : armour) {
                    if (armourStack != null) {
                        xOffset -= 8;
                    }
                }

                Object renderStack;
                if (equipment.get(0) != null) {
                    xOffset -= 8;
                    renderStack = equipment.get(0).copy();
                    renderItem(entity, (ItemStack) renderStack, xOffset, -10);
                    xOffset += 16;
                }
                for (int index = 3; index >= 0; --index) {
                    ItemStack armourStack = armour.get(index);
                    if (armourStack != null) {
                        ItemStack renderStack1 = armourStack.copy();

                        renderItem(entity, renderStack1, xOffset, -10);
                        xOffset += 16;
                    }
                }

                Object renderOffhand;
                if (equipment.get(1) != null) {
                    xOffset -= 0;
                    renderOffhand = equipment.get(1).copy();

                    renderItem(entity, (ItemStack) renderOffhand, xOffset, -10);
                    xOffset += 8;
                }
            }
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    public float getNametagSize(Entity entity) {
        // return mc.thePlayer.getDistanceToEntity(player) / 4.0F <= 2.0F ? 2.0F
        // : mc.thePlayer.getDistanceToEntity(player) / 4.0F;

        float f = mc.player.getDistance(entity);
        float m = (f / 8f) * (float) (Math.pow(1.2589254f, this.scale.getValue()));

        return m;
    }

    public void drawBorderRect(float left, float top, float right, float bottom, int bcolor, int icolor, float f) {
        RenderingMethods.drawGuiRect(left + f, top + f, right - f, bottom - f, icolor);
        RenderingMethods.drawGuiRect(left, top, left + f, bottom, bcolor);
        RenderingMethods.drawGuiRect(left + f, top, right, top + f, bcolor);
        RenderingMethods.drawGuiRect(left + f, bottom - f, right, bottom, bcolor);
        RenderingMethods.drawGuiRect(right - f, top + f, right, bottom - f, bcolor);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    // man i love doubles!
    public static void drawBorderedRect(double x, double y, double x1, double y1, double width, int internalColor,
                                        int borderColor) {
        enableGL2D();
        RenderingMethods.fakeGuiRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        RenderingMethods.fakeGuiRect(x + width, y, x1 - width, y + width, borderColor);
        RenderingMethods.fakeGuiRect(x, y, x + width, y1, borderColor);
        RenderingMethods.fakeGuiRect(x1 - width, y, x1, y1, borderColor);
        RenderingMethods.fakeGuiRect(x + width, y1 - width, x1 - width, y1, borderColor);
        disableGL2D();
    }

    public void renderItem(Entity entity, ItemStack stack, int x, int y) {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);

        GlStateManager.disableDepth();
        GlStateManager.enableDepth();

        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -100.0F;
        GlStateManager.scale(1, 1, 0.01f);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, (y / 2) - 12);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, (y / 2) - 12);
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.scale(1, 1, 1);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        GlStateManager.disableDepth();
        if(enchantments.getValue()) {
            renderEnchantText(entity, stack, x, y - 18);
        }
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GL11.glPopMatrix();
    }

    public void renderEnchantText(Entity entity, ItemStack stack, int x, int y) {
        int encY = y - 24;
        int yCount = encY - -5;
        if (stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword
                || stack.getItem() instanceof ItemTool) {
            mc.fontRenderer.drawStringWithShadow(stack.getMaxDamage() - stack.getItemDamage() + "", x * 2 + 8, y + 26,
                    -1);
        }
        NBTTagList enchants = stack.getEnchantmentTagList();
        if (enchants != null) {
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                short level = enchants.getCompoundTagAt(index).getShort("lvl");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    String encName = enc.isCurse()
                            ? TextFormatting.RED
                            + enc.getTranslatedName(level).substring(11).substring(0, 1).toLowerCase()
                            : enc.getTranslatedName(level).substring(0, 1).toLowerCase();
                    encName = encName + level;
                    GL11.glPushMatrix();
                    GL11.glScalef(0.9f, 0.9f, 0);
                    mc.fontRenderer.drawStringWithShadow(encName, x * 2 + 13, yCount, -1);
                    GL11.glScalef(1f, 1f, 1);
                    GL11.glPopMatrix();
                    encY += 8;
                    yCount -= 10;
                }
            }
        }
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

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
//    RenderItem itemRenderer = mc.getRenderItem();
//
//    @Override
//    public void onWorldRender(RenderEvent event) {
//        if (mc.getRenderManager().options == null) return;
//
//        GlStateManager.enableTexture2D();
//        GlStateManager.disableLighting();
//        GlStateManager.disableDepth();
//        Minecraft.getMinecraft().world.loadedEntityList.stream()
//                .filter(EntityUtil::isLiving)
//                .filter(entity -> !EntityUtil.isFakeLocalPlayer(entity))
//                .filter(entity -> (entity instanceof EntityPlayer ? players.getValue() && mc.player != entity : (EntityUtil.isPassive(entity) ? animals.getValue() : mobs.getValue())))
//                .filter(entity -> mc.player.getDistance(entity) < range.getValue())
//                .sorted(Comparator.comparing(entity -> -mc.player.getDistance(entity)))
//                .forEach(this::drawNametag);
//        GlStateManager.disableTexture2D();
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.enableLighting();
//        GlStateManager.enableDepth();
//    }
//
//    private void drawNametag(Entity entityIn) {
//        GlStateManager.pushMatrix();
//
//        Vec3d interp = EntityUtil.getInterpolatedRenderPos(entityIn, mc.getRenderPartialTicks());
//        float yAdd = entityIn.height + 0.5F - (entityIn.isSneaking() ? 0.25F : 0.0F);
//        double x = interp.x;
//        double y = interp.y + yAdd;
//        double z = interp.z;
//
//        float viewerYaw = mc.getRenderManager().playerViewY;
//        float viewerPitch = mc.getRenderManager().playerViewX;
//        boolean isThirdPersonFrontal = mc.getRenderManager().options.thirdPersonView == 2;
//        GlStateManager.translate(x, y, z);
//        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
//        GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
//
//        float f = mc.player.getDistance(entityIn);
//        float m = (f / 8f) * (float) (Math.pow(1.2589254f, this.scale.getValue()));
//        GlStateManager.scale(m, m, m);
//
//        FontRenderer fontRendererIn = mc.fontRenderer;
//        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
//
//        String str = entityIn.getName() + (health.getValue() ? " " + Command.SECTIONSIGN() + "c" + Math.round(((EntityLivingBase) entityIn).getHealth() + (entityIn instanceof EntityPlayer ? ((EntityPlayer) entityIn).getAbsorptionAmount() : 0)) : "");
//        int i = fontRendererIn.getStringWidth(str) / 2;
//        GlStateManager.enableBlend();
//        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//        GlStateManager.disableTexture2D();
//        Tessellator tessellator = Tessellator.getInstance();
//
//        BufferBuilder bufferbuilder = tessellator.getBuffer();
//
//        glTranslatef(0, -20, 0);
//        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
//        bufferbuilder.pos(-i - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
//        bufferbuilder.pos(-i - 1, 19, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
//        bufferbuilder.pos(i + 1, 19, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
//        bufferbuilder.pos(i + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
//        tessellator.draw();
//
//        bufferbuilder.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
//        bufferbuilder.pos(-i - 1, 8, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
//        bufferbuilder.pos(-i - 1, 19, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
//        bufferbuilder.pos(i + 1, 19, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
//        bufferbuilder.pos(i + 1, 8, 0.0D).color(.1f, .1f, .1f, .1f).endVertex();
//        tessellator.draw();
//
//        GlStateManager.enableTexture2D();
//
//        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
//        fontRendererIn.drawString(str, -i, 10, entityIn instanceof EntityPlayer ? Friends.isFriend(entityIn.getName()) ? 0x11ee11 : 0xffffff : 0xffffff);
//        GlStateManager.glNormal3f(0.0F, 0.0F, 0.0F);
//        glTranslatef(0, 20, 0);
//
//        GlStateManager.scale(-40, -40, 40);
//
//        ArrayList<ItemStack> equipment = new ArrayList<>();
//        entityIn.getHeldEquipment().forEach(itemStack -> {
//            if (itemStack != null) equipment.add(itemStack);
//        });
//        ArrayList<ItemStack> armour = new ArrayList<>();
//        entityIn.getArmorInventoryList().forEach(itemStack -> {
//            if (itemStack != null) armour.add(itemStack);
//        });
//        Collections.reverse(armour);
//        equipment.addAll(armour);
//        if (equipment.size() == 0) {
//            GlStateManager.popMatrix();
//            return;
//        }
//
//        Collection<ItemStack> a = equipment.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
//        GlStateManager.translate(((a.size() - 1) / 2f) * .5f, .6, 0);
//
//        a.forEach(itemStack -> {
//            GlStateManager.pushAttrib();
//            RenderHelper.enableStandardItemLighting();
//            GlStateManager.scale(.5, .5, 0);
//            GlStateManager.disableLighting();
//            this.itemRenderer.zLevel = -5;
//
//            this.itemRenderer.renderItem(itemStack, itemStack.getItem() == Items.SHIELD ? ItemCameraTransforms.TransformType.FIXED : ItemCameraTransforms.TransformType.NONE);
//
//            this.itemRenderer.zLevel = 0;
//            GlStateManager.scale(2, 2, 0);
//            GlStateManager.popAttrib();
//            GlStateManager.translate(-.5f, 0, 0);
//
//            GlStateManager.scale(1, 1, 1);
//            RenderHelper.disableStandardItemLighting();
//            GlStateManager.enableAlpha();
//            GlStateManager.disableBlend();
//            GlStateManager.scale(0.5, 0.5, 0.5);
//
//            if(enchantments.getValue()) {
//                float encY = (float) y - 24;
//                float yCount = encY - -5;
//                NBTTagList enchants = itemStack.getEnchantmentTagList();
//                if (enchants != null) {
//                    for (int index = 0; index < enchants.tagCount(); ++index) {
//                        short id = enchants.getCompoundTagAt(index).getShort("id");
//                        short level = enchants.getCompoundTagAt(index).getShort("lvl");
//                        Enchantment enc = Enchantment.getEnchantmentByID(id);
//                        if (enc != null) {
//                            String encName = enc.isCurse()
//                                    ? TextFormatting.RED
//                                    + enc.getTranslatedName(level).substring(11).substring(0, 1).toLowerCase()
//                                    : enc.getTranslatedName(level).substring(0, 1).toLowerCase();
//                            encName = encName + level;
//                            GL11.glPushMatrix();
//                            GL11.glScalef(0.9f, 0.9f, 0);
//                            fontRendererIn.drawStringWithShadow(encName, (float) x * 2 + 13, yCount, -1);
//                            GL11.glScalef(1f, 1f, 1);
//                            GL11.glPopMatrix();
//                            GlStateManager.pushMatrix();
//                            encY += 8;
//                            yCount -= 10;
//                        }
//                    }
//                }
//            }
//
//            GlStateManager.enableDepth();
//            GlStateManager.enableBlend();
//            GlStateManager.scale(2.0, 2.0, 2.0);
//            GL11.glPopMatrix();
//        });
//
//        GlStateManager.popMatrix();
//    }
}