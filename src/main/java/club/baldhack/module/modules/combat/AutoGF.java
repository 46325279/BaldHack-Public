package club.baldhack.module.modules.combat;

import club.baldhack.module.*;
import java.util.concurrent.*;
import me.zero.alpine.listener.*;
import club.baldhack.setting.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.*;
import club.baldhack.util.*;
import java.util.function.*;
import net.minecraft.network.play.server.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import club.baldhack.event.events.*;
import net.minecraft.network.play.client.*;
import java.util.*;

@Module.Info(name = "AutoGF", category = Module.Category.COMBAT, description = "ez")
public class AutoGF extends Module
{
    private ConcurrentHashMap<String, Integer> targetedPlayers;
    private Setting<Integer> timeoutTicks;
    @EventHandler
    public Listener<PacketEvent.Send> sendListener;
    @EventHandler
    public Listener<PacketEvent.Receive> receiveListener;

    public AutoGF() {
        targetedPlayers = null;
        timeoutTicks = register(Settings.i("Timeout Ticks", 20));
        final CPacketUseEntity[] cPacketUseEntity = new CPacketUseEntity[1];
        final Entity[] targetEntity = new Entity[1];
        sendListener = new Listener<PacketEvent.Send>(event -> {
            if (AutoGF.mc.player == null) {
                return;
            }
            else {
                if (targetedPlayers == null) {
                    targetedPlayers = new ConcurrentHashMap<String, Integer>();
                }
                if (!(event.getPacket() instanceof CPacketUseEntity)) {
                    return;
                }
                else {
                    cPacketUseEntity[0] = (CPacketUseEntity)event.getPacket();
                    if (!cPacketUseEntity[0].getAction().equals((Object)CPacketUseEntity.Action.ATTACK)) {
                        return;
                    }
                    else {
                        targetEntity[0] = cPacketUseEntity[0].getEntityFromWorld((World)AutoGF.mc.world);
                        if (!EntityUtil.isPlayer(targetEntity[0])) {
                            return;
                        }
                        else {
                            addTargetedPlayer(targetEntity[0].getName());
                            return;
                        }
                    }
                }
            }
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
        final Entity[] entity = new Entity[1];
        final Packet[] packet = new Packet[1];
        final SPacketAnimation[] sPacketAnimation = new SPacketAnimation[1];
        final Entity[] entity2 = new Entity[1];
        final SPacketEntityMetadata[] sPacketEntityMetadata = new SPacketEntityMetadata[1];
        final SPacketEntityProperties[] sPacketEntityProperties = new SPacketEntityProperties[1];
        final SPacketEntityStatus[] sPacketEntityStatus = new SPacketEntityStatus[1];
        final EntityPlayer[] player = new EntityPlayer[1];
        receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (AutoGF.mc.player != null) {
                if (targetedPlayers == null) {
                    targetedPlayers = new ConcurrentHashMap<String, Integer>();
                }
                entity[0] = null;
                packet[0] = event.getPacket();
                if (packet[0] instanceof SPacketAnimation) {
                    sPacketAnimation[0] = (SPacketAnimation) packet[0];
                    entity2[0] = AutoGF.mc.world.getEntityByID(sPacketAnimation[0].getEntityID());
                }
                else if (packet[0] instanceof SPacketEntityMetadata) {
                    sPacketEntityMetadata[0] = (SPacketEntityMetadata) packet[0];
                    entity2[0] = AutoGF.mc.world.getEntityByID(sPacketEntityMetadata[0].getEntityId());
                }
                else if (packet[0] instanceof SPacketEntityProperties) {
                    sPacketEntityProperties[0] = (SPacketEntityProperties) packet[0];
                    entity2[0] = AutoGF.mc.world.getEntityByID(sPacketEntityProperties[0].getEntityId());
                }
                else if (packet[0] instanceof SPacketEntityStatus) {
                    sPacketEntityStatus[0] = (SPacketEntityStatus) packet[0];
                    entity2[0] = sPacketEntityStatus[0].getEntity((World)AutoGF.mc.world);
                }
                else {
                    return;
                }
                if (entity2[0] != null) {
                    if (!(!EntityUtil.isPlayer(entity2[0]))) {
                        player[0] = (EntityPlayer) entity2[0];
                        if (player[0].getHealth() <= 0.0f) {
                            announceInChat(player[0].getName());
                        }
                    }
                }
            }
        }, (Predicate<PacketEvent.Receive>[])new Predicate[0]);
    }

    public void onEnable() {
        targetedPlayers = new ConcurrentHashMap<String, Integer>();
    }

    public void onDisable() {
        targetedPlayers = null;
    }

    @Override
    public void onUpdate() {
        if (isDisabled() || AutoGF.mc.player == null) {
            return;
        }
        if (targetedPlayers == null) {
            targetedPlayers = new ConcurrentHashMap<String, Integer>();
        }
        for (final Entity entity : AutoGF.mc.world.getLoadedEntityList()) {
            if (!EntityUtil.isPlayer(entity)) {
                continue;
            }
            final EntityPlayer player = (EntityPlayer)entity;
            if (player.getHealth() > 0.0f) {
                continue;
            }
            final String name2 = player.getName();
            if (targetedPlayers.containsKey(name2)) {
                announceInChat(name2);
                break;
            }
        }
        targetedPlayers.forEach((name, timeout) -> {
            if (timeout <= 0) {
                targetedPlayers.remove(name);
            }
            else {
                targetedPlayers.put(name, timeout - 1);
            }
        });
    }

    @Override
    public void onWorldRender(final RenderEvent event) {
        if (AutoGF.mc.player == null) {
            return;
        }
        if (targetedPlayers == null) {
            targetedPlayers = new ConcurrentHashMap<String, Integer>();
        }
        AutoGF.mc.world.loadedEntityList.stream().filter(EntityUtil::isPlayer).filter(entity -> AutoGF.mc.player != entity).map(entity -> entity).filter(player -> player.isDead).forEach(player -> announceInChat(player.getName()));
    }

    private void announceInChat(final String name) {
        if (!targetedPlayers.containsKey(name)) {
            return;
        }
        targetedPlayers.remove(name);
        final StringBuilder message = new StringBuilder();
        message.append("GET RAPED WITH THE POWER OF MAH SEXY BALD HEAD!!!");
        message.append(name);
        message.append("!");
        String messageSanitized = message.toString().replaceAll("§", "");
        if (messageSanitized.length() > 255) {
            messageSanitized = messageSanitized.substring(0, 255);
            AutoGF.mc.player.connection.sendPacket((new CPacketChatMessage(messageSanitized)));
        }
    }
    public void addTargetedPlayer(final String name) {
        if (Objects.equals(name, AutoGF.mc.player.getName())) {
            return;
        }
        if (targetedPlayers == null) {
            targetedPlayers = new ConcurrentHashMap<String, Integer>();
        }
        targetedPlayers.put(name, timeoutTicks.getValue());
    }
}
