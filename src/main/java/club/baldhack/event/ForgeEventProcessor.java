package club.baldhack.event;

import club.baldhack.command.Command;
import club.baldhack.command.commands.PeekCommand;
import club.baldhack.event.events.DisplaySizeChangedEvent;
import club.baldhack.module.ModuleManager;
import club.baldhack.module.modules.render.BossStack;
import club.baldhack.util.KamiTessellator;
import club.baldhack.BaldHack;
import club.baldhack.gui.UIRenderer;
import club.baldhack.gui.kami.KamiGUI;
import club.baldhack.gui.rgui.component.container.use.Frame;
import club.baldhack.util.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Created by 086 on 11/11/2017.
 */
public class ForgeEventProcessor {

    private int displayWidth;
    private int displayHeight;

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.isCanceled()) return;
//        BaldHack.EVENT_BUS.post(new UpdateEvent());

        if (Minecraft.getMinecraft().displayWidth != displayWidth || Minecraft.getMinecraft().displayHeight != displayHeight) {
            BaldHack.EVENT_BUS.post(new DisplaySizeChangedEvent());
            displayWidth = Minecraft.getMinecraft().displayWidth;
            displayHeight = Minecraft.getMinecraft().displayHeight;

            BaldHack.getInstance().getGuiManager().getChildren().stream()
                    .filter(component -> component instanceof Frame)
                    .forEach(component -> KamiGUI.dock((Frame) component));
        }

        if (PeekCommand.sb != null) {
            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            GuiShulkerBox gui = new GuiShulkerBox(Wrapper.getPlayer().inventory, PeekCommand.sb);
            gui.setWorldAndResolution(Wrapper.getMinecraft(), i, j);
            Minecraft.getMinecraft().displayGuiScreen(gui);
            PeekCommand.sb = null;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Wrapper.getPlayer() == null) return;
        ModuleManager.onUpdate();
        BaldHack.getInstance().getGuiManager().callTick(BaldHack.getInstance().getGuiManager());
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) return;
        ModuleManager.onWorldRender(event);
    }

    @SubscribeEvent
    public void onRenderPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO && ModuleManager.isModuleEnabled("BossStack")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.isCanceled()) return;

        RenderGameOverlayEvent.ElementType target = RenderGameOverlayEvent.ElementType.EXPERIENCE;
        if (!Wrapper.getPlayer().isCreative() && Wrapper.getPlayer().getRidingEntity() instanceof AbstractHorse)
            target = RenderGameOverlayEvent.ElementType.HEALTHMOUNT;

        if (event.getType() == target) {
            ModuleManager.onRender();
            GL11.glPushMatrix();
            UIRenderer.renderAndUpdateFrames();
            GL11.glPopMatrix();
            KamiTessellator.releaseGL();
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO && ModuleManager.isModuleEnabled("BossStack")) {
            BossStack.render(event);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState())
            ModuleManager.onBind(Keyboard.getEventKey());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatSent(ClientChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.setCanceled(true);
            try {
                Wrapper.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(event.getMessage());

                if (event.getMessage().length() > 1)
                    BaldHack.getInstance().commandManager.callCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
                else
                    Command.sendChatMessage("Please enter a command.");
            } catch (Exception e) {
                e.printStackTrace();
                Command.sendChatMessage("Error occured while running command! (" + e.getMessage() + ")");
            }
            event.setMessage("");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDrawn(RenderPlayerEvent.Pre event) {
        BaldHack.EVENT_BUS.post(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDrawn(RenderPlayerEvent.Post event) {
        BaldHack.EVENT_BUS.post(event);
    }

    @SubscribeEvent()
    public void onChunkLoaded(ChunkEvent.Load event) {
        BaldHack.EVENT_BUS.post(event);
    }

    @SubscribeEvent()
    public void onChunkLoaded(ChunkEvent.Unload event) {
        BaldHack.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        BaldHack.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onLivingEntityUseItemEventTick(LivingEntityUseItemEvent.Start entityUseItemEvent) {
        BaldHack.EVENT_BUS.post(entityUseItemEvent);
    }

    @SubscribeEvent
    public void onLivingDamageEvent(LivingDamageEvent event) {
        BaldHack.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent entityJoinWorldEvent) {
        BaldHack.EVENT_BUS.post(entityJoinWorldEvent);
    }

    @SubscribeEvent
    public void onPlayerPush(PlayerSPPushOutOfBlocksEvent event) {
        BaldHack.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        BaldHack.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent entityEvent) {
        BaldHack.EVENT_BUS.post(entityEvent);
    }

}
