package club.baldhack.module.modules.combat;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import club.baldhack.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.text.TextComponentString;
//pasted from backdoored. this is also used in elementars.com cuz i gave elementars baldhack for it but he basically scammed me by banning me but whatever
@Module.Info(name = "str2detect", category = Module.Category.COMBAT, description = "Detects when players have Strength 2")
public class str2detect extends Module {
    private Set<EntityPlayer> str = Collections.newSetFromMap(new WeakHashMap());
    public static final Minecraft mc = Minecraft.getMinecraft();

    public void onUpdate() {
        for (EntityPlayer player : str2detect.mc.world.playerEntities) {
            if (player.equals(str2detect.mc.player)) continue;
            if (player.isPotionActive(MobEffects.STRENGTH) && !this.str.contains(player)) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("\u00A74[BaldHack] " + player.getDisplayNameString() + " has str2"));
                this.str.add(player);
            }
            if (!this.str.contains(player) || player.isPotionActive(MobEffects.STRENGTH)) continue;
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(("\u00A7a[BaldHack] " + player.getDisplayNameString() + " has ran out of str2")));
            this.str.remove(player);
        }
    }
}