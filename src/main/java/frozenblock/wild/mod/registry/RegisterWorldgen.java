package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.mixins.TreeDecoratorTypeInvoker;
import frozenblock.wild.mod.worldgen.MangroveSwamps;
import frozenblock.wild.mod.worldgen.MangroveTree;
import frozenblock.wild.mod.worldgen.MangroveTreePropagule;
import frozenblock.wild.mod.worldgen.MangroveTreeRoots;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public abstract class RegisterWorldgen {

    public static final RegistryKey<Biome> MANGROVE_SWAMPS_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(WildMod.MOD_ID, "mangrove_swamps"));
    public static final TreeDecoratorType<MangroveTreeRoots> MANGROVE_TREE_ROOTS = TreeDecoratorTypeInvoker.callRegister("mangrove_tree_roots", MangroveTreeRoots.CODEC);
    public static final TreeDecoratorType<MangroveTreePropagule> MANGROVE_TREE_PROPAGULE = TreeDecoratorTypeInvoker.callRegister("mangrove_tree_propagule", MangroveTreePropagule.CODEC);
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
