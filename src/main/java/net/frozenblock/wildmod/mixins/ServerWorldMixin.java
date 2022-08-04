package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.event.WildGameEvent;
import net.frozenblock.wildmod.event.WildGameEventListener;
import net.frozenblock.wildmod.misc.WildServerWorld;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements WildServerWorld {
    @Shadow
    @Final
    List<ServerPlayerEntity> players;

    private List<WildGameEvent.Message> queuedEvents = new ArrayList<>();

    @Shadow
    protected abstract boolean sendToPlayerIfNearby(ServerPlayerEntity player, boolean force, double x, double y, double z, Packet<?> packet);

    public <T extends ParticleEffect> int spawnParticles(
            T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed
    ) {
        ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particle, false, x, y, z, (float) deltaX, (float) deltaY, (float) deltaZ, (float) speed, count);
        int i = 0;

        for (ServerPlayerEntity serverPlayerEntity : this.players) {
            if (this.sendToPlayerIfNearby(serverPlayerEntity, false, x, y, z, particleS2CPacket)) {
                ++i;
            }
        }

        return i;
    }

    public <T extends ParticleEffect> boolean spawnParticles(
            ServerPlayerEntity viewer, T particle, boolean force, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed
    ) {
        Packet<?> packet = new ParticleS2CPacket(particle, force, x, y, z, (float) deltaX, (float) deltaY, (float) deltaZ, (float) speed, count);
        return this.sendToPlayerIfNearby(viewer, force, x, y, z, packet);
    }

    /*@Inject(method = "emitGameEvent", at = @At("TAIL"))
    public void emitGameEvent(Entity entity, GameEvent event, BlockPos pos, CallbackInfo ci) {
        ServerWorld serverWorld = ServerWorld.class.cast(this);
        WildVec3d emitterPos = WildVec3d.ofCenter(pos);
        WildGameEvent.Emitter emitter = WildGameEvent.Emitter.of(entity);
        int i = event.getRange();
        BlockPos blockPos = new BlockPos(emitterPos);
        int j = ChunkSectionPos.getSectionCoord(blockPos.getX() - i);
        int k = ChunkSectionPos.getSectionCoord(blockPos.getY() - i);
        int l = ChunkSectionPos.getSectionCoord(blockPos.getZ() - i);
        int m = ChunkSectionPos.getSectionCoord(blockPos.getX() + i);
        int n = ChunkSectionPos.getSectionCoord(blockPos.getY() + i);
        int o = ChunkSectionPos.getSectionCoord(blockPos.getZ() + i);
        List<WildGameEvent.Message> list = new ArrayList<>();
        boolean bl = false;

        for(int p = j; p <= m; ++p) {
            for(int q = l; q <= o; ++q) {
                Chunk chunk = serverWorld.getChunkManager().getWorldChunk(p, q);
                if (chunk != null) {
                    for(int r = k; r <= n; ++r) {
                        bl |= chunk.getGameEventDispatcher(r)
                                .dispatch(
                                        event,
                                        entity,
                                        pos,
                                        (listener, listenerPos) -> (this.queuedEvents)
                                                .add(new WildGameEvent.Message(event, emitterPos, emitter, listener, listenerPos))
                                );
                    }
                }
            }
        }

        if (!this.queuedEvents.isEmpty()) {
            this.processEvents(list);
        }

        if (bl) {
            DebugInfoSender.sendGameEvent(serverWorld, event, pos);
        }

    }*/

    private void processEvents(List<WildGameEvent.Message> events) {
        ServerWorld serverWorld = ServerWorld.class.cast(this);
        Collections.sort(events);

        for (WildGameEvent.Message message : events) {
            if (message.getListener() instanceof WildGameEventListener wildGameEventListener) {
                wildGameEventListener.listen(serverWorld, message);
            }
        }

    }

    private void processEventQueue() {
        if (!this.queuedEvents.isEmpty()) {
            List<WildGameEvent.Message> list = this.queuedEvents;
            this.queuedEvents = new ArrayList<>();
            this.processEvents(list);
        }
    }


}
