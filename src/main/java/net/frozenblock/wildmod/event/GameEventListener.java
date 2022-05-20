package net.frozenblock.wildmod.event;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.event.PositionSource;

public interface GameEventListener {
    default boolean shouldListenImmediately() {
        return false;
    }
    PositionSource getPositionSource();

    int getRange();

    boolean listen(ServerWorld world, WildGameEvents.Message arg);
}
