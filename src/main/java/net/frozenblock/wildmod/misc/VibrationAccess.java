package net.frozenblock.wildmod.misc;

import net.frozenblock.wildmod.event.WildGameEvents;
import net.minecraft.server.world.ServerWorld;

public interface VibrationAccess {

    boolean listen(ServerWorld world, WildGameEvents.Message event);

    interface Vibration {

    }
}
