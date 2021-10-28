package frozenblock.wild.mod.worldgen;

import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class MangroveSwamps {

    public static void addMangroveSwampFeatures(GenerationSettings.Builder builder) {
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, MangroveTree.MANGROVE_TREE);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, MangroveTree.MANGROVE_TREE);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, MangroveTree.MANGROVE_TREE);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, MangroveTree.MANGROVE_TREE);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, MangroveTree.MANGROVE_TREE);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_WATERLILLY);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_SWAMP);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_SWAMP);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE_SWAMP);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
        builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.KELP_COLD);
        builder.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_WATER);
    }

    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> MANGROVE_SWAMPS_BUILDER = SurfaceBuilder.DEFAULT
            .withConfig(new TernarySurfaceConfig(
                    RegisterBlocks.MUD_BLOCK.getDefaultState(),
                    RegisterBlocks.MUD_BLOCK.getDefaultState(),
                    Blocks.GRAVEL.getDefaultState()));

    public static Biome createMangroveSwamps() {
        // We specify what entities spawn and what features generate in the biome.
        // Aside from some structures, trees, rocks, plants and
        //   custom entities, these are mostly the same for each biome.
        // Vanilla configured features for biomes are defined in DefaultBiomeFeatures.

        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(spawnSettings);
        DefaultBiomeFeatures.addMonsters(spawnSettings, 95, 5, 100);

        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
        generationSettings.surfaceBuilder(MANGROVE_SWAMPS_BUILDER);
        DefaultBiomeFeatures.addDefaultUndergroundStructures(generationSettings);
        DefaultBiomeFeatures.addLandCarvers(generationSettings);
        DefaultBiomeFeatures.addDungeons(generationSettings);
        DefaultBiomeFeatures.addMineables(generationSettings);
        DefaultBiomeFeatures.addDefaultOres(generationSettings);
        DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);
        DefaultBiomeFeatures.addDesertLakes(generationSettings);
        MangroveSwamps.addMangroveSwampFeatures(generationSettings);


        return (new Biome.Builder())
                .precipitation(Biome.Precipitation.RAIN)
                .category(Biome.Category.SWAMP)
                .depth(-0.15F)
                .scale(0.1F)
                .temperature(0.8F)
                .downfall(0.4F)
                .effects((new BiomeEffects.Builder())
                        .waterColor(0x617B64)
                        .waterFogColor(0x232317)
                        .foliageColor(0x6A7039)
                        .grassColor(0x6A7039)
                        .fogColor(0xc0d8ff)
                        .skyColor(0x78a7ff)
                        .grassColorModifier(BiomeEffects.GrassColorModifier.SWAMP)
                        .build())
                .spawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();
    }
}
