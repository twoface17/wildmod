package frozenblock.wild.mod.worldgen;

import com.mojang.serialization.Codec;
import frozenblock.wild.mod.registry.MangroveWoods;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.treedecorator.LeavesVineTreeDecorator;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Random;

public class MangroveTree {
    @Nullable
    public static final ConfiguredFeature<?, ?> MANGROVE_TREE = Feature.TREE
            // Configure the feature using the builder
            .configure(new TreeFeatureConfig.Builder(
                    new SimpleBlockStateProvider(MangroveWoods.MANGROVE_LOG.getDefaultState()), // Trunk block provider
                    new ForkingTrunkPlacer(5, 1, 5), // places a forked trunk
                    new SimpleBlockStateProvider(MangroveWoods.MANGROVE_LEAVES.getDefaultState()), // Foliage block provider
                    new SimpleBlockStateProvider(MangroveWoods.MANGROVE_SAPLING.getDefaultState()), // Sapling provider; used to determine what blocks the tree can generate on
                    new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 50), // places leaves as a blob (radius, offset from trunk, height)
                    new TwoLayersFeatureSize(1, 0, 1) // The width of the tree at different layers; used to see how tall the tree can be without clipping into blocks

            )
                    .decorators(Collections.singletonList(LeavesVineTreeDecorator.INSTANCE))
                    //.decorators(Collections.singletonList(MangroveTreeRoots.INSTANCE))
                    .build())
            .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
            .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.OCEAN_FLOOR)))
            .spreadHorizontally()
            .applyChance(1); // About a 33% chance to generate per chunk (1/x)


}

