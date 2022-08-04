package net.frozenblock.wildmod.misc;

import net.frozenblock.wildmod.event.WildGameEvent;
import net.minecraft.server.world.ServerWorld;

public interface VibrationAccess {

    boolean listen(ServerWorld world, WildGameEvent.Message event);

    interface Vibration {

    }
}
