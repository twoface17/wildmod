package net.frozenblock.wildmod.event;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.liukrastapi.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class GameEvent extends net.minecraft.world.event.GameEvent {
    public static GameEvent NOTE_BLOCK_PLAY;
    public static GameEvent SCULK_SENSOR_TENDRILS_CLICKING;
    public static GameEvent SHRIEK;

    public static void RegisterGameEvents() {
    /*public static final GameEvent BLOCK_ACTIVATE = register("block_activate");
    public static final GameEvent BLOCK_DEACTIVATE = register("block_deactivate");
    public static final GameEvent DRINK = register("drink");
    public static final GameEvent ELYTRA_GLIDE = register("elytra_glide");
    public static final GameEvent ENTITY_DAMAGE = register("entity_damage");
    public static final GameEvent ENTITY_DIE = register("entity_die");
    public static final GameEvent ENTITY_INTERACT = register("entity_interact");
    public static final GameEvent ENTITY_ROAR = register("entity_roar");
    public static final GameEvent ENTITY_SHAKE = register("entity_shake");
    public static final GameEvent ITEM_INTERACT_FINISH = register("item_interact_finish");
    public static final GameEvent ITEM_INTERACT_START = register("item_interact_start");
    */
        NOTE_BLOCK_PLAY = register("note_block_play");
        SCULK_SENSOR_TENDRILS_CLICKING = register("sculk_sensor_tendrils_clicking");
        SHRIEK = register("shriek", 32);
    }
    public static final int DEFAULT_RANGE = 16;
    private final String id;
    private final int range;
    private final RegistryEntry.Reference<net.minecraft.world.event.GameEvent> registryEntry = Registry.GAME_EVENT.createEntry(this);

    public GameEvent(String id, int range) {
        super(id, range);
        this.id = id;
        this.range = range;
    }

    public String getId() {
        return this.id;
    }

    public int getRange() {
        return this.range;
    }

    private static GameEvent register(String id) {
        return register(id, 16);
    }

    private static GameEvent register(String id, int range) {
        return Registry.register(Registry.GAME_EVENT, new Identifier(WildMod.MOD_ID, id), new GameEvent(id, range));
    }

    public String toString() {
        return "Game Event{ " + this.id + " , " + this.range + "}";
    }

    /**
     * @deprecated
     */
    @Deprecated
    public RegistryEntry.Reference<net.minecraft.world.event.GameEvent> getRegistryEntry() {
        return super.getRegistryEntry();
    }



    public static record Emitter(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
        public static GameEvent.Emitter of(@Nullable Entity sourceEntity) {
            return new GameEvent.Emitter(sourceEntity, null);
        }

        public static GameEvent.Emitter of(@Nullable BlockState affectedState) {
            return new GameEvent.Emitter(null, affectedState);
        }

        public static GameEvent.Emitter of(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
            return new GameEvent.Emitter(sourceEntity, affectedState);
        }
    }

    public static final class class_7447 implements Comparable<GameEvent.class_7447> {
        private final GameEvent field_39177;
        private final net.frozenblock.wildmod.liukrastapi.Vec3d field_39178;
        private final Emitter field_39179;
        private final GameEventListener field_39180;
        private final double field_39181;

        public class_7447(GameEvent gameEvent, Vec3d vec3d, GameEvent.Emitter emitter, GameEventListener gameEventListener, net.frozenblock.wildmod.liukrastapi.Vec3d vec3d2) {
            this.field_39177 = gameEvent;
            this.field_39178 = vec3d;
            this.field_39179 = emitter;
            this.field_39180 = gameEventListener;
            this.field_39181 = vec3d.squaredDistanceTo(vec3d2);
        }

        public int compareTo(GameEvent.class_7447 arg) {
            return Double.compare(this.field_39181, arg.field_39181);
        }

        public GameEvent method_43724() {
            return this.field_39177;
        }

        public net.frozenblock.wildmod.liukrastapi.Vec3d method_43726() {
            return this.field_39178;
        }

        public Emitter method_43727() {
            return this.field_39179;
        }

        public GameEventListener method_43728() {
            return this.field_39180;
        }
    }
}
