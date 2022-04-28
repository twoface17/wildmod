package net.frozenblock.wildmod.event;

import net.frozenblock.wildmod.liukrastapi.Vec3d;
import net.minecraft.server.world.ServerWorld;

public interface GameEventListener {
    PositionSource getPositionSource();

    int getRange();

    boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d pos);
}
