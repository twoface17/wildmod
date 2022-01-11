package frozenblock.wild.mod.fromAccurateSculk;

import frozenblock.wild.mod.WildMod;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class ClickGameEvent extends GameEvent {

    public static final GameEvent CLICK = ClickGameEvent.register("click");
    public static final GameEvent DEATH = ClickGameEvent.register("death");

    public ClickGameEvent(String string, int i, String id, int range) {
        super(string, i);
        this.id = id;
        this.range = range;
    }
    private final String id;
    private final int range;

    public String getId() {
        return this.id;
    }

    public int getRange() {
        return this.range;
    }

    private static GameEvent register(String string) {
        return ClickGameEvent.register(string, 16);
    }

    private static GameEvent register(String string, int i) {
        return Registry.register(Registry.GAME_EVENT, string, new GameEvent(string, i));
    }

    public String toString() {
        return "Game Event{ " + this.id + " , " + this.range + "}";
    }
}
