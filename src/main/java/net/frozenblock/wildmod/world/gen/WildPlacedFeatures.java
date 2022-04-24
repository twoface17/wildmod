package net.frozenblock.wildmod.world.gen;

import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public class WildPlacedFeatures {
    public static final RegistryEntry<PlacedFeature> SCULK_PATCH_DEEP_DARK;
    public static final RegistryEntry<PlacedFeature> SCULK_PATCH_ANCIENT_CITY;
    //public static final RegistryEntry<PlacedFeature> SCULK_VEIN;

    static {


        SCULK_PATCH_DEEP_DARK = PlacedFeatures.register("sculk_patch_deep_dark", WildConfiguredFeatures.SCULK_PATCH_DEEP_DARK, CountPlacementModifier.of(ConstantIntProvider.create(256)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, BiomePlacementModifier.of());
        SCULK_PATCH_ANCIENT_CITY = PlacedFeatures.register("sculk_patch_ancient_city", WildConfiguredFeatures.SCULK_PATCH_ANCIENT_CITY);
        //SCULK_VEIN = PlacedFeatures.register("sculk_vein", WildConfiguredFeatures.SCULK_VEIN, CountPlacementModifier.of(UniformIntProvider.create(204, 250)), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, BiomePlacementModifier.of());


    }
}
