package net.frozenblock.wildmod.event;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.misc.WildVec3d;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class WildGameEvent extends GameEvent {
    public static final GameEvent NOTE_BLOCK_PLAY = register("note_block_play");
    public static final GameEvent SCULK_SENSOR_TENDRILS_CLICKING = register("sculk_sensor_tendrils_clicking");
    public static final GameEvent SHRIEK = register("shriek", 32);
    public static final GameEvent INSTRUMENT_PLAY = register("instrument_play");
    public static final GameEvent ITEM_INTERACT_FINISH = register("item_interact_finish");
    public static final GameEvent ITEM_INTERACT_START = register("item_interact_start");
    public static final GameEvent TELEPORT = register("teleport");

    public static void registerGameEvents() {
        SculkSensorBlock.FREQUENCIES.put(TELEPORT, 5);
        SculkSensorBlock.FREQUENCIES.put(NOTE_BLOCK_PLAY, 6);
        SculkSensorBlock.FREQUENCIES.put(ITEM_INTERACT_FINISH, 14);
        SculkSensorBlock.FREQUENCIES.put(ITEM_INTERACT_START, 15);
        SculkSensorBlock.FREQUENCIES.put(INSTRUMENT_PLAY, 15);
    }

    private final String id;
    private final int range;

    public WildGameEvent(String id, int range) {
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
        return register(id, DEFAULT_RANGE);
    }

    private static GameEvent register(String id, int range) {
        return Registry.register(Registry.GAME_EVENT, new Identifier(WildMod.MOD_ID, id), new GameEvent(id, range));
    }

    public String toString() {
        return "Game Event{ " + this.id + " , " + this.range + "}";
    }

    public record Emitter(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
        public static WildGameEvent.Emitter of(@Nullable Entity sourceEntity) {
            return new WildGameEvent.Emitter(sourceEntity, null);
        }

        public static WildGameEvent.Emitter of(@Nullable BlockState affectedState) {
            return new WildGameEvent.Emitter(null, affectedState);
        }

        public static WildGameEvent.Emitter of(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
            return new WildGameEvent.Emitter(sourceEntity, affectedState);
        }
    }

    public static final class Message implements Comparable<Message> {
        private final WildGameEvent event;
        private final WildVec3d emitterPos;
        private final Emitter emitter;
        private final GameEventListener listener;
        private final double distanceTraveled;

        public Message(WildGameEvent event, WildVec3d emitterPos, Emitter emitter, GameEventListener listener, Vec3d listenerPos) {
            this.event = event;
            this.emitterPos = emitterPos;
            this.emitter = emitter;
            this.listener = listener;
            this.distanceTraveled = emitterPos.squaredDistanceTo(listenerPos);
        }

        public int compareTo(Message message) {
            return Double.compare(this.distanceTraveled, message.distanceTraveled);
        }

        public WildGameEvent getEvent() {
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
