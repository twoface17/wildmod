package net.frozenblock.wildmod.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.FrogVariant;
import net.frozenblock.wildmod.entity.WildPacketByteBuf;
import net.frozenblock.wildmod.entity.WildTrackedDataHandler;
import net.frozenblock.wildmod.event.PositionSourceType;
import net.frozenblock.wildmod.items.Instrument;
import net.frozenblock.wildmod.items.Instruments;
import net.frozenblock.wildmod.world.gen.root.RootPlacerType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public abstract class WildRegistry<T> extends Registry<T> {
    public static final RegistryKey<Registry<FrogVariant>> FROG_VARIANT_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "frog_variant"));
    public static final Registry<FrogVariant> FROG_VARIANT = Registry.create(FROG_VARIANT_KEY, registry -> FrogVariant.TEMPERATE);
    public static final TrackedDataHandler<FrogVariant> FROG_VARIANT_DATA = of(FROG_VARIANT);

    public static final RegistryKey<Registry<RootPlacerType<?>>> ROOT_PLACER_TYPE_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "worldgen/root_placer_type"));
    public static final Registry<RootPlacerType<?>> ROOT_PLACER_TYPE = Registry.create(ROOT_PLACER_TYPE_KEY, registry -> RootPlacerType.MANGROVE_ROOT_PLACER);
    //public static final RegistryKey<Registry<net.frozenblock.wildmod.world.gen.structure.StructureType<?>>> STRUCTURE_TYPE_KEY = WildRegistry.createRegistryEntry("worldgen/structure_type");
    //public static final Registry<StructureType<?>> STRUCTURE_TYPE = WildRegistry.create(STRUCTURE_TYPE_KEY, registry -> net.frozenblock.wildmod.world.gen.structure.StructureType.JIGSAW);
    //public static final RegistryKey<Registry<StructureType<?>>> STRUCTURE_KEY = WildRegistry.createRegistryKey("worldgen/structure");
    public static final RegistryKey<Registry<DoublePerlinNoiseSampler.NoiseParameters>> NOISE_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "worldgen/noise"));
    public static final RegistryKey<Registry<PositionSourceType<?>>> WILD_POSITION_SOURCE_TYPE_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "wild_position_source_type"));
    public static final Registry<PositionSourceType<?>> WILD_POSITION_SOURCE_TYPE = Registry.create(WILD_POSITION_SOURCE_TYPE_KEY, registry -> WildMod.BLOCK);
    public static final RegistryKey<Registry<Instrument>> INSTRUMENT_KEY = RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, "instrument"));
    public static final Registry<Instrument> INSTRUMENT = Registry.create(INSTRUMENT_KEY, Instruments::registerAndGetDefault);

    protected WildRegistry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
        super(key, lifecycle);
    }


    public static void init() {
    }

    public static <T> TrackedDataHandler<T> of(WildPacketByteBuf.PacketWriter<T> arg, WildPacketByteBuf.PacketReader<T> arg2) {
        return new WildTrackedDataHandler.ImmutableHandler<T>() {
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

    @Nullable
    public abstract Identifier getId(T value);

    public abstract Optional<RegistryKey<T>> getKey(T entry);;

    public abstract int getRawId(@Nullable T value);

    @Nullable
    public abstract T get(int index);

    public abstract int size();

    @Nullable
    public abstract T get(@Nullable RegistryKey key);

    @Nullable
    public abstract T get(@Nullable Identifier id);

    public abstract Lifecycle getEntryLifecycle(T entry);

    public abstract Lifecycle getLifecycle();

    public abstract Set<Identifier> getIds();

    public abstract Set<Map.Entry<RegistryKey<T>, T>> getEntrySet();

    public abstract Optional<RegistryEntry<T>> getRandom(Random random);

    public abstract boolean containsId(Identifier id);

    public abstract boolean contains(RegistryKey<T> key);

    public abstract Registry<T> freeze();

    public abstract RegistryEntry<T> getOrCreateEntry(RegistryKey key);

    public abstract RegistryEntry.Reference<T> createEntry(T value);

    public abstract Optional<RegistryEntry<T>> getEntry(int rawId);

    public abstract Optional<RegistryEntry<T>> getEntry(RegistryKey<T> key);

    public abstract Stream<RegistryEntry.Reference<T>> streamEntries();

    public abstract Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag);

    public abstract RegistryEntryList.Named<T> getOrCreateEntryList(TagKey<T> tag);

    public abstract Stream<Pair<TagKey<T>, RegistryEntryList.Named<T>>> streamTagsAndEntries();

    public abstract Stream<TagKey<T>> streamTags();

    public abstract boolean containsTag(TagKey<T> tag);

    public abstract void clearTags();

    public abstract void populateTags(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries);
}
