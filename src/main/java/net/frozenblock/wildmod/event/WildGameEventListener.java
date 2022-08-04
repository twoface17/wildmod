package net.frozenblock.wildmod.event;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

public interface WildGameEventListener extends GameEventListener {
    default boolean shouldListenImmediately() {
        return false;
    }

    /**
     * Returns the position source of this listener.
     */
    PositionSource getPositionSource();

    /**
     * Returns the range, in blocks, of the listener.
     */
    int getRange();

    /**
     * Listens to an incoming game event.
     *
     * @return {@code true} if the game event has been accepted by this listener
     */
    boolean listen(ServerWorld world, WildGameEvent.Message event);
}
