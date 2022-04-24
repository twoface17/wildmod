package frozenblock.wild.mod.world.gen;

import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class WildConfiguredFeatures {
    public static final RegistryEntry<ConfiguredFeature<SculkPatchFeatureConfig, ?>> SCULK_PATCH_DEEP_DARK = ConfiguredFeatures.register(
            "sculk_patch_deep_dark", WildFeatures.SCULK_PATCH, new SculkPatchFeatureConfig(10, 32, 64, 0, 1, ConstantIntProvider.create(0), 0.5F)
    );
    public static final RegistryEntry<ConfiguredFeature<SculkPatchFeatureConfig, ?>> SCULK_PATCH_ANCIENT_CITY = ConfiguredFeatures.register(
            "sculk_patch_ancient_city", WildFeatures.SCULK_PATCH, new SculkPatchFeatureConfig(10, 32, 64, 0, 1, UniformIntProvider.create(1, 3), 0.5F)
    );
    private static final AbstractLichenBlock SCULK_VEIN_BLOCK = (AbstractLichenBlock)RegisterBlocks.SCULK_VEIN;
    public static final RegistryEntry<ConfiguredFeature<MultifaceGrowthFeatureConfig, ?>> SCULK_VEIN = ConfiguredFeatures.register(
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
}
