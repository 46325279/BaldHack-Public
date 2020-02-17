package club.baldhack;

import club.baldhack.util.ColourUtils;
import club.baldhack.util.Friends;
import club.baldhack.util.LagCompensator;
import club.baldhack.util.Wrapper;
import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.zero.alpine.EventBus;
import me.zero.alpine.EventManager;
import club.baldhack.command.Command;
import club.baldhack.command.CommandManager;
import club.baldhack.event.ForgeEventProcessor;
import club.baldhack.gui.font.CFontRenderer;
import club.baldhack.gui.kami.KamiGUI;
import club.baldhack.gui.rgui.component.AlignedComponent;
import club.baldhack.gui.rgui.component.Component;
import club.baldhack.gui.rgui.component.container.use.Frame;
import club.baldhack.gui.rgui.util.ContainerHelper;
import club.baldhack.gui.rgui.util.Docking;
import club.baldhack.module.Module;
import club.baldhack.module.ModuleManager;
import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import club.baldhack.setting.SettingsRegister;
import club.baldhack.setting.config.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.BufferedReader;

/**
 * Created by 086 on 7/11/2017.
 */
@Mod(modid = BaldHack.MODID, name = BaldHack.MODNAME, version = BaldHack.MODVER)
public class BaldHack {

    public static final String MODID = "baldhack";
    public static final String MODNAME = "BaldHack";
    public static final String MODVER = "b3";
    public int redForBG;
    public int greenForBG;
    public int blueForBG;
    public boolean rainbowBG;
    public static CFontRenderer cFontRenderer;
    private static final String KAMI_CONFIG_NAME_DEFAULT = "baldhack.json";

    public static final Logger log = LogManager.getLogger("baldhack");

    public static final EventBus EVENT_BUS = new EventManager();
    public static ArrayList<String> lines = new ArrayList<String>();
    @Mod.Instance
    private static BaldHack INSTANCE;

    public KamiGUI guiManager;
    public CommandManager commandManager;
    private Setting<JsonObject> guiStateSetting = Settings.custom("gui", new JsonObject(), new Converter<JsonObject, JsonObject>() {
        @Override
        protected JsonObject doForward(JsonObject jsonObject) {
            return jsonObject;
        }

        @Override
        protected JsonObject doBackward(JsonObject jsonObject) {
            return jsonObject;
        }
    }).buildAndRegister("");

    private void checkSettingGuiColour(final Setting setting) {
        final String name2;
        final String name = name2 = setting.getName();
        switch (name2) {
            case "Red": {
                redForBG = (int) setting.getValue();
                break;
            }
            case "Green": {
                greenForBG = (int) setting.getValue();
                break;
            }
            case "Blue": {
                blueForBG = (int) setting.getValue();
                break;
            }
        }
    }

    private void checkRainbowSetting(final Setting setting) {
        final String name2;
        final String name = name2 = setting.getName();
        switch (name2) {
            case "Rainbow Watermark": {
                rainbowBG = (boolean) setting.getValue();
                break;
            }
        }
    }
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }
    @Mod.EventHandler
    public void init() {
        BaldHack.log.info("\n\nstarting baldhack " + MODVER);
        MinecraftForge.EVENT_BUS.register(this);
        ModuleManager.initialize();

        ModuleManager.getModules().stream().filter(module -> module.alwaysListening).forEach(EVENT_BUS::subscribe);
        MinecraftForge.EVENT_BUS.register(new ForgeEventProcessor());
        LagCompensator.INSTANCE = new LagCompensator();
        cFontRenderer = new CFontRenderer(new Font("Verdana", 0, 18), true, false);

        Wrapper.init();

        guiManager = new KamiGUI();
        guiManager.initializeGUI();

        commandManager = new CommandManager();

        Friends.initFriends();
        SettingsRegister.register("commandPrefix", Command.commandPrefix);
        loadConfiguration();
        BaldHack.log.info("bruh");

        ModuleManager.updateLookup(); // generate the lookup table after settings are loaded to make custom module names work

        // After settings loaded, we want to let the enabled modules know they've been enabled (since the setting is done through reflection)
        ModuleManager.getModules().stream().filter(Module::isEnabled).forEach(Module::enable);

        BaldHack.log.info("done\n");
    }
    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        final float[] hue = {System.currentTimeMillis() % 11520L / 11520.0f};
        ModuleManager.getModuleByName("Gui").settingList.forEach(setting -> this.checkSettingGuiColour(setting));
        ModuleManager.getModuleByName("Gui").settingList.forEach(setting -> this.checkRainbowSetting(setting));
        final int rgb = Color.HSBtoRGB(hue[0], 1.0f, 1.0f);
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }
        String playername = mc.player.getName();
        if (rainbowBG) {
            cFontRenderer.drawStringWithShadow("BaldHack: " + playername + "", 1.0, 0.0, rgb);
        }
        else {
            cFontRenderer.drawStringWithShadow("BaldHack: " + playername + "", 1.0, 0.0, ColourUtils.toRGBA(redForBG, greenForBG, blueForBG, 255));
        }
            if (mc.player.isPotionActive(MobEffects.WEAKNESS) && !mc.player.isPotionActive(MobEffects.STRENGTH)) {
                cFontRenderer.drawStringWithShadow("You can not break crystals", 1, 10, Color.red.getRGB());
                final float[] array = hue;
                final int n = 0;
                array[n] += 0.02f;
            } else {
                cFontRenderer.drawStringWithShadow("You can break crystals", 1, 10, Color.white.getRGB());
                }
            }
    public static String getConfigName() {
        Path config = Paths.get("KAMILastConfig.txt");
        String kamiConfigName = KAMI_CONFIG_NAME_DEFAULT;
        try(BufferedReader reader = Files.newBufferedReader(config)) {
            kamiConfigName = reader.readLine();
            if (!isFilenameValid(kamiConfigName)) kamiConfigName = KAMI_CONFIG_NAME_DEFAULT;
        } catch (FileNotFoundException e) {
            try(BufferedWriter writer = Files.newBufferedWriter(config)) {
                writer.write(KAMI_CONFIG_NAME_DEFAULT);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kamiConfigName;
    }

    public static void loadConfiguration() {
        try {
            loadConfigurationUnsafe();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfigurationUnsafe() throws IOException {
        String kamiConfigName = getConfigName();
        Path kamiConfig = Paths.get(kamiConfigName);
        if (!Files.exists(kamiConfig)) return;
        Configuration.loadConfiguration(kamiConfig);

        JsonObject gui = BaldHack.INSTANCE.guiStateSetting.getValue();
        for (Map.Entry<String, JsonElement> entry : gui.entrySet()) {
            Optional<Component> optional = BaldHack.INSTANCE.guiManager.getChildren().stream().filter(component -> component instanceof Frame).filter(component -> ((Frame) component).getTitle().equals(entry.getKey())).findFirst();
            if (optional.isPresent()) {
                JsonObject object = entry.getValue().getAsJsonObject();
                Frame frame = (Frame) optional.get();
                frame.setX(object.get("x").getAsInt());
                frame.setY(object.get("y").getAsInt());
                Docking docking = Docking.values()[object.get("docking").getAsInt()];
                if (docking.isLeft()) ContainerHelper.setAlignment(frame, AlignedComponent.Alignment.LEFT);
                else if (docking.isRight()) ContainerHelper.setAlignment(frame, AlignedComponent.Alignment.RIGHT);
                frame.setDocking(docking);
                frame.setMinimized(object.get("minimized").getAsBoolean());
                frame.setPinned(object.get("pinned").getAsBoolean());
            } else {
                System.err.println("Found GUI config entry for " + entry.getKey() + ", but found no frame with that name");
            }
        }
        BaldHack.getInstance().getGuiManager().getChildren().stream().filter(component -> (component instanceof Frame) && (((Frame) component).isPinneable()) && component.isVisible()).forEach(component -> component.setOpacity(0f));
    }

    public static void saveConfiguration() {
        try {
            saveConfigurationUnsafe();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfigurationUnsafe() throws IOException {
        JsonObject object = new JsonObject();
        BaldHack.INSTANCE.guiManager.getChildren().stream().filter(component -> component instanceof Frame).map(component -> (Frame) component).forEach(frame -> {
            JsonObject frameObject = new JsonObject();
            frameObject.add("x", new JsonPrimitive(frame.getX()));
            frameObject.add("y", new JsonPrimitive(frame.getY()));
            frameObject.add("docking", new JsonPrimitive(Arrays.asList(Docking.values()).indexOf(frame.getDocking())));
            frameObject.add("minimized", new JsonPrimitive(frame.isMinimized()));
            frameObject.add("pinned", new JsonPrimitive(frame.isPinned()));
            object.add(frame.getTitle(), frameObject);
        });
        BaldHack.INSTANCE.guiStateSetting.setValue(object);

        Path outputFile = Paths.get(getConfigName());
        if (!Files.exists(outputFile))
            Files.createFile(outputFile);
        Configuration.saveConfiguration(outputFile);
        ModuleManager.getModules().forEach(Module::destroy);
    }

    public static boolean isFilenameValid(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static BaldHack getInstance() {
        return INSTANCE;
    }

    public KamiGUI getGuiManager() {
        return guiManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
