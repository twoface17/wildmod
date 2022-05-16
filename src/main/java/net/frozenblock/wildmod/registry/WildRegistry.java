package net.frozenblock.wildmod.registry;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.FrogVariant;
import net.frozenblock.wildmod.entity.WildPacketByteBuf;
import net.frozenblock.wildmod.event.PositionSourceType;
import net.frozenblock.wildmod.world.gen.root.RootPlacerType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class WildRegistry<T> extends Registry<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Identifier ROOT_KEY = new Identifier("root");

    protected WildRegistry(RegistryKey key, Lifecycle lifecycle) {
        super(key, lifecycle);
    }

    public static <T> RegistryKey<Registry<T>> createRegistryKey(String registryId) {
        return RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, registryId));
    }

    public static <T> RegistryKey<Registry<T>> createVanillaRegistryKey(String registryId) {
        return RegistryKey.ofRegistry(new Identifier(WildMod.MOD_ID, registryId));
    }

    public static <T> T register(Registry<? super T> registry, String id, T entry) {
        return register(registry, new Identifier(WildMod.MOD_ID, id), entry);
    }

    public static <V, T extends V> T register(Registry<V> registry, Identifier id, T entry) {
        return register(registry, RegistryKey.of(registry.registryKey, id), entry);
    }

    public static <V, T extends V> T register(Registry<V> registry, RegistryKey<V> key, T entry) {
        ((MutableRegistry<V>)registry).add(key, entry, Lifecycle.stable());
        return entry;
    }

    public static <V, T extends V> T register(Registry<V> registry, int rawId, String id, T entry) {
        ((MutableRegistry<V>)registry).set(rawId, RegistryKey.of(registry.registryKey, new Identifier(WildMod.MOD_ID, id)), entry, Lifecycle.stable());
        return entry;
    }

    public Codec<T> getCodec() {
        Codec<T> codec = Identifier.CODEC
                .flatXmap(
                        id -> Optional.ofNullable(this.get(id))
                                .map(DataResult::success)
                                .orElseGet(() -> DataResult.error("Unknown registry key in " + this.registryKey + ": " + id)),
                        value -> this.getKey((T)value)
                                .map(RegistryKey::getValue)
                                .map(DataResult::success)
                                .orElseGet(() -> DataResult.error("Unknown registry element in " + this.registryKey + ":" + value))
                );
        Codec<T> codec2 = Codecs.rawIdChecked(value -> this.getKey((T)value).isPresent() ? this.getRawId((T)value) : -1, this::get, -1);
        return Codecs.withLifecycle(Codecs.orCompressed(codec, codec2), this::getEntryLifecycle, value -> this.lifecycle);
    }

    @Nullable
    public abstract Identifier getId(T value);

    public abstract Optional<RegistryKey<T>> getKey(T entry);

    public abstract int getRawId(@Nullable T value);

    @Nullable
    public T get(int index) {
        return null;
    }

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

    public abstract WildRegistry<T> freeze();

    public abstract RegistryEntry<T> getOrCreateEntry(RegistryKey key);

    public abstract RegistryEntry.Reference<T> createEntry(T value);

    public abstract Optional<RegistryEntry<T>> getEntry(int rawId);

    public abstract Optional<RegistryEntry<T>> getEntry(RegistryKey key);

    public abstract Stream<RegistryEntry.Reference<T>> streamEntries();

    public abstract Optional<RegistryEntryList.Named<T>> getEntryList(TagKey tag);

    public abstract RegistryEntryList.Named<T> getOrCreateEntryList(TagKey tag);

    public abstract Stream<Pair<TagKey<T>, RegistryEntryList.Named<T>>> streamTagsAndEntries();

    public abstract Stream<TagKey<T>> streamTags();

    public abstract boolean containsTag(TagKey tag);

    public abstract void clearTags();

    public abstract void populateTags(Map tagEntries);

    @NotNull
    public abstract Iterator<T> iterator();

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
