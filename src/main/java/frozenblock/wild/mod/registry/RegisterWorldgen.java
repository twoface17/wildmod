package frozenblock.wild.mod.registry;

import com.google.common.collect.ImmutableList;
import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.mixins.TreeDecoratorTypeInvoker;
import frozenblock.wild.mod.worldgen.MangroveTreeDecorator;
import frozenblock.wild.mod.worldgen.SculkPatchFeature;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import org.jetbrains.annotations.Nullable;

public class RegisterWorldgen {

    @Nullable
    private static final MusicSound DEFAULT_MUSIC = null;

    public static final RegistryKey<Biome> MANGROVE_SWAMP = register("mangrove_swamp");
    public static final RegistryKey<Biome> DEEP_DARK = register("deep_dark");

    private static final Feature<DefaultFeatureConfig> SCULK_PATCH = new SculkPatchFeature(DefaultFeatureConfig.CODEC);
    public static PlacedFeature TREES_MANGROVE;
    public static PlacedFeature SCULK_PATCH_PLACED_FEATURE;
    public static ConfiguredFeature<TreeFeatureConfig, ?> MANGROVE;
    public static ConfiguredFeature<TreeFeatureConfig, ?> BIRCH_NEW;
    public static ConfiguredFeature<DefaultFeatureConfig, ?> SCULK;

    public static final TreeDecoratorType<MangroveTreeDecorator> MANGROVE_TREE_DECORATOR = TreeDecoratorTypeInvoker.callRegister("rich_tree_decorator", MangroveTreeDecorator.CODEC);

    private static RegistryKey<Biome> register(String name) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(WildMod.MOD_ID, name));
    }

    public static Biome createDeepDark() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();;
        net.minecraft.world.biome.GenerationSettings.Builder builder2 = new net.minecraft.world.biome.GenerationSettings.Builder();
        DefaultBiomeFeatures.addFossils(builder2);
        addBasicFeatures(builder2);
        DefaultBiomeFeatures.addPlainsTallGrass(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, SCULK_PATCH_PLACED_FEATURE);
        MusicSound musicSound = MusicType.createIngameMusic(RegisterSounds.MUSIC_OVERWORLD_DEEP_DARK);
        return (
                new net.minecraft.world.biome.Biome.Builder())
                .precipitation(Biome.Precipitation.NONE)
                .category(Biome.Category.UNDERGROUND)
                .temperature(0.8F).downfall(0.9F)
                .effects((new net.minecraft.world.biome.BiomeEffects.Builder())
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(12638463)
                        .skyColor(getSkyColor(0.8F))
                        .foliageColor(FoliageColors.getDefaultColor())
                        .grassColorModifier(BiomeEffects.GrassColorModifier.NONE)
                        .music(musicSound)
                        .moodSound(BiomeMoodSound.CAVE).build())
                .spawnSettings(builder.build())
                .generationSettings(builder2.build()).build();

    }

    public static Biome createMangroveSwamp() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        net.minecraft.world.biome.GenerationSettings.Builder builder2 = new net.minecraft.world.biome.GenerationSettings.Builder();
        DefaultBiomeFeatures.addFossils(builder2);
        addBasicFeatures(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addClayDisk(builder2);
        addMangroveSwampFeatures(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addSwampVegetation(builder2);
        builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_SWAMP);
        return (
                new net.minecraft.world.biome.Biome.Builder())
                .precipitation(Biome.Precipitation.RAIN)
                .category(Biome.Category.SWAMP)
                .temperature(0.8F).downfall(0.9F)
                .effects((new net.minecraft.world.biome.BiomeEffects.Builder())
                        .waterColor(0x397d71)
                        .waterFogColor(0x397d71)
                        .fogColor(12638463)
                        .skyColor(getSkyColor(0.8F))
                        .foliageColor(6975545)
                        .grassColorModifier(BiomeEffects.GrassColorModifier.SWAMP)
                        .moodSound(BiomeMoodSound.CAVE).build())
                .spawnSettings(builder.build())
                .generationSettings(builder2.build()).build();

    }

    public static void addMangroveSwampFeatures(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, TREES_MANGROVE);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_WATERLILY);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BROWN_MUSHROOM_SWAMP);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.RED_MUSHROOM_SWAMP);
    }

    private static void addBasicFeatures(net.minecraft.world.biome.GenerationSettings.Builder generationSettings) {
        DefaultBiomeFeatures.addLandCarvers(generationSettings);
        DefaultBiomeFeatures.addAmethystGeodes(generationSettings);
        DefaultBiomeFeatures.addDungeons(generationSettings);
        DefaultBiomeFeatures.addMineables(generationSettings);
        DefaultBiomeFeatures.addSprings(generationSettings);
        DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);
    }

    protected static int getSkyColor(float temperature) {
        float f = temperature / 3.0F;
        f = MathHelper.clamp(f, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
    }

    public static void RegisterWorldgen() {
        Registry.register(Registry.FEATURE, new Identifier(WildMod.MOD_ID, "sculk_patch_feature"), SCULK_PATCH);

        MANGROVE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(WildMod.MOD_ID, "mangrove"), Feature.TREE
                .configure(new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(MangroveWoods.MANGROVE_LOG),
                        new StraightTrunkPlacer(8, 3, 0), BlockStateProvider.of(MangroveWoods.MANGROVE_LEAVES),
                        new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(3), 100),
                        new TwoLayersFeatureSize(1, 0, 2)).ignoreVines()
                        .decorators(ImmutableList.of(MangroveTreeDecorator.INSTANCE))
                        .build()));
        BIRCH_NEW = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(WildMod.MOD_ID, "birch"), Feature.TREE
                .configure(new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(MangroveWoods.MANGROVE_LOG),
                        new StraightTrunkPlacer(7, 3, 9), BlockStateProvider.of(Blocks.BIRCH_LEAVES),
                        new BlobFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), 10),
                        new TwoLayersFeatureSize(1, 0, 2)).ignoreVines()
                        .build()));

        SCULK = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(WildMod.MOD_ID, "sculk_patch"), SCULK_PATCH.configure(new DefaultFeatureConfig()));
        TREES_MANGROVE = Registry.register(BuiltinRegistries.PLACED_FEATURE, new Identifier(WildMod.MOD_ID, "trees_mangrove"), MANGROVE.withPlacement(PlacedFeatures.createCountExtraModifier(6, 0.1f, 1), SquarePlacementModifier.of(), SurfaceWaterDepthFilterPlacementModifier.of(6), PlacedFeatures.OCEAN_FLOOR_HEIGHTMAP, BiomePlacementModifier.of(), BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(MangroveWoods.MANGROVE_PROPAGULE.getDefaultState(), BlockPos.ORIGIN))));
        SCULK_PATCH_PLACED_FEATURE = Registry.register(BuiltinRegistries.PLACED_FEATURE, new Identifier(WildMod.MOD_ID, "sculk_patch"), SCULK.withPlacement(PlacedFeatures.createCountExtraModifier(15, 0.1f, 3), SquarePlacementModifier.of(), HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)), EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR, 12), BiomePlacementModifier.of()));
        BuiltinRegistries.add(BuiltinRegistries.BIOME, MANGROVE_SWAMP, createMangroveSwamp());
        BuiltinRegistries.add(BuiltinRegistries.BIOME, DEEP_DARK, createDeepDark());
    }
}
