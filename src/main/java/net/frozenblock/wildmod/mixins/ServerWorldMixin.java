package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.liukrastapi.WildServerWorld;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements WildServerWorld {
    @Shadow @Final private List<ServerPlayerEntity> players;

    @Shadow protected abstract boolean sendToPlayerIfNearby(ServerPlayerEntity player, boolean force, double x, double y, double z, Packet<?> packet);

    public <T extends ParticleEffect> int spawnParticles(
            T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed
    ) {
        ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particle, false, x, y, z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
        int i = 0;

        for(int j = 0; j < this.players.size(); ++j) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(j);
            if (this.sendToPlayerIfNearby(serverPlayerEntity, false, x, y, z, particleS2CPacket)) {
                ++i;
            }
        }

        return i;
    }

    public <T extends ParticleEffect> boolean spawnParticles(
            ServerPlayerEntity viewer, T particle, boolean force, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed
    ) {
        Packet<?> packet = new ParticleS2CPacket(particle, force, x, y, z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
        return this.sendToPlayerIfNearby(viewer, force, x, y, z, packet);
    }
}
