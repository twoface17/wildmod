package net.frozenblock.wildmod.event;

import net.frozenblock.wildmod.WildMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

public class GameEvent extends net.minecraft.world.event.GameEvent {
    public static final GameEvent BLOCK_ACTIVATE = register("block_activate");
    public static final GameEvent BLOCK_ATTACH = register("block_attach");
    public static final GameEvent BLOCK_CHANGE = register("block_change");
    public static final GameEvent BLOCK_CLOSE = register("block_close");
    public static final GameEvent BLOCK_DEACTIVATE = register("block_deactivate");
    public static final GameEvent BLOCK_DESTROY = register("block_destroy");
    public static final GameEvent BLOCK_DETACH = register("block_detach");
    public static final GameEvent BLOCK_OPEN = register("block_open");
    public static final GameEvent BLOCK_PLACE = register("block_place");
    public static final GameEvent CONTAINER_CLOSE = register("container_close");
    public static final GameEvent CONTAINER_OPEN = register("container_open");
    public static final GameEvent DISPENSE_FAIL = register("dispense_fail");
    public static final GameEvent DRINK = register("drink");
    public static final GameEvent EAT = register("eat");
    public static final GameEvent ELYTRA_GLIDE = register("elytra_glide");
    public static final GameEvent ENTITY_DAMAGE = register("entity_damage");
    public static final GameEvent ENTITY_DIE = register("entity_die");
    public static final GameEvent ENTITY_INTERACT = register("entity_interact");
    public static final GameEvent ENTITY_PLACE = register("entity_place");
    public static final GameEvent ENTITY_ROAR = register("entity_roar");
    public static final GameEvent ENTITY_SHAKE = register("entity_shake");
    public static final GameEvent EQUIP = register("equip");
    public static final GameEvent EXPLODE = register("explode");
    public static final GameEvent FLAP = register("flap");
    public static final GameEvent FLUID_PICKUP = register("fluid_pickup");
    public static final GameEvent FLUID_PLACE = register("fluid_place");
    public static final GameEvent HIT_GROUND = register("hit_ground");
    public static final GameEvent ITEM_INTERACT_FINISH = register("item_interact_finish");
    public static final GameEvent ITEM_INTERACT_START = register("item_interact_start");
    public static final GameEvent LIGHTNING_STRIKE = register("lightning_strike");
    public static final GameEvent NOTE_BLOCK_PLAY = register("note_block_play");
    public static final GameEvent PISTON_CONTRACT = register("piston_contract");
    public static final GameEvent PISTON_EXTEND = register("piston_extend");
    public static final GameEvent PRIME_FUSE = register("prime_fuse");
    public static final GameEvent PROJECTILE_LAND = register("projectile_land");
    public static final GameEvent PROJECTILE_SHOOT = register("projectile_shoot");
    public static final GameEvent SCULK_SENSOR_TENDRILS_CLICKING = register("sculk_sensor_tendrils_clicking");
    public static final GameEvent SHEAR = register("shear");
    public static final GameEvent SHRIEK = register(String.valueOf(new Identifier(WildMod.MOD_ID, "shriek")), 32);
    public static final GameEvent SPLASH = register("splash");
    public static final GameEvent STEP = register("step");
    public static final GameEvent SWIM = register("swim");
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
        return Registry.register(Registry.GAME_EVENT, id, new GameEvent(id, range));
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
