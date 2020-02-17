package club.baldhack.util;

import club.baldhack.gui.kami.KamiGUI;
import club.baldhack.gui.rgui.render.font.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import net.minecraft.network.Packet;
import org.lwjgl.input.Keyboard;

public class Wrapper {

    private static FontRenderer fontRenderer;

    public static void init() {
//      fontRenderer = new CFontRenderer(new Font("Segoe UI", Font.PLAIN, 19), true, false);
        fontRenderer = KamiGUI.fontRenderer;
    }
    public static EntityPlayerSP player() { return (mc()).player; }
    public static Minecraft mc() { return Minecraft.getMinecraft(); }
    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }
    public static EntityPlayerSP getPlayer() {
        return getMinecraft().player;
    }
    public static World getWorld() {
        return getMinecraft().world;
    }
    public static int getKey(String keyname){
        return Keyboard.getKeyIndex(keyname.toUpperCase());
    }
    public static void sendPacket(Packet p) { (player()).connection.sendPacket(p); }
    public static FontRenderer getFontRenderer() {
        return fontRenderer;
    }
}
