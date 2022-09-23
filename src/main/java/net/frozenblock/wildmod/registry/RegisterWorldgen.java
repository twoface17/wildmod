package net.frozenblock.wildmod.registry;

import com.google.common.collect.ImmutableList;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.world.feature.WildConfiguredFeatures;
import net.frozenblock.wildmod.world.feature.WildPlacedFeatures;
import net.frozenblock.wildmod.world.gen.structure.BlockRotStructureProcessor;
import net.frozenblock.wildmod.world.gen.structure.ancientcity.AncientCityGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.processor.*;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.placementmodifier.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

import java.util.List;

public class RegisterWorldgen {

    public static final RegistryKey<Biome> MANGROVE_SWAMP = register("mangrove_swamp");
    public static final RegistryKey<Biome> DEEP_DARK = register("deep_dark");

    public static final RegistryEntry<PlacedFeature> TREES_MANGROVE = WildPlacedFeatures.register("trees_mangrove", WildConfiguredFeatures.MANGROVE_VEGETATION, CountPlacementModifier.of(30), SquarePlacementModifier.of(), SurfaceWaterDepthFilterPlacementModifier.of(5), PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP, BiomePlacementModifier.of(), BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(MangroveWoods.MANGROVE_PROPAGULE.getDefaultState(), BlockPos.ORIGIN)));

    public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> BIRCH_NEW = WildConfiguredFeatures.register("birch", Feature.TREE, new TreeFeatureConfig.Builder(
            BlockStateProvider.of(MangroveWoods.MANGROVE_LOG),
            new StraightTrunkPlacer(7, 3, 9), BlockStateProvider.of(Blocks.BIRCH_LEAVES),
            new BlobFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), 10),
            new TwoLayersFeatureSize(1, 0, 2)).ignoreVines()
            .build());

    private static RegistryEntry<StructureProcessorList> registerList(String id, ImmutableList<StructureProcessor> processorList) {
        Identifier identifier = new Identifier(WildMod.MOD_ID, id);
        StructureProcessorList structureProcessorList = new StructureProcessorList(processorList);
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_PROCESSOR_LIST, identifier, structureProcessorList);
    }

    /*private static RegistryEntry<StructureType> register(RegistryKey<StructureType> key, StructureType configuredStructureFeature) {
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE, key, configuredStructureFeature);
    }

    private static Config createConfig(
        TagKey<Biome> biomeTag, Map<SpawnGroup, StructureSpawns> spawns, Feature featureStep, StructureTerrainAdaptation terrainAdaptation
    ) {
        return new Config(getOrCreateBiomeTag(biomeTag), spawns, featureStep, terrainAdaptation);
    }

    */public static final RegistryEntry<StructureProcessorList> ANCIENT_CITY_START_DEGRADATION = registerList(
            "ancient_city_start_degradation",
            ImmutableList.of(
                    new RuleStructureProcessor(
                            ImmutableList.of(
                                    new StructureProcessorRule(
                                            new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_BRICKS, 0.3F),
                                            AlwaysTrueRuleTest.INSTANCE,
                                            Blocks.CRACKED_DEEPSLATE_BRICKS.getDefaultState()
                                    ),
                                    new StructureProcessorRule(
                                            new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILES, 0.3F),
                                            AlwaysTrueRuleTest.INSTANCE,
                                            Blocks.CRACKED_DEEPSLATE_TILES.getDefaultState()
                                    ),
                                    new StructureProcessorRule(
                                            new RandomBlockMatchRuleTest(Blocks.SOUL_LANTERN, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
                                    )
                            )
                    ),
                    new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE)
            )
    );
    public static final RegistryEntry<StructureProcessorList> ANCIENT_CITY_GENERIC_DEGRADATION = registerList(
            "ancient_city_generic_degradation",
            ImmutableList.of(
                    new BlockRotStructureProcessor(RegisterTags.ANCIENT_CITY_REPLACEABLE, 0.95F),
                    new RuleStructureProcessor(
                            ImmutableList.of(
                                    new StructureProcessorRule(
                                            new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_BRICKS, 0.3F),
                                            AlwaysTrueRuleTest.INSTANCE,
                                            Blocks.CRACKED_DEEPSLATE_BRICKS.getDefaultState()
                                    ),
                                    new StructureProcessorRule(
                                            new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILES, 0.3F),
                                            AlwaysTrueRuleTest.INSTANCE,
                                            Blocks.CRACKED_DEEPSLATE_TILES.getDefaultState()
                                    ),
                                    new StructureProcessorRule(
                                            new RandomBlockMatchRuleTest(Blocks.SOUL_LANTERN, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
                                    )
                            )
                    ),
                    new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE)
            )
    );
    public static final RegistryEntry<StructureProcessorList> ANCIENT_CITY_WALLS_DEGRADATION = registerList(
            "ancient_city_walls_degradation",
            ImmutableList.of(
                    new BlockRotStructureProcessor(RegisterTags.ANCIENT_CITY_REPLACEABLE, 0.95F),
                    new RuleStructureProcessor(
                            ImmutableList.of(
                                    new StructureProcessorRule(
                                            new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_BRICKS, 0.3F),
                                            AlwaysTrueRuleTest.INSTANCE,
                                            Blocks.CRACKED_DEEPSLATE_BRICKS.getDefaultState()
                                    ),
                                    new StructureProcessorRule(
                                            new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILES, 0.3F),
                                            AlwaysTrueRuleTest.INSTANCE,
                                            Blocks.CRACKED_DEEPSLATE_TILES.getDefaultState()
                                    ),
                                    new StructureProcessorRule(
                                            new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILE_SLAB, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
                                    ),
                                    new StructureProcessorRule(
                                            new RandomBlockMatchRuleTest(Blocks.SOUL_LANTERN, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()
                                    )
                            )
                    ),
                    new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE)
            )
    );

    public static final StructureFeature<StructurePoolFeatureConfig> ANCIENT_CITY_POOL = register(
            "ancient_city", new JigsawFeature(StructurePoolFeatureConfig.CODEC, -51, true, false, context -> true), GenerationStep.Feature.SURFACE_STRUCTURES
    );

    public static final RegistryKey<StructureSet> ANCIENT_CITIES_KEY = RegistryKey.of(Registry.STRUCTURE_SET_KEY, new Identifier(WildMod.MOD_ID, "ancient_cities"));

    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> ANCIENT_CITY_CONFIGURED_KEY = RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(WildMod.MOD_ID, "ancient_city"));;

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> ANCIENT_CITY_CONFIGURED = register(
            ANCIENT_CITY_CONFIGURED_KEY,
            ANCIENT_CITY_POOL.configure(new StructurePoolFeatureConfig(AncientCityGenerator.CITY_CENTER, 7), RegisterTags.ANCIENT_CITY_HAS_STRUCTURE)
    );

    public static final RegistryEntry<StructureSet> ANCIENT_CITIES = register(
            ANCIENT_CITIES_KEY,
            ANCIENT_CITY_CONFIGURED,
            new RandomSpreadStructurePlacement(24, 8, SpreadType.LINEAR, 20083232)
    );

    private static <FC extends FeatureConfig, F extends StructureFeature<FC>> RegistryEntry<ConfiguredStructureFeature<?, ?>> register(
            RegistryKey<ConfiguredStructureFeature<?, ?>> key, ConfiguredStructureFeature<FC, F> configuredStructureFeature
    ) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, key, configuredStructureFeature);
    }

    private static <F extends StructureFeature<?>> F register(String name, F structureFeature, GenerationStep.Feature step) {
        StructureFeature.STRUCTURE_TO_GENERATION_STEP.put(structureFeature, step);
        return Registry.register(Registry.STRUCTURE_FEATURE, new Identifier(WildMod.MOD_ID, name), structureFeature);
    }

    static RegistryEntry<StructureSet> register(RegistryKey<StructureSet> key, StructureSet structureSet) {
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_SET, key, structureSet);
    }

    static RegistryEntry<StructureSet> register(
            RegistryKey<StructureSet> key, RegistryEntry<ConfiguredStructureFeature<?, ?>> configuredStructureFigure, StructurePlacement placement
    ) {
        return register(key, new StructureSet(configuredStructureFigure, placement));
    }

    /*public static final RegistryEntry<Structure> ANCIENT_CITY = register(
                    StructureTypeKeys.ANCIENT_CITY,
                    new JigsawStructure(
                            createConfig(
                                    RegisterTags.ANCIENT_CITY_HAS_STRUCTURE,
                                    (Map<SpawnGroup, StructureSpawns>) Arrays.stream(SpawnGroup.values())
                                            .collect(Collectors.toMap(spawnGroup -> spawnGroup, spawnGroup -> new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.empty()))),
                                    GenerationStep.Feature.UNDERGROUND_DECORATION,
                                    StructureTerrainAdaptation.BEARD_BOX
                            ),
                            AncientCityGenerator.CITY_CENTER,
                            Optional.of(new Identifier("city_anchor")),
                            7,
                            ConstantHeightProvider.create(YOffset.fixed(-27)),
                            false,
                            Optional.empty(),
                            116
                    )
    );*/

    private static RegistryKey<Biome> register(String name) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(WildMod.MOD_ID, name));
    }

    public static Biome createDeepDark() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        builder2.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE);
        builder2.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE_EXTRA_UNDERGROUND);
        builder2.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CANYON);
        DefaultBiomeFeatures.addAmethystGeodes(builder2);
        DefaultBiomeFeatures.addDungeons(builder2);
        DefaultBiomeFeatures.addMineables(builder2);
        DefaultBiomeFeatures.addFrozenTopLayer(builder2);
        DefaultBiomeFeatures.addPlainsTallGrass(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2, true);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addPlainsFeatures(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        addSculk(builder2);
        MusicSound musicSound = MusicType.createIngameMusic(RegisterSounds.MUSIC_OVERWORLD_DEEP_DARK);
        return (
                new Biome.Builder())
                .precipitation(Biome.Precipitation.RAIN)
                .category(Biome.Category.UNDERGROUND)
                .temperature(0.8F).downfall(0.4F)
                .effects((new BiomeEffects.Builder())
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(12638463)
                        .skyColor(getSkyColor(0.8F))
                        .foliageColor(FoliageColors.getDefaultColor())
                        .grassColorModifier(BiomeEffects.GrassColorModifier.NONE)
                        .moodSound(BiomeMoodSound.CAVE)
                        .music(musicSound)
                        //.loopSound(RegisterSounds.AMBIENT_DEEP_DARK_LOOP)
                        //.additionsSound(new BiomeAdditionsSound(RegisterSounds.AMBIENT_DEEP_DARK_ADDITIONS, 0.0072D))
                        .moodSound(BiomeMoodSound.CAVE).build())
                .spawnSettings(builder.build())
                .generationSettings(builder2.build()).build();

    }

    public static Biome createMangroveSwamp() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addBatsAndMonsters(builder);
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 1, 1, 1));
        builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        DefaultBiomeFeatures.addFossils(builder2);
        addBasicFeatures(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        addGrassAndClayDisks(builder2);
        addMangroveSwampFeatures(builder2);
        builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_SWAMP);
        MusicSound musicSound = MusicType.createIngameMusic(RegisterSounds.MUSIC_OVERWORLD_SWAMP);
        return new net.minecraft.world.biome.Biome.Builder()
                .precipitation(Biome.Precipitation.RAIN)
                .category(Biome.Category.SWAMP)
                .temperature(0.8F)
                .downfall(0.9F)
                .effects(
                        new net.minecraft.world.biome.BiomeEffects.Builder()
                                .waterColor(3832426)
                                .waterFogColor(5077600)
                                .fogColor(12638463)
                                .skyColor(getSkyColor(0.8F))
                                .foliageColor(9285927)
                                .grassColorModifier(BiomeEffects.GrassColorModifier.SWAMP)
                                .moodSound(BiomeMoodSound.CAVE)
                                .music(musicSound)
                                .build()
                )
                .spawnSettings(builder.build())
                .generationSettings(builder2.build())
                .build();
    }

    private static void addBasicFeatures(GenerationSettings.Builder generationSettings) {
        DefaultBiomeFeatures.addLandCarvers(generationSettings);
        DefaultBiomeFeatures.addAmethystGeodes(generationSettings);
        DefaultBiomeFeatures.addDungeons(generationSettings);
        DefaultBiomeFeatures.addMineables(generationSettings);
        DefaultBiomeFeatures.addSprings(generationSettings);
        DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);
    }

    private static void addGrassAndClayDisks(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, WildPlacedFeatures.DISK_GRASS);
        builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, MiscPlacedFeatures.DISK_CLAY);
    }

    private static void addMangroveSwampFeatures(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, TREES_MANGROVE);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_NORMAL);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_DEAD_BUSH);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_WATERLILY);
    }

    private static void addSculk(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, WildPlacedFeatures.SCULK_VEIN);
        builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, WildPlacedFeatures.SCULK_PATCH_DEEP_DARK);
    }

    protected static int getSkyColor(float temperature) {
        float f = temperature / 3.0F;
        f = MathHelper.clamp(f, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
    }

    public static void registerWorldgen() {
        BuiltinRegistries.add(BuiltinRegistries.BIOME, MANGROVE_SWAMP, createMangroveSwamp());
        BuiltinRegistries.add(BuiltinRegistries.BIOME, DEEP_DARK, createDeepDark());
    }
}
