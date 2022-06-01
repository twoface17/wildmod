package net.frozenblock.wildmod.event;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.liukrastapi.WildVec3d;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class WildGameEvents extends net.minecraft.world.event.GameEvent {
    public static WildGameEvents NOTE_BLOCK_PLAY;
    public static WildGameEvents SCULK_SENSOR_TENDRILS_CLICKING;
    public static WildGameEvents SHRIEK;
    public static WildGameEvents INSTRUMENT_PLAY;

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
        INSTRUMENT_PLAY = register("instrument_play");
        NOTE_BLOCK_PLAY = register("note_block_play");
        SCULK_SENSOR_TENDRILS_CLICKING = register("sculk_sensor_tendrils_clicking");
        SHRIEK = register("shriek", 32);
    }
    public static final int DEFAULT_RANGE = 16;
    private final String id;
    private final int range;
    private final RegistryEntry.Reference<net.minecraft.world.event.GameEvent> registryEntry = Registry.GAME_EVENT.createEntry(this);

    public WildGameEvents(String id, int range) {
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

    private static WildGameEvents register(String id) {
        return register(id, 16);
    }

    private static WildGameEvents register(String id, int range) {
        return Registry.register(Registry.GAME_EVENT, new Identifier(WildMod.MOD_ID, id), new WildGameEvents(id, range));
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
        public static WildGameEvents.Emitter of(@Nullable Entity sourceEntity) {
            return new WildGameEvents.Emitter(sourceEntity, null);
        }

        public static WildGameEvents.Emitter of(@Nullable BlockState affectedState) {
            return new WildGameEvents.Emitter(null, affectedState);
        }

        public static WildGameEvents.Emitter of(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
            return new WildGameEvents.Emitter(sourceEntity, affectedState);
        }
    }

    public static final class Message implements Comparable<Message> {
        private final WildGameEvents event;
        private final WildVec3d emitterPos;
        private final Emitter emitter;
        private final GameEventListener listener;
        private final double distanceTraveled;

        public Message(WildGameEvents event, WildVec3d emitterPos, Emitter emitter, GameEventListener listener, Vec3d listenerPos) {
            this.event = event;
            this.emitterPos = emitterPos;
            this.emitter = emitter;
            this.listener = listener;
            this.distanceTraveled = emitterPos.squaredDistanceTo(listenerPos);
        }

        public int compareTo(Message message) {
            return Double.compare(this.distanceTraveled, message.distanceTraveled);
        }

        public WildGameEvents getEvent() {
            return this.event;
        }

        public WildVec3d getEmitterPos() {
            return this.emitterPos;
        }

        public Emitter getEmitter() {
            return this.emitter;
        }

        public GameEventListener getListener() {
            return this.listener;
        }
    }
}
