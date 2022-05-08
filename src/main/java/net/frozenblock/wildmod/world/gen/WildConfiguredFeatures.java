package net.frozenblock.wildmod.world.gen;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

public class WildConfiguredFeatures {
    public static final RegistryEntry<ConfiguredFeature<SculkPatchFeatureConfig, ?>> SCULK_PATCH_DEEP_DARK = register(
            "sculk_patch_deep_dark", WildFeatures.SCULK_PATCH, new SculkPatchFeatureConfig(10, 32, 64, 0, 1, ConstantIntProvider.create(0), 0.5F)
    );
    public static final RegistryEntry<ConfiguredFeature<SculkPatchFeatureConfig, ?>> SCULK_PATCH_ANCIENT_CITY = register(
            "sculk_patch_ancient_city", WildFeatures.SCULK_PATCH, new SculkPatchFeatureConfig(10, 32, 64, 0, 1, UniformIntProvider.create(1, 3), 0.5F)
    );
    private static final AbstractLichenBlock SCULK_VEIN_BLOCK = (AbstractLichenBlock)RegisterBlocks.SCULK_VEIN;
    public static final RegistryEntry<ConfiguredFeature<MultifaceGrowthFeatureConfig, ?>> SCULK_VEIN = register(
            "sculk_vein",
            WildFeatures.MULTIFACE_GROWTH,
            new MultifaceGrowthFeatureConfig(
                    SCULK_VEIN_BLOCK,
                    20,
                    true,
                    true,
                    true,
                    1.0F,
                    RegistryEntryList.of(
                            Block::getRegistryEntry,
                            Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.DRIPSTONE_BLOCK, Blocks.CALCITE, Blocks.TUFF, Blocks.DEEPSLATE)
            )
    );

    public static final RegistryEntry<ConfiguredFeature<DiskFeatureConfig, ?>> DISK_GRASS = register(
        "disk_grass",
        WildFeatures.DISK,
        new DiskFeatureConfig(
            new PredicatedStateProvider(
                BlockStateProvider.of(Blocks.DIRT),
                List.of(
                    new PredicatedStateProvider.Rule(
                        BlockPredicate.not(
                            BlockPredicate.eitherOf(
                                BlockPredicate.solid(Direction.UP.getVector()),
                                BlockPredicate.matchingFluids(List.of(new Fluid[]{Fluids.WATER}), Direction.UP.getVector())
                            )
                        ),
                        BlockStateProvider.of(Blocks.GRASS_BLOCK)
                    )
                )
            ),
            BlockPredicate.matchingBlocks(List.of(Blocks.DIRT, RegisterBlocks.MUD)),
            UniformIntProvider.create(2, 6),
            2
        )
    );

    /*public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> TALL_MANGROVE = register(
            "tall_mangrove",
            Feature.TREE,
            new TreeFeatureConfig.Builder(
                    BlockStateProvider.of(MangroveWoods.MANGROVE_LOG),
                    new UpwardsBranchingTrunkPlacer(
                            4,
                            1,
                            9,
                            UniformIntProvider.create(1, 6),
                            0.5F,
                            UniformIntProvider.create(0, 1),
                            Registry.BLOCK.getOrCreateEntryList(RegisterTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
                    ),
                    BlockStateProvider.of(MangroveWoods.MANGROVE_LEAVES),
                    new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 70),
                    Optional.of(
                            new MangroveRootPlacer(
                                    UniformIntProvider.create(3, 7),
                                    BlockStateProvider.of(Blocks.MANGROVE_ROOTS),
                                    Optional.of(new AboveRootPlacement(BlockStateProvider.of(Blocks.MOSS_CARPET), 0.5F)),
                                    new MangroveRootPlacement(
                                            Registry.BLOCK.getOrCreateEntryList(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH),
                                            RegistryEntryList.of(Block::getRegistryEntry, new Block[]{Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS}),
                                            BlockStateProvider.of(Blocks.MUDDY_MANGROVE_ROOTS),
                                            8,
                                            15,
                                            0.2F
                                    )
                            )
                    ),
                    new TwoLayersFeatureSize(3, 0, 2)
            )
                    .decorators(
                            List.of(
                                    new LeavesVineTreeDecorator(0.125F),
                                    new AttachedToLeavesTreeDecorator(
                                            0.14F,
                                            1,
                                            0,
                                            new RandomizedIntBlockStateProvider(
                                                    BlockStateProvider.of((BlockState)Blocks.MANGROVE_PROPAGULE.getDefaultState().with(PropaguleBlock.HANGING, true)),
                                                    PropaguleBlock.AGE,
                                                    UniformIntProvider.create(0, 4)
                                            ),
                                            2,
                                            List.of(Direction.DOWN)
                                    ),
                                    BEES_001
                            )
                    )
                    .ignoreVines()
                    .build()
    );

    */public static <V extends T, T> RegistryEntry<V> method_40360(Registry<T> registry, String id, V value) {
        return (RegistryEntry<V>) BuiltinRegistries.add(registry, new Identifier(WildMod.MOD_ID, id), value);
    }

    public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<FC, ?>> register(String id, F feature, FC config) {
        return method_40360(BuiltinRegistries.CONFIGURED_FEATURE, id, new ConfiguredFeature<>(feature, config));
    }

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return (F)Registry.register(Registry.FEATURE, new Identifier(WildMod.MOD_ID, name), feature);
    }
}
