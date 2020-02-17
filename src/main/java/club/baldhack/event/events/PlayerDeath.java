package club.baldhack.event.events;

import club.baldhack.event.KamiEvent;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerDeath extends KamiEvent {
    private EntityPlayer player;

    public PlayerDeath(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}

