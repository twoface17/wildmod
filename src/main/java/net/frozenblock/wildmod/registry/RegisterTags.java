package net.frozenblock.wildmod.registry;

import net.frozenblock.wildmod.WildMod;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class RegisterTags {
    public static final TagKey<Block> ANCIENT_CITY_REPLACEABLE = of("ancient_city_replaceable");
    public static final TagKey<Block> CONVERTABLE_TO_MUD = of("convertable_to_mud");
    public static final TagKey<Block> DAMPENS_VIBRATIONS = of("dampens_vibrations");
    public static final TagKey<Block> FROG_PREFER_JUMP_TO = of("frog_prefer_jump_to");
    public static final TagKey<Block> FROGS_SPAWNABLE_ON = of("frogs_spawnable_on");
    public static final TagKey<Block> MANGROVE_LOGS_CAN_GROW_THROUGH = of("mangrove_logs_can_grow_through");
    public static final TagKey<Block> MANGROVE_ROOTS_CAN_GROW_THROUGH = of("mangrove_roots_can_grow_through");
    public static final TagKey<Block> SNAPS_GOAT_HORN = of("snaps_goat_horn");

    public static final TagKey<EntityType<?>> FROG_FOOD = ofEntityType("frog_food");
    public static final TagKey<GameEvent> WARDEN_CAN_LISTEN = ofEvent("warden_can_listen");
    public static final TagKey<GameEvent> SHRIEKER_CAN_LISTEN = ofEvent("shrieker_can_listen");
    public static final TagKey<GameEvent> ALLAY_CAN_LISTEN = ofEvent("allay_can_listen");

    public static final TagKey<Biome> ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT = ofBiome("allows_tropical_fish_spawns_at_any_height");
    public static final TagKey<Biome> ALLOWS_SURFACE_SLIME_SPAWNS = ofBiome("allows_surface_slime_spawns");
    public static final TagKey<Biome> ANCIENT_CITY_HAS_STRUCTURE = ofBiome("ancient_city_has_structure");
    public static final TagKey<Biome> HAS_CLOSER_WATER_FOG = ofBiome("has_closer_water_fog");
    public static final TagKey<Biome> IS_DEEP_OCEAN = ofBiome("is_deep_ocean");
    public static final TagKey<Biome> IS_OCEAN = ofBiome("is_ocean");
    public static final TagKey<Biome> IS_BEACH = ofBiome("is_beach");
    public static final TagKey<Biome> IS_RIVER = ofBiome("is_river");
    public static final TagKey<Biome> IS_MOUNTAIN = ofBiome("is_mountain");
    public static final TagKey<Biome> IS_BADLANDS = ofBiome("is_badlands");
    public static final TagKey<Biome> IS_HILL = ofBiome("is_hill");
    public static final TagKey<Biome> IS_TAIGA = ofBiome("is_taiga");
    public static final TagKey<Biome> IS_JUNGLE = ofBiome("is_jungle");
    public static final TagKey<Biome> IS_FOREST = ofBiome("is_forest");
    public static final TagKey<Biome> IS_SAVANNA = ofBiome("is_savanna");
    public static final TagKey<Biome> IS_OVERWORLD = ofBiome("is_overworld");
    public static final TagKey<Biome> IS_NETHER = ofBiome("is_nether");
    public static final TagKey<Biome> IS_END = ofBiome("is_end");
    public static final TagKey<Biome> MORE_FREQUENT_DROWNED_SPAWNS = ofBiome("more_frequent_drowned_spawns");
    public static final TagKey<Biome> ONLY_ALLOWS_SNOW_AND_GOLD_RABBITS = ofBiome("only_allows_snow_and_gold_rabbits");
    public static final TagKey<Biome> PLAYS_UNDERWATER_MUSIC = ofBiome("plays_underwater_music");
    public static final TagKey<Biome> POLAR_BEARS_SPAWN_ON_ALTERNATE_BLOCKS = ofBiome("polar_bears_spawn_on_alternate_blocks");
    public static final TagKey<Biome> PRODUCES_CORALS_FROM_BONEMEAL = ofBiome("produces_corals_from_bonemeal");
    public static final TagKey<Biome> REDUCE_WATER_AMBIENT_SPAWNS = ofBiome("reduce_water_ambient_spawns");
    public static final TagKey<Biome> REQUIRED_OCEAN_MONUMENT_SURROUNDING = ofBiome("required_ocean_monument_surrounding");
    public static final TagKey<Biome> STRONGHOLD_BIASED_TO = ofBiome("stronghold_biased_to");
    public static final TagKey<Biome> SPAWNS_COLD_VARIANT_FROGS = ofBiome("spawns_cold_variant_frogs");
    public static final TagKey<Biome> SPAWNS_WARM_VARIANT_FROGS = ofBiome("spawns_warm_variant_frogs");
    public static final TagKey<Biome> WATER_ON_MAP_OUTLINES = ofBiome("water_on_map_outlines");
    public static final TagKey<Biome> WITHOUT_PATROL_SPAWNS = ofBiome("without_patrol_spawns");
    public static final TagKey<Biome> WITHOUT_WANDERING_TRADER_SPAWNS = ofBiome("without_wandering_trader_spawns");
    public static final TagKey<Biome> WITHOUT_ZOMBIE_SIEGES = ofBiome("without_zombie_sieges");

    private RegisterTags() {
    }

    private static TagKey<Block> of(String id) {
        return TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, id));
    }

    private static TagKey<Biome> ofBiome(String id) {
        return TagKey.of(Registry.BIOME_KEY, new Identifier(WildMod.MOD_ID, id));
    }

    private static TagKey<EntityType<?>> ofEntityType(String id) {
        return TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, id));
    }

    private static TagKey<GameEvent> ofEvent(String id) {
        return TagKey.of(Registry.GAME_EVENT_KEY, new Identifier(WildMod.MOD_ID, id));
    }

    public static final TagKey<Block> SCULK_VEIN_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk_vein_replaceable"));
    public static final TagKey<Block> SCULK_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk_replaceable"));
    public static final TagKey<Block> SCULK_REPLACEABLE_WORLD_GEN = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk_replaceable_world_gen"));
    public static final TagKey<Block> SCULK_UNTOUCHABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk_untouchable"));
    public static final TagKey<Block> SCULK = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk"));
    public static final TagKey<EntityType<?>> THREE = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "3xp"));
    public static final TagKey<EntityType<?>> FIVE = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "5xp"));
    public static final TagKey<EntityType<?>> TEN = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "10xp"));
    public static final TagKey<EntityType<?>> TWENTY = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "20xp"));
    public static final TagKey<EntityType<?>> FIFTY = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "50xp"));
    public static final TagKey<EntityType<?>> ONEHUNDRED = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "100xp"));
    public static final TagKey<EntityType<?>> DROPSXP = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "dropsxp"));
    public static final TagKey<Block> ALWAYS_WATER = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "always_water_sculk"));
    public static final TagKey<Block> WARDEN_UNSPAWNABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "warden_unspawnable"));
    public static final TagKey<Block> WARDEN_NON_COLLIDE = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "warden_non_collide"));

    //ACTIVATORS
    public static final TagKey<Block> ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk_activators"));
    public static final TagKey<Block> COMMON_ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "common_activators"));
    public static final TagKey<Block> RARE_ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "rare_activators"));
    public static final TagKey<Block> GROUND_ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "ground_activators"));
    public static final TagKey<Block> OCCLUDES_VIBRATION_SIGNALS = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "occludes_vibration_signals"));

    public static boolean blockTagContains(Block block1, TagKey<Block> tag) {
        for (RegistryEntry<Block> block : Registry.BLOCK.iterateEntries(tag)) {
            if (block.getKey().equals(Registry.BLOCK.getKey(block1))) {
                return true;
            }
        }
        return false;
    }

    public static Block getRandomBlock(Random random, TagKey<Block> tag) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (RegistryEntry<Block> block : Registry.BLOCK.iterateEntries(tag)) {
            if (block.getKey().isPresent()) {
                Optional<Block> block1 = Registry.BLOCK.getOrEmpty(block.getKey().get());
                block1.ifPresent(blocks::add);
            }
        }
        if (!blocks.isEmpty()) {
            return blocks.get((int) (Math.random() * blocks.size()));
        }
        return null;
    }

    public static boolean fluidTagContains(Fluid fluid1, TagKey<Fluid> tag) {
        for (RegistryEntry<Fluid> fluid : Registry.FLUID.iterateEntries(tag)) {
            if (fluid.getKey().equals(Registry.FLUID.getKey(fluid1))) {
                return true;
            }
        }
        return false;
    }

    public static boolean entityTagContains(EntityType<?> type, TagKey<EntityType<?>> tag) {
        for (RegistryEntry<EntityType<?>> entity : Registry.ENTITY_TYPE.iterateEntries(tag)) {
            if (entity.getKey().equals(Registry.ENTITY_TYPE.getKey(type))) {
                return true;
            }
        }
        return false;
    }

    public static boolean itemTagContains(Item item1, TagKey<Item> tag) {
        for (RegistryEntry<Item> item : Registry.ITEM.iterateEntries(tag)) {
            if (item.getKey().equals(Registry.ITEM.getKey(item1))) {
                return true;
            }
        }
        return false;
    }

    public static void init() {
    }
}
