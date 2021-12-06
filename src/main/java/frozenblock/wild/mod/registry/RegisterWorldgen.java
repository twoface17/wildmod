package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.mixins.TreeDecoratorTypeInvoker;
import frozenblock.wild.mod.worldgen.MangroveSwamps;
import frozenblock.wild.mod.worldgen.MangroveTree;
import frozenblock.wild.mod.worldgen.MangroveTreeDecorator;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public abstract class RegisterWorldgen {

    public static final RegistryKey<Biome> MANGROVE_SWAMPS_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(WildMod.MOD_ID, "mangrove_swamps"));
    public static final TreeDecoratorType<MangroveTreeDecorator> MANGROVE_TREE_DECORATOR = TreeDecoratorTypeInvoker.callRegister("mangrove_tree_roots", MangroveTreeDecorator.CODEC);
    private static final Biome MANGROVE_SWAMPS = MangroveSwamps.createMangroveSwamps();


    public static void RegisterWorldgen() {
        RegistryKey<ConfiguredFeature<?, ?>> mangroveTree = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,
                new Identifier(WildMod.MOD_ID, "mangrove_tree"));
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, mangroveTree.getValue(), MangroveTree.MANGROVE_TREE);
        Registry.register(BuiltinRegistries.BIOME, MANGROVE_SWAMPS_KEY.getValue(), MANGROVE_SWAMPS);
        Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier(WildMod.MOD_ID, "mangrove_swamps_builder"), MangroveSwamps.MANGROVE_SWAMPS_BUILDER);
        OverworldBiomes.addContinentalBiome(MANGROVE_SWAMPS_KEY, OverworldClimate.TEMPERATE, 2D);
    }
}
