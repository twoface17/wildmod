package net.frozenblock.wildmod.liukrastapi;

import net.frozenblock.wildmod.event.GameEvent;
import net.minecraft.server.world.ServerWorld;

public interface VibrationAccess {

    boolean listen(ServerWorld world, GameEvent.Message event);

    interface Vibration {

    }
}
