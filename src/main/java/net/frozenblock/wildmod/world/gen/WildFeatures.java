package net.frozenblock.wildmod.world.gen;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class WildFeatures<FC extends FeatureConfig> {
    public static Feature<SculkPatchFeatureConfig> SCULK_PATCH;
    public static final Feature<MultifaceGrowthFeatureConfig> MULTIFACE_GROWTH = register(
            "multiface_growth", new MultifaceGrowthFeature(MultifaceGrowthFeatureConfig.CODEC)
    );

    private static <C extends FeatureConfig, F extends Feature<C>> F register(final String name, final F feature) {
        return Registry.register(Registry.FEATURE, name, feature);
    }

    static {
        SCULK_PATCH = register("sculk_patch", new SculkPatchFeature(SculkPatchFeatureConfig.CODEC));
    }
}
