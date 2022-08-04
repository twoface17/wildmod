package net.frozenblock.wildmod.misc;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;

public interface WildServerWorld {
    <T extends ParticleEffect> int spawnParticles(T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed);

    <T extends ParticleEffect> boolean spawnParticles(ServerPlayerEntity viewer, T particle, boolean force, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed);
}
