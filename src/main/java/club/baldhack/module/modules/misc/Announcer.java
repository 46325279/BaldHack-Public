package club.baldhack.module.modules.misc;

import java.text.*;
import net.minecraftforge.event.entity.living.*;
import club.baldhack.event.events.*;
import me.zero.alpine.listener.*;
import club.baldhack.setting.*;
import club.baldhack.module.*;
import net.minecraft.client.gui.*;
import java.util.function.*;
import net.minecraft.network.play.server.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import java.math.*;
import java.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.util.math.*;
import net.minecraft.block.*;
import java.util.concurrent.*;

@Module.Info(name = "Announcer", category = Module.Category.MISC, description = "Announcer")
public class Announcer extends Module
{
    private static boolean isFirstRun;
    private static Queue<String> messageQueue;
    private static Map<String, Integer> minedBlocks;
    private static Map<String, Integer> placedBlocks;
    private static Map<String, Integer> droppedItems;
    private static Map<String, Integer> consumedItems;
    private static DecimalFormat df;
    private static TimerTask timerTask;
    private static Timer timer;
    private static PacketEvent.Receive lastEventReceive;
    private static PacketEvent.Send lastEventSend;
    private static LivingEntityUseItemEvent.Finish lastLivingEntityUseFinishEvent;
    private static GuiScreenEvent.Displayed lastGuiScreenDisplayedEvent;
    private static String lastmessage;
    private static Vec3d thisTickPos;
    private static Vec3d lastTickPos;
    private static double distanceTraveled;
    private static float thisTickHealth;
    private static float lastTickHealth;
    private static float gainedHealth;
    private static float lostHealth;
    private Setting<Boolean> distance;
    private Setting<Integer> mindistance;
    private Setting<Integer> maxdistance;
    private Setting<Boolean> blocks;
    private Setting<Boolean> items;
    private Setting<Boolean> playerheal;
    private Setting<Boolean> playerdamage;
    private Setting<Boolean> playerdeath;
    private Setting<Boolean> greentext;
    private Setting<Integer> delay;
    private Setting<Integer> queuesize;
    private Setting<Boolean> clearqueue;
    @EventHandler
    public Listener<GuiScreenEvent.Displayed> guiScreenEventDisplayedlistener;
    @EventHandler
    private Listener<PacketEvent.Receive> packetEventReceiveListener;
    @EventHandler
    private Listener<PacketEvent.Send> packetEventSendListener;
    @EventHandler
    public Listener<LivingEntityUseItemEvent.Finish> listener;

    public Announcer() {
        distance = register(Settings.b("Distance", true));
        mindistance = register((Setting<Integer>)Settings.integerBuilder("Min Distance").withRange(1, 100).withValue(10).build());
        maxdistance = register((Setting<Integer>)Settings.integerBuilder("Max Distance").withRange(100, 10000).withValue(150).build());
        blocks = register(Settings.b("Blocks", true));
        items = register(Settings.b("Items", true));
        playerheal = register(Settings.b("Player Heal", true));
        playerdamage = register(Settings.b("Player Damage", true));
        playerdeath = register(Settings.b("Death", true));
        greentext = register(Settings.b("Greentext", false));
        delay = register((Setting<Integer>)Settings.integerBuilder("Send Delay").withRange(1, 10).withValue(2).build());
        queuesize = register((Setting<Integer>)Settings.integerBuilder("Queue Size").withRange(1, 100).withValue(5).build());
        clearqueue = register(Settings.b("Clear Queue", false));
        guiScreenEventDisplayedlistener = new Listener<GuiScreenEvent.Displayed>(event -> {
            if (isDisabled() || Announcer.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
                return;
            }
            else if (Announcer.lastGuiScreenDisplayedEvent != null && Announcer.lastGuiScreenDisplayedEvent.equals(event)) {
                return;
            }
            else if (playerdeath.getValue() && event.getScreen() instanceof GuiGameOver) {
                queueMessage("lag killed me not you skid");
                return;
            }
            else {
                Announcer.lastGuiScreenDisplayedEvent = event;
                return;
            }
        }, (Predicate<GuiScreenEvent.Displayed>[])new Predicate[0]);
        packetEventReceiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (isDisabled() || Announcer.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
                return;
            }
            else if (Announcer.lastEventReceive != null && Announcer.lastEventReceive.equals(event)) {
                return;
            }
            else if (event.getPacket() instanceof SPacketUseBed) {
                queueMessage("I used a Bed!");
                Announcer.lastEventReceive = event;
                return;
            }
            else {
                return;
            }
        });
        final CPacketPlayerDigging[] p = new CPacketPlayerDigging[1];
        final String[] name = new String[1];
        final String[] name2 = new String[1];
        final CPacketUpdateSign[] p2 = new CPacketUpdateSign[1];
        final CPacketPlayerTryUseItemOnBlock[] p3 = new CPacketPlayerTryUseItemOnBlock[1];
        final ItemStack[] itemStack = new ItemStack[1];
        final String[] name3 = new String[1];
        packetEventSendListener = new Listener<PacketEvent.Send>(event -> {
            if (isDisabled() || Announcer.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
                return;
            }
            else if (Announcer.lastEventSend != null && Announcer.lastEventSend.equals(event)) {
                return;
            }
            else {
                if ((items.getValue() || blocks.getValue()) && event.getPacket() instanceof CPacketPlayerDigging) {
                    p[0] = (CPacketPlayerDigging)event.getPacket();
                    if (items.getValue() && Announcer.mc.player.getHeldItemMainhand().getItem() != Items.AIR && (p[0].getAction().equals(CPacketPlayerDigging.Action.DROP_ITEM) || p[0].getAction().equals(CPacketPlayerDigging.Action.DROP_ALL_ITEMS))) {
                        name[0] = Announcer.mc.player.inventory.getCurrentItem().getDisplayName();
                        if (Announcer.droppedItems.containsKey(name[0])) {
                            Announcer.droppedItems.put(name[0], Announcer.droppedItems.get(name[0]) + 1);
                        }
                        else {
                            Announcer.droppedItems.put(name[0], 1);
                        }
                        Announcer.lastEventSend = event;
                    }
                    else if (blocks.getValue() && p[0].getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                        name2[0] = Announcer.mc.world.getBlockState(p[0].getPosition()).getBlock().getLocalizedName();
                        if (Announcer.minedBlocks.containsKey(name2[0])) {
                            Announcer.minedBlocks.put(name2[0], Announcer.minedBlocks.get(name2[0]) + 1);
                        }
                        else {
                            Announcer.minedBlocks.put(name2[0], 1);
                        }
                        Announcer.lastEventSend = event;
                    }
                }
                else if (items.getValue() && event.getPacket() instanceof CPacketUpdateSign) {
                    p2[0] = (CPacketUpdateSign)event.getPacket();
                    queueMessage("i wrote a sign thanks to the power of Mc_Ren's sexy bald head");
                    Announcer.lastEventSend = event;
                }
                else if (blocks.getValue() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                    p3[0] = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
                    itemStack[0] = Announcer.mc.player.inventory.getCurrentItem();
                    if (itemStack[0].isEmpty) {
                        Announcer.lastEventSend = event;
                    }
                    else if (itemStack[0].getItem() instanceof ItemBlock) {
                        name3[0] = Announcer.mc.player.inventory.getCurrentItem().getDisplayName();
                        if (Announcer.placedBlocks.containsKey(name3[0])) {
                            Announcer.placedBlocks.put(name3[0], Announcer.placedBlocks.get(name3[0]) + 1);
                        }
                        else {
                            Announcer.placedBlocks.put(name3[0], 1);
                        }
                        Announcer.lastEventSend = event;
                    }
                }
                return;
            }
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
        final String[] name4 = new String[1];
        listener = new Listener<LivingEntityUseItemEvent.Finish>(event -> {
            if (event.getEntity().equals(Announcer.mc.player) && event.getItem().getItem() instanceof ItemFood) {
                name4[0] = event.getItem().getDisplayName();
                if (Announcer.consumedItems.containsKey(name4[0])) {
                    Announcer.consumedItems.put(name4[0], Announcer.consumedItems.get(name4[0]) + 1);
                }
                else {
                    Announcer.consumedItems.put(name4[0], 1);
                }
                Announcer.lastLivingEntityUseFinishEvent = event;
            }
        }, (Predicate<LivingEntityUseItemEvent.Finish>[])new Predicate[0]);
    }

    public void onEnable() {
        Announcer.timer = new Timer();
        if (Announcer.mc.player == null) {
            disable();
            return;
        }
        (Announcer.df = new DecimalFormat("#.#")).setRoundingMode(RoundingMode.CEILING);
        Announcer.timerTask = new TimerTask() {
            @Override
            public void run() {
                Announcer.this.sendMessageCycle();
            }
        };
        Announcer.timer.schedule(Announcer.timerTask, 0L, delay.getValue() * 1000);
    }

    public void onDisable() {
        Announcer.timer.cancel();
        Announcer.timer.purge();
        Announcer.messageQueue.clear();
    }

    @Override
    public void onUpdate() {
        if (isDisabled() || Announcer.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (clearqueue.getValue()) {
            clearqueue.setValue(false);
            Announcer.messageQueue.clear();
        }
        getGameTickData();
    }

    private void getGameTickData() {
        if (distance.getValue()) {
            Announcer.lastTickPos = Announcer.thisTickPos;
            Announcer.thisTickPos = Announcer.mc.player.getPositionVector();
            Announcer.distanceTraveled += Announcer.thisTickPos.distanceTo(Announcer.lastTickPos);
        }
        if (playerheal.getValue() || playerdamage.getValue()) {
            Announcer.lastTickHealth = Announcer.thisTickHealth;
            Announcer.thisTickHealth = Announcer.mc.player.getHealth() + Announcer.mc.player.getAbsorptionAmount();
            final float healthDiff = Announcer.thisTickHealth - Announcer.lastTickHealth;
            if (healthDiff < 0.0f) {
                Announcer.lostHealth += healthDiff * -1.0f;
            }
            else {
                Announcer.gainedHealth += healthDiff;
            }
        }
    }

    private void composeGameTickData() {
        if (Announcer.isFirstRun) {
            Announcer.isFirstRun = false;
            clearTickData();
            return;
        }
        if (distance.getValue() && Announcer.distanceTraveled >= 1.0) {
            if (Announcer.distanceTraveled < delay.getValue() * mindistance.getValue()) {
                return;
            }
            if (Announcer.distanceTraveled > delay.getValue() * maxdistance.getValue()) {
                Announcer.distanceTraveled = 0.0;
                return;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("i ran ");
            sb.append((int)Announcer.distanceTraveled);
            if ((int)Announcer.distanceTraveled == 1) {
                sb.append(" meter! thanks to the power of Mc_Ren's sexy bald head");
            }
            else {
                sb.append(" meters! thanks to the power of Mc_Ren's sexy bald head");
            }
            queueMessage(sb.toString());
            Announcer.distanceTraveled = 0.0;
        }
        if (playerdamage.getValue() && Announcer.lostHealth != 0.0f) {
            final String sb2 = "i got hurt for " + Announcer.df.format(Announcer.lostHealth) + " health not thanks to the power of Mc_Ren's sexy bald head";
            queueMessage(sb2);
            Announcer.lostHealth = 0.0f;
        }
        if (playerheal.getValue() && Announcer.gainedHealth != 0.0f) {
            final String sb2 = "i gained " + Announcer.df.format(Announcer.gainedHealth) + " health thanks to the power of Mc_Ren's sexy bald head";
            queueMessage(sb2);
            Announcer.gainedHealth = 0.0f;
        }
    }

    private void composeEventData() {
        for (final Map.Entry<String, Integer> kv : Announcer.minedBlocks.entrySet()) {
            queueMessage("i just mined " + kv.getValue() + " " + kv.getKey() + "! thanks to the power of Mc_Ren's sexy bald head");
            Announcer.minedBlocks.remove(kv.getKey());
        }
        for (final Map.Entry<String, Integer> kv : Announcer.placedBlocks.entrySet()) {
            queueMessage("i just placed " + kv.getValue() + " " + kv.getKey() + "! thanks to the power of Mc_Ren's sexy bald head");
            Announcer.placedBlocks.remove(kv.getKey());
        }
        for (final Map.Entry<String, Integer> kv : Announcer.droppedItems.entrySet()) {
            queueMessage("i dropped " + kv.getValue() + " " + kv.getKey() + "! thanks to the power of Mc_Ren's sexy bald head");
            Announcer.droppedItems.remove(kv.getKey());
        }
        for (final Map.Entry<String, Integer> kv : Announcer.consumedItems.entrySet()) {
            queueMessage("i ate " + kv.getValue() + " " + kv.getKey() + "! thanks to the power of Mc_Ren's sexy bald head");
            Announcer.consumedItems.remove(kv.getKey());
        }
    }

    private void sendMessageCycle() {
        if (isDisabled() || Announcer.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        composeGameTickData();
        composeEventData();
        final Iterator<String> iterator = Announcer.messageQueue.iterator();
        if (iterator.hasNext()) {
            final String message = iterator.next();
            sendMessage(message);
            Announcer.messageQueue.remove(message);
        }
    }

    private void sendMessage(final String s) {
        final StringBuilder sb = new StringBuilder();
        if (greentext.getValue()) {
            sb.append("> ");
        }
        sb.append(s);
        Announcer.mc.player.connection.sendPacket(new CPacketChatMessage(sb.toString().replaceAll("§", "")));
    }

    private void clearTickData() {
        final Vec3d pos = Announcer.thisTickPos = (Announcer.lastTickPos = Announcer.mc.player.getPositionVector());
        Announcer.distanceTraveled = 0.0;
        final float health = Announcer.thisTickHealth = (Announcer.lastTickHealth = Announcer.mc.player.getHealth() + Announcer.mc.player.getAbsorptionAmount());
        Announcer.lostHealth = 0.0f;
        Announcer.gainedHealth = 0.0f;
    }

    private Block getBlock(final BlockPos pos) {
        return Announcer.mc.world.getBlockState(pos).getBlock();
    }

    private void queueMessage(final String message) {
        if (Announcer.messageQueue.size() > queuesize.getValue()) {
            return;
        }
        Announcer.messageQueue.add(message);
    }

    static {
        Announcer.isFirstRun = true;
        Announcer.messageQueue = new ConcurrentLinkedQueue<String>();
        Announcer.minedBlocks = new ConcurrentHashMap<String, Integer>();
        Announcer.placedBlocks = new ConcurrentHashMap<String, Integer>();
        Announcer.droppedItems = new ConcurrentHashMap<String, Integer>();
        Announcer.consumedItems = new ConcurrentHashMap<String, Integer>();
        Announcer.df = new DecimalFormat();
        Announcer.lastmessage = "";
    }
}
