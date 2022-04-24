package frozenblock.wild.mod.event;

import net.minecraft.util.math.Vec3d;

public interface GameEventDispatcher {
    GameEventDispatcher EMPTY = new GameEventDispatcher() {
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void addListener(GameEventListener listener) {
        }

        @Override
        public void removeListener(GameEventListener listener) {
        }

        @Override
        public void dispatch(frozenblock.wild.mod.event.GameEvent event, Vec3d pos, frozenblock.wild.mod.event.GameEvent.Emitter emitter) {
        }
    };

    boolean isEmpty();

    void addListener(GameEventListener listener);

    void removeListener(GameEventListener listener);

    void dispatch(frozenblock.wild.mod.event.GameEvent event, Vec3d pos, frozenblock.wild.mod.event.GameEvent.Emitter emitter);
}
