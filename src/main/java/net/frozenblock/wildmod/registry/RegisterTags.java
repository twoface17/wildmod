package net.frozenblock.wildmod.registry;

import net.frozenblock.wildmod.WildMod;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class RegisterTags {
    public static final TagKey<Block> ANCIENT_CITY_REPLACEABLE = of("ancient_city_replaceable");
    public static final TagKey<Block> CONVERTABLE_TO_MUD = of("convertable_to_mud");
    public static final TagKey<Block> DAMPENS_VIBRATIONS = of("dampens_vibrations");
    public static final TagKey<Block> FROG_PREFER_JUMP_TO = of("frog_prefer_jump_to");
    public static final TagKey<Block> FROGS_SPAWNABLE_ON = of("frogs_spawnable_on");
    public static final TagKey<Block> MANGROVE_LOGS_CAN_GROW_THROUGH = of("mangrove_logs_can_grow_through");
    public static final TagKey<Block> MANGROVE_ROOTS_CAN_GROW_THROUGH = of("mangrove_roots_can_grow_through");

    public static final TagKey<EntityType<?>> FROG_FOOD = ofEntityType("frog_food");
    public static final TagKey<GameEvent> WARDEN_CAN_LISTEN = ofEvent("warden_can_listen");
    public static final TagKey<GameEvent> SHRIEKER_CAN_LISTEN = ofEvent("shrieker_can_listen");
    public static final TagKey<GameEvent> ALLAY_CAN_LISTEN = ofEvent("allay_can_listen");

    private RegisterTags() {
    }

    private static TagKey<Block> of(String id) {
        return TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, id));
    }
    private static TagKey<EntityType<?>> ofEntityType(String id) {
        return TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, id));
    }

    private static TagKey<GameEvent> ofEvent(String id) {
        return TagKey.of(Registry.GAME_EVENT_KEY, new Identifier(WildMod.MOD_ID, id));
    }
}
