package net.frozenblock.wildmod.registry;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.FrogVariant;
import net.frozenblock.wildmod.entity.WildPacketByteBuf;
import net.frozenblock.wildmod.event.PositionSourceType;
import net.frozenblock.wildmod.world.gen.root.RootPlacerType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class WildRegistry {
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
        FROG_VARIANT_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "frog_variant"));
        FROG_VARIANT = Registry.create(FROG_VARIANT_KEY, registry -> FrogVariant.TEMPERATE);
        FROG_VARIANT_DATA = of(FROG_VARIANT);
        //STRUCTURE_TYPE_KEY = WildRegistry.createRegistryEntry("worldgen/structure_type");
        //STRUCTURE_TYPE = WildRegistry.create(STRUCTURE_TYPE_KEY, registry -> net.frozenblock.wildmod.world.gen.structure.StructureType.JIGSAW);
        //STRUCTURE_KEY = WildRegistry.createRegistryKey("worldgen/structure");
        ROOT_PLACER_TYPE_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "worldgen/root_placer_type"));
        ROOT_PLACER_TYPE = Registry.create(ROOT_PLACER_TYPE_KEY, registry -> RootPlacerType.MANGROVE_ROOT_PLACER);
        NOISE_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "worldgen/noise"));
        WILD_POSITION_SOURCE_TYPE_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "wild_position_source_type"));
        WILD_POSITION_SOURCE_TYPE = Registry.create(WILD_POSITION_SOURCE_TYPE_KEY, registry -> PositionSourceType.BLOCK);
    }

    public static <T> TrackedDataHandler<T> of(WildPacketByteBuf.class_7462<T> arg, WildPacketByteBuf.class_7461<T> arg2) {
        return new net.frozenblock.wildmod.entity.TrackedDataHandler.ImmutableHandler<T>() {
            @Override
            public void write(PacketByteBuf buf, T value) {
                arg.accept((WildPacketByteBuf) buf, value);
            }

            @Override
            public T read(PacketByteBuf buf) {
                return arg2.apply((WildPacketByteBuf) buf);
            }

            @Override
            public void write(WildPacketByteBuf buf, T value) {
                arg.accept(buf, value);
            }

            @Override
            public T read(WildPacketByteBuf buf) {
                return (T)arg2.apply(buf);
            }
        };
    }

    public static <T> TrackedDataHandler<T> of(IndexedIterable<T> registry) {
        return of((buf, value) -> buf.writeRegistryValue(registry, value), buf -> buf.readRegistryValue(registry));
    }
}
