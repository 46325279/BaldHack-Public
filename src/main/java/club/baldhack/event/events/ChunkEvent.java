package club.baldhack.event.events;

import club.baldhack.event.KamiEvent;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;

/**
 * @author 086
 */
public class ChunkEvent extends KamiEvent {
    private Chunk chunk;
    private SPacketChunkData packet;

    public ChunkEvent(Chunk chunk, SPacketChunkData packet) {
        this.chunk = chunk;
        this.packet = packet;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public SPacketChunkData getPacket() {
        return packet;
    }
}
