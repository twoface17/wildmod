package frozenblock.wild.mod.worldgen;

import frozenblock.wild.mod.registry.MangroveWoods;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.*;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.treedecorator.LeavesVineTreeDecorator;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class MangroveTree {
    @Nullable
    public static final ConfiguredFeature<?, ?> MANGROVE_TREE = Feature.TREE
            // Configure the feature using the builder
            .configure(new TreeFeatureConfig.Builder(
                    new SimpleBlockStateProvider(MangroveWoods.MANGROVE_LOG.getDefaultState()), // Trunk block provider
                    new ForkingTrunkPlacer(5, 1, 5), // places a forked trunk
                    new SimpleBlockStateProvider(MangroveWoods.MANGROVE_LEAVES.getDefaultState()), // Foliage block provider
                    new SimpleBlockStateProvider(MangroveWoods.MANGROVE_PROPAGULE.getDefaultState()), // Sapling provider; used to determine what blocks the tree can generate on
                    new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(3), 75), // places leaves as a blob (radius, offset from trunk, height)
                    new TwoLayersFeatureSize(1, 0, 1) // The width of the tree at different layers; used to see how tall the tree can be without clipping into blocks

            )
                    .decorators(Collections.singletonList(MangroveTreeDecorator.INSTANCE))
                    .build())
            .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.OCEAN_FLOOR)))
            .decorate(Decorator.ICEBERG.configure(new NopeDecoratorConfig()))
            .decorate(Decorator.WATER_DEPTH_THRESHOLD.configure(new WaterDepthThresholdDecoratorConfig(5)))
            .repeatRandomly(30)
            .spreadHorizontally()
            .applyChance(1); // About a 33% chance to generate per chunk (1/x)

    public static final ConfiguredFeature<?, ?> MANGROVE_TREE_PLANTABLE = Feature.TREE
            .configure(new TreeFeatureConfig.Builder(
                    new SimpleBlockStateProvider(MangroveWoods.MANGROVE_LOG.getDefaultState()), // Trunk block provider
                    new ForkingTrunkPlacer(5, 1, 5), // places a forked trunk
                    new SimpleBlockStateProvider(MangroveWoods.MANGROVE_LEAVES.getDefaultState()), // Foliage block provider
                    new SimpleBlockStateProvider(MangroveWoods.MANGROVE_PROPAGULE.getDefaultState()), // Sapling provider; used to determine what blocks the tree can generate on
                    new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(3), 75), // places leaves as a blob (radius, offset from trunk, height)
                    new TwoLayersFeatureSize(1, 0, 1) // The width of the tree at different layers; used to see how tall the tree can be without clipping into blocks

            )
                    .decorators(Collections.singletonList(MangroveTreeDecorator.INSTANCE))
                    .build())
            .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.OCEAN_FLOOR)))
            .decorate(Decorator.ICEBERG.configure(new NopeDecoratorConfig()))
            .decorate(Decorator.WATER_DEPTH_THRESHOLD.configure(new WaterDepthThresholdDecoratorConfig(5))); // About a 33% chance to generate per chunk (1/x)


}

