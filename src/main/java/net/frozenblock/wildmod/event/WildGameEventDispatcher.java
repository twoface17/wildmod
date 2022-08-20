package net.frozenblock.wildmod.event;

import net.minecraft.util.math.Vec3d;

public interface WildGameEventDispatcher {
    WildGameEventDispatcher EMPTY = new WildGameEventDispatcher() {
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void addListener(WildGameEventListener listener) {
        }

        @Override
        public void removeListener(WildGameEventListener listener) {
        }

        @Override
        public void dispatch(WildGameEvent event, Vec3d pos, WildGameEvent.Emitter emitter) {
        }
    };

    boolean isEmpty();

    void addListener(WildGameEventListener listener);

    void removeListener(WildGameEventListener listener);

    void dispatch(WildGameEvent event, Vec3d pos, WildGameEvent.Emitter emitter);
}
