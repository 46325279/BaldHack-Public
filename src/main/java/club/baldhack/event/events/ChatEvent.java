package club.baldhack.event.events;

import club.baldhack.event.KamiEvent;
import net.minecraft.util.text.ITextComponent;

public class ChatEvent extends KamiEvent {
    private ITextComponent txt;

    public ChatEvent(ITextComponent txt) {
        this.txt = txt;
    }
}