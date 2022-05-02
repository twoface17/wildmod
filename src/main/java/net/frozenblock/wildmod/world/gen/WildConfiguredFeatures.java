package net.frozenblock.wildmod.world.gen;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.feature.*;

public class WildConfiguredFeatures {
    public static final RegistryEntry<ConfiguredFeature<SculkPatchFeatureConfig, ?>> SCULK_PATCH_DEEP_DARK = ConfiguredFeatures.register(
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
                            new Block[]{
                                    Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.DRIPSTONE_BLOCK, Blocks.CALCITE, Blocks.TUFF, Blocks.DEEPSLATE
                            }
                    )
            )

    );



    public static <V extends T, T> RegistryEntry<V> method_40360(Registry<T> registry, String id, V value) {
        return (RegistryEntry<V>) BuiltinRegistries.add(registry, new Identifier(WildMod.MOD_ID, id), value);
    }

    public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<FC, ?>> register(String id, F feature, FC config) {
        return method_40360(BuiltinRegistries.CONFIGURED_FEATURE, id, new ConfiguredFeature<>(feature, config));
    }
}
