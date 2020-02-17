package club.baldhack.rpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// pasted from backdoored
public class RPCshit {
    public static String lastChat = "";
    @SubscribeEvent
    public void onChatRecieved(ClientChatReceivedEvent event) {
        lastChat = event.getMessage().getUnformattedText();
    }
    public static final Minecraft mc = Minecraft.getMinecraft();
    private static final String APP_ID = "629430771647512608";
    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
    private static DiscordRichPresence presence = new DiscordRichPresence();
    private static boolean hasStarted = false;


    public static boolean start() {
        FMLLog.log.info("Starting Discord RPC");
        if (hasStarted) {
            return false;
        }
        hasStarted = true;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.disconnected = (var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + String.valueOf(var1) + ", var2: " + var2);
        rpc.Discord_Initialize(APP_ID, handlers, true, "");
        RPCshit.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        RPCshit.presence.details = "Main Menu";
        RPCshit.presence.state = "https://discord.gg/eRC4TQs";
        RPCshit.presence.largeImageKey = "crystallinqq";
        rpc.Discord_UpdatePresence(presence);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    rpc.Discord_RunCallbacks();
                    String details = "";
                    String state = "";
                    int players = 0;
                    int maxPlayers = 0;
                    if (mc.isIntegratedServerRunning()) {
                        details = "Singleplayer";
                    } else if (mc.getCurrentServerData() != null) {
                        ServerData svr = mc.getCurrentServerData();
                        if (!svr.serverIP.equals("")) {
                            String[] popInfo;
                            details = "Multiplayer";
                            state = svr.serverIP;
                            if (svr.populationInfo != null && (popInfo = svr.populationInfo.split("/")).length > 2) {
                                players = Integer.valueOf(popInfo[0]);
                                maxPlayers = Integer.valueOf(popInfo[1]);
                            }
                            if (state.contains("2b2t.org")) {
                                try {
                                    if (RPCshit.lastChat.startsWith("Position in queue: ")) {
                                        state = state + " " + Integer.parseInt(RPCshit.lastChat.substring(19)) + " in queue";
                                    }
                                }
                                catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        details = "Main Menu";
                        state = "https://discord.gg/eRC4TQs";
                    }
                    if (!details.equals(RPCshit.presence.details) || !state.equals(RPCshit.presence.state)) {
                        RPCshit.presence.startTimestamp = System.currentTimeMillis() / 1000L;
                    }
                    RPCshit.presence.details = details;
                    RPCshit.presence.state = state;
                    rpc.Discord_UpdatePresence(presence);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Discord-RPC-Callback-Handler").start();
        return true;
    }
}