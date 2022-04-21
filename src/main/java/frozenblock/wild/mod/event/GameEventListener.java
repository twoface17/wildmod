package frozenblock.wild.mod.event;

import frozenblock.wild.mod.liukrastapi.Vec3d;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.event.PositionSource;

public interface GameEventListener {
    PositionSource getPositionSource();

    int getRange();

    boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d pos);
}
