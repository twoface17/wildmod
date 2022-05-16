package net.frozenblock.wildmod.registry;

import net.frozenblock.wildmod.entity.FrogVariant;
import net.frozenblock.wildmod.event.PositionSourceType;
import net.frozenblock.wildmod.world.gen.root.RootPlacerType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class RegisterRegistries {
    public static RegistryKey<Registry<FrogVariant>> FROG_VARIANT_KEY;
    public static Registry<FrogVariant> FROG_VARIANT;
    public static TrackedDataHandler<FrogVariant> FROG_VARIANT_DATA;

    public static RegistryKey<Registry<RootPlacerType<?>>> ROOT_PLACER_TYPE_KEY;
    public static Registry<RootPlacerType<?>> ROOT_PLACER_TYPE;
    //public static RegistryKey<Registry<net.frozenblock.wildmod.world.gen.structure.StructureType<?>>> STRUCTURE_TYPE_KEY;
    //public static Registry<StructureType<?>> STRUCTURE_TYPE;
    //public static RegistryKey<Registry<StructureType<?>>> STRUCTURE_KEY;
    public static RegistryKey<Registry<DoublePerlinNoiseSampler.NoiseParameters>> NOISE_KEY;
    public static RegistryKey<Registry<PositionSourceType<?>>> WILD_POSITION_SOURCE_TYPE_KEY;
    public static Registry<PositionSourceType<?>> WILD_POSITION_SOURCE_TYPE;

    public static void register() {
        FROG_VARIANT_KEY = WildRegistry.createRegistryKey("frog_variant");
        FROG_VARIANT = WildRegistry.create(FROG_VARIANT_KEY, registry -> FrogVariant.TEMPERATE);
        FROG_VARIANT_DATA = WildRegistry.of(FROG_VARIANT);
        //STRUCTURE_TYPE_KEY = WildRegistry.createRegistryEntry("worldgen/structure_type");
        //STRUCTURE_TYPE = WildRegistry.create(STRUCTURE_TYPE_KEY, registry -> net.frozenblock.wildmod.world.gen.structure.StructureType.JIGSAW);
        //STRUCTURE_KEY = WildRegistry.createRegistryKey("worldgen/structure");
        ROOT_PLACER_TYPE_KEY = WildRegistry.createRegistryKey("worldgen/root_placer_type");
        ROOT_PLACER_TYPE = WildRegistry.create(ROOT_PLACER_TYPE_KEY, registry -> RootPlacerType.MANGROVE_ROOT_PLACER);
        NOISE_KEY = WildRegistry.createRegistryKey("worldgen/noise");
        WILD_POSITION_SOURCE_TYPE_KEY = WildRegistry.createRegistryKey("wild_position_source_type");
        WILD_POSITION_SOURCE_TYPE = WildRegistry.create(WILD_POSITION_SOURCE_TYPE_KEY, registry -> PositionSourceType.BLOCK);
    }
}
