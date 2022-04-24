package net.frozenblock.wildmod.tags;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class BiomeTags {
    public static final TagKey<Biome> ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT;
    public static final TagKey<Biome> ALLOWS_SURFACE_SLIME_SPAWNS;
    public static final TagKey<Biome> ANCIENT_CITY_HAS_STRUCTURE;
    public static final TagKey<Biome> HAS_CLOSER_WATER_FOG;
    public static final TagKey<Biome> IS_DEEP_OCEAN;
    public static final TagKey<Biome> IS_OCEAN;
    public static final TagKey<Biome> IS_BEACH;
    public static final TagKey<Biome> IS_RIVER;
    public static final TagKey<Biome> IS_MOUNTAIN;
    public static final TagKey<Biome> IS_BADLANDS;
    public static final TagKey<Biome> IS_HILL;
    public static final TagKey<Biome> IS_TAIGA;
    public static final TagKey<Biome> IS_JUNGLE;
    public static final TagKey<Biome> IS_FOREST;
    public static final TagKey<Biome> IS_SAVANNA;
    public static final TagKey<Biome> IS_OVERWORLD;
    public static final TagKey<Biome> IS_NETHER;
    public static final TagKey<Biome> IS_END;
    public static final TagKey<Biome> MORE_FREQUENT_DROWNED_SPAWNS;
    public static final TagKey<Biome> ONLY_ALLOWS_SNOW_AND_GOLD_RABBITS;
    public static final TagKey<Biome> PLAYS_UNDERWATER_MUSIC;
    public static final TagKey<Biome> POLAR_BEARS_SPAWN_ON_ALTERNATE_BLOCKS;
    public static final TagKey<Biome> PRODUCES_CORALS_FROM_BONEMEAL;
    public static final TagKey<Biome> REDUCE_WATER_AMBIENT_SPAWNS;
    public static final TagKey<Biome> REQUIRED_OCEAN_MONUMENT_SURROUNDING;
    public static final TagKey<Biome> STRONGHOLD_BIASED_TO;
    public static final TagKey<Biome> SPAWNS_COLD_VARIANT_FROGS;
    public static final TagKey<Biome> SPAWNS_WARM_VARIANT_FROGS;
    public static final TagKey<Biome> WATER_ON_MAP_OUTLINES;
    public static final TagKey<Biome> WITHOUT_PATROL_SPAWNS;
    public static final TagKey<Biome> WITHOUT_WANDERING_TRADER_SPAWNS;
    public static final TagKey<Biome> WITHOUT_ZOMBIE_SIEGES;

    private BiomeTags() {
    }

    private static TagKey<Biome> of(final String id) {
        return TagKey.of(Registry.BIOME_KEY, new Identifier(id));
    }

    static {
        ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT = of("allows_tropical_fish_spawns_at_any_height");
        ALLOWS_SURFACE_SLIME_SPAWNS = of("allows_surface_slime_spawns");
        ANCIENT_CITY_HAS_STRUCTURE = of("ancient_city_has_structure");
        HAS_CLOSER_WATER_FOG = of("has_closer_water_fog");
        IS_DEEP_OCEAN = of("is_deep_ocean");
        IS_OCEAN = of("is_ocean");
        IS_BEACH = of("is_beach");
        IS_RIVER = of("is_river");
        IS_MOUNTAIN = of("is_mountain");
        IS_BADLANDS = of("is_badlands");
        IS_HILL = of("is_hill");
        IS_TAIGA = of("is_taiga");
        IS_JUNGLE = of("is_jungle");
        IS_FOREST = of("is_forest");
        IS_SAVANNA = of("is_savanna");
        IS_OVERWORLD = of("is_overworld");
        IS_NETHER = of("is_nether");
        IS_END = of("is_end");
        MORE_FREQUENT_DROWNED_SPAWNS = of("more_frequent_drowned_spawns");
        ONLY_ALLOWS_SNOW_AND_GOLD_RABBITS = of("only_allows_snow_and_gold_rabbits");
        PLAYS_UNDERWATER_MUSIC = of("plays_underwater_music");
        POLAR_BEARS_SPAWN_ON_ALTERNATE_BLOCKS = of("polar_bears_spawn_on_alternate_blocks");
        PRODUCES_CORALS_FROM_BONEMEAL = of("produces_corals_from_bonemeal");
        REDUCE_WATER_AMBIENT_SPAWNS = of("reduce_water_ambient_spawns");
        REQUIRED_OCEAN_MONUMENT_SURROUNDING = of("required_ocean_monument_surrounding");
        SPAWNS_COLD_VARIANT_FROGS = of("spawns_cold_variant_frogs");
        SPAWNS_WARM_VARIANT_FROGS = of("spawns_warm_variant_frogs");
        STRONGHOLD_BIASED_TO = of("stronghold_biased_to");
        WATER_ON_MAP_OUTLINES = of("water_on_map_outlines");
        WITHOUT_PATROL_SPAWNS = of("without_patrol_spawns");
        WITHOUT_WANDERING_TRADER_SPAWNS = of("without_wandering_trader_spawns");
        WITHOUT_ZOMBIE_SIEGES = of("without_zombie_sieges");
    }
}
