package club.baldhack.event.events;

import club.baldhack.event.KamiEvent;
import net.minecraft.client.gui.GuiScreen;

public class KeyboardEvent extends KamiEvent {
    public final GuiScreen screen;

    public KeyboardEvent(GuiScreen screen) {
        this.screen = screen;
    }
}
