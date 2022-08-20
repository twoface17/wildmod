package net.frozenblock.wildmod.world.feature;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.world.feature.features.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.GlowLichenFeatureConfig;

public class WildFeatures<FC extends FeatureConfig> {

    public static final Feature<WildTreeFeatureConfig> TREE = register("tree", new WildTreeFeature(WildTreeFeatureConfig.CODEC));

    public static Feature<SculkPatchFeatureConfig> SCULK_PATCH = register("sculk_patch", new SculkPatchFeature(SculkPatchFeatureConfig.CODEC));
    public static final Feature<DiskFeatureConfig> DISK = register("disk", new DiskFeature(DiskFeatureConfig.CODEC));
    public static final Feature<GlowLichenFeatureConfig> MULTIFACE_GROWTH = register(
            "multiface_growth", new MultifaceGrowthFeature(GlowLichenFeatureConfig.CODEC)
    );

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, final F feature) {
        return Registry.register(Registry.FEATURE, new Identifier(WildMod.MOD_ID, name), feature);
    }
}
