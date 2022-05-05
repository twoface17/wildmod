package net.frozenblock.wildmod.event;

import net.frozenblock.wildmod.WildMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
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
    private final RegistryEntry.Reference<net.minecraft.world.event.GameEvent> registryEntry;

    public GameEvent(String id, int range) {
        super(id, range);
        this.registryEntry = Registry.GAME_EVENT.createEntry(this);
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
        public Emitter(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
            this.sourceEntity = sourceEntity;
            this.affectedState = affectedState;
        }

        public static Emitter of(@Nullable Entity sourceEntity) {
            return new Emitter(sourceEntity, (BlockState)null);
        }

        public static Emitter of(@Nullable BlockState affectedState) {
            return new Emitter((Entity)null, affectedState);
        }

        public static Emitter of(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
            return new Emitter(sourceEntity, affectedState);
        }

        @Nullable
        public Entity sourceEntity() {
            return this.sourceEntity;
        }

        @Nullable
        public BlockState affectedState() {
            return this.affectedState;
        }
    }
}
