package net.frozenblock.wildmod.event;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.event.PositionSource;

public interface GameEventListener extends net.minecraft.world.event.listener.GameEventListener {
    default boolean method_43723() {
        return false;
    }
    PositionSource getPositionSource();

    int getRange();

    boolean listen(ServerWorld world, GameEvent.class_7447 arg);
}
