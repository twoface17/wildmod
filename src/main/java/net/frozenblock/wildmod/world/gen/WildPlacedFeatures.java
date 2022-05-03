package net.frozenblock.wildmod.world.gen;

import com.mojang.serialization.Lifecycle;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.*;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class WildPlacedFeatures {
    public static final RegistryEntry<PlacedFeature> SCULK_PATCH_DEEP_DARK;
    public static final RegistryEntry<PlacedFeature> SCULK_PATCH_ANCIENT_CITY;
    public static final RegistryEntry<PlacedFeature> SCULK_VEIN;
    public static final RegistryEntry<PlacedFeature> DISK_GRASS;
    //public static final RegistryEntry<PlacedFeature> TALL_MANGROVE_CHECKED;

    static {


        SCULK_PATCH_DEEP_DARK = register(
                "sculk_patch_deep_dark",
                WildConfiguredFeatures.SCULK_PATCH_DEEP_DARK,
                CountPlacementModifier.of(ConstantIntProvider.create(256)),
                SquarePlacementModifier.of(),
                PlacedFeatures.BOTTOM_TO_120_RANGE,
                BiomePlacementModifier.of());
        SCULK_PATCH_ANCIENT_CITY = register(
                "sculk_patch_ancient_city", WildConfiguredFeatures.SCULK_PATCH_ANCIENT_CITY, new PlacementModifier[0]
        );
        SCULK_VEIN = register(
                "sculk_vein",
                WildConfiguredFeatures.SCULK_VEIN,
                CountPlacementModifier.of(UniformIntProvider.create(204, 250)),
                SquarePlacementModifier.of(),
                PlacedFeatures.BOTTOM_TO_120_RANGE,
                BiomePlacementModifier.of());
        DISK_GRASS = register(
                "disk_grass",
                WildConfiguredFeatures.DISK_GRASS,
                CountPlacementModifier.of(1),
                SquarePlacementModifier.of(),
                PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP,
                RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1)),
                BlockFilterPlacementModifier.of(BlockPredicate.matchingBlocks(List.of(new Block[]{RegisterBlocks.MUD}))),
                BiomePlacementModifier.of());

        /*TALL_MANGROVE_CHECKED = register(
                "tall_mangrove_checked", WildConfiguredFeatures.TALL_MANGROVE, new PlacementModifier[]{PlacedFeatures.wouldSurvive(MangroveWoods.MANGROVE_PROPAGULE)}
        );
    */}

    public static <T> RegistryEntry<T> add(Registry<T> registry, RegistryKey<T> key, T object) {
        return ((MutableRegistry<T>)registry).add(key, object, Lifecycle.stable());
    }

    public static <T> RegistryEntry<T> add(Registry<T> registry, Identifier id, T object) {
        return add(registry, RegistryKey.of(registry.getKey(), id), object);
    }

    public static <T> RegistryEntry<T> add(Registry<T> registry, String id, T object) {
        return add(registry, new Identifier(WildMod.MOD_ID, id), object);
    }

    public static RegistryEntry<PlacedFeature> register(
            String id, RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, List<PlacementModifier> modifiers
    ) {
        return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, id, new PlacedFeature(RegistryEntry.upcast(registryEntry), List.copyOf(modifiers)));
    }

    public static RegistryEntry<PlacedFeature> register(
            String id, RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, PlacementModifier... modifiers
    ) {
        return register(id, registryEntry, List.of(modifiers));
    }
}
