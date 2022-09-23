package net.frozenblock.wildmod.registry;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.FrogVariant;
import net.frozenblock.wildmod.entity.WildTrackedDataHandler;
import net.frozenblock.wildmod.items.Instrument;
import net.frozenblock.wildmod.items.Instruments;
import net.frozenblock.wildmod.world.gen.root.RootPlacerType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public abstract class WildRegistry<T> {
    public static final RegistryKey<Registry<FrogVariant>> FROG_VARIANT_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "frog_variant"));
    public static final Registry<FrogVariant> FROG_VARIANT = Registry.create(FROG_VARIANT_KEY, registry -> FrogVariant.TEMPERATE);
    public static final TrackedDataHandler<FrogVariant> FROG_VARIANT_DATA = WildTrackedDataHandler.of(FROG_VARIANT);

    public static final RegistryKey<Registry<RootPlacerType<?>>> ROOT_PLACER_TYPE_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "worldgen/root_placer_type"));
    public static final Registry<RootPlacerType<?>> ROOT_PLACER_TYPE = Registry.create(ROOT_PLACER_TYPE_KEY, registry -> RootPlacerType.MANGROVE_ROOT_PLACER);
    //public static final RegistryKey<Registry<net.frozenblock.wildmod.world.gen.structure.StructureType<?>>> STRUCTURE_TYPE_KEY = WildRegistry.createRegistryEntry("worldgen/structure_type");
    //public static final Registry<StructureType<?>> STRUCTURE_TYPE = WildRegistry.create(STRUCTURE_TYPE_KEY, registry -> net.frozenblock.wildmod.world.gen.structure.StructureType.JIGSAW);
    //public static final RegistryKey<Registry<StructureType<?>>> STRUCTURE_KEY = WildRegistry.createRegistryKey("worldgen/structure");
    public static final RegistryKey<Registry<DoublePerlinNoiseSampler.NoiseParameters>> NOISE_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "worldgen/noise"));
    public static final RegistryKey<Registry<Instrument>> INSTRUMENT_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "instrument"));
    public static final Registry<Instrument> INSTRUMENT = Registry.create(INSTRUMENT_KEY, Instruments::registerAndGetDefault);

    public static void init() {
    }
}
