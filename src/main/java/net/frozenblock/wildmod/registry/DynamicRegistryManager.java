/*package net.frozenblock.wildmod.registry;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.frozenblock.wildmod.world.gen.StructureType;
import net.minecraft.network.MessageType;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.EntryLoader;
import net.minecraft.util.dynamic.RegistryLoader;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DynamicRegistryManager {
    Logger LOGGER = LogUtils.getLogger();
    public static final Codec<Biome> NETWORK_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Biome.Weather.CODEC.forGetter(biome -> biome.weather), BiomeEffects.CODEC.fieldOf("effects").forGetter(biome -> biome.effects)
                    )
                    .apply(instance, (weather, effects) -> new Biome(weather, effects, GenerationSettings.INSTANCE, SpawnSettings.INSTANCE))
    );
    Map<net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, net.minecraft.util.registry.DynamicRegistryManager.Info<?>> INFOS = (Map<net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, net.minecraft.util.registry.DynamicRegistryManager.Info<?>>) net.minecraft.util.Util.make(
            () -> {
                ImmutableMap.Builder<net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, net.minecraft.util.registry.DynamicRegistryManager.Info<?>> builder = ImmutableMap.builder();
                register(builder, net.minecraft.util.registry.Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, DimensionType.CODEC);
                register(builder, net.minecraft.util.registry.Registry.BIOME_KEY, Biome.CODEC, NETWORK_CODEC);
                register(builder, net.minecraft.util.registry.Registry.CONFIGURED_CARVER_KEY, ConfiguredCarver.CODEC);
                register(builder, net.minecraft.util.registry.Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeature.CODEC);
                register(builder, net.minecraft.util.registry.Registry.PLACED_FEATURE_KEY, PlacedFeature.CODEC);
                register(builder, Registry.STRUCTURE_KEY, StructureType.STRUCTURE_TYPE_CODEC);
                register(builder, net.minecraft.util.registry.Registry.STRUCTURE_SET_KEY, StructureSet.CODEC);
                register(builder, net.minecraft.util.registry.Registry.STRUCTURE_PROCESSOR_LIST_KEY, StructureProcessorType.PROCESSORS_CODEC);
                register(builder, net.minecraft.util.registry.Registry.STRUCTURE_POOL_KEY, StructurePool.CODEC);
                register(builder, net.minecraft.util.registry.Registry.CHUNK_GENERATOR_SETTINGS_KEY, ChunkGeneratorSettings.CODEC);
                register(builder, net.minecraft.util.registry.Registry.NOISE_KEY, DoublePerlinNoiseSampler.NoiseParameters.CODEC);
                register(builder, net.minecraft.util.registry.Registry.DENSITY_FUNCTION_KEY, DensityFunction.CODEC);
                register(builder, net.minecraft.util.registry.Registry.CHAT_TYPE, MessageType.field_39227, MessageType.field_39227);
                register(builder, net.minecraft.util.registry.Registry.WORLD_PRESET_KEY, WorldPreset.CODEC);
                register(builder, net.minecraft.util.registry.Registry.FLAT_LEVEL_GENERATOR_PRESET_KEY, FlatLevelGeneratorPreset.CODEC);
                return builder.build();
            }
    );
    Codec<net.minecraft.util.registry.DynamicRegistryManager> CODEC = createCodec();
    Supplier<net.minecraft.util.registry.DynamicRegistryManager.Immutable> BUILTIN = Suppliers.memoize(() -> createAndLoad().toImmutable());

    <E> Optional<net.minecraft.util.registry.Registry<E>> getOptionalManaged(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<? extends E>> key);

    default <E> net.minecraft.util.registry.Registry<E> getManaged(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<? extends E>> key) {
        return (net.minecraft.util.registry.Registry<E>)this.getOptionalManaged(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    default <E> Optional<? extends net.minecraft.util.registry.Registry<E>> getOptional(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<? extends E>> key) {
        Optional<? extends net.minecraft.util.registry.Registry<E>> optional = this.getOptionalManaged(key);
        return optional.isPresent() ? optional : net.minecraft.util.registry.Registry.REGISTRIES.getOrEmpty(key.getValue());
    }

    default <E> net.minecraft.util.registry.Registry<E> get(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<? extends E>> key) {
        return (net.minecraft.util.registry.Registry<E>)this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    private static <E> void register(
            ImmutableMap.Builder<net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, net.minecraft.util.registry.DynamicRegistryManager.Info<?>> infosBuilder,
            net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<E>> registryRef,
            Codec<E> entryCodec
    ) {
        infosBuilder.put(registryRef, new net.minecraft.util.registry.DynamicRegistryManager.Info(registryRef, entryCodec, null));
    }

    private static <E> void register(
            ImmutableMap.Builder<net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, net.minecraft.util.registry.DynamicRegistryManager.Info<?>> infosBuilder,
            net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<E>> registryRef,
            Codec<E> entryCodec,
            Codec<E> networkEntryCodec
    ) {
        infosBuilder.put(registryRef, new net.minecraft.util.registry.DynamicRegistryManager.Info(registryRef, entryCodec, networkEntryCodec));
    }

    static Iterable<net.minecraft.util.registry.DynamicRegistryManager.Info<?>> getInfos() {
        return INFOS.values();
    }

    Stream<net.minecraft.util.registry.DynamicRegistryManager.Entry<?>> streamManagedRegistries();

    private static Stream<net.minecraft.util.registry.DynamicRegistryManager.Entry<Object>> streamStaticRegistries() {
        return net.minecraft.util.registry.Registry.REGISTRIES.streamEntries().map(net.minecraft.util.registry.DynamicRegistryManager.Entry::of);
    }

    default Stream<net.minecraft.util.registry.DynamicRegistryManager.Entry<?>> streamAllRegistries() {
        return Stream.concat(this.streamManagedRegistries(), streamStaticRegistries());
    }

    default Stream<net.minecraft.util.registry.DynamicRegistryManager.Entry<?>> streamSyncedRegistries() {
        return Stream.concat(this.streamSyncedManagedRegistries(), streamStaticRegistries());
    }

    private static <E> Codec<net.minecraft.util.registry.DynamicRegistryManager> createCodec() {
        Codec<net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<E>>> codec = Identifier.CODEC.xmap(net.minecraft.util.registry.RegistryKey::ofRegistry, net.minecraft.util.registry.RegistryKey::getValue);
        Codec<net.minecraft.util.registry.Registry<E>> codec2 = codec.partialDispatch(
                "type",
                registry -> DataResult.success(registry.getKey()),
                registryRef -> getNetworkEntryCodec(registryRef).map(codecx -> RegistryCodecs.createRegistryCodec(registryRef, Lifecycle.experimental(), codecx))
        );
        UnboundedMapCodec<? extends net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, ? extends net.minecraft.util.registry.Registry<?>> unboundedMapCodec = Codec.unboundedMap(codec, codec2);
        return createCodec(unboundedMapCodec);
    }

    private static <K extends net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, V extends net.minecraft.util.registry.Registry<?>> Codec<net.minecraft.util.registry.DynamicRegistryManager> createCodec(
            UnboundedMapCodec<K, V> originalCodec
    ) {
        return originalCodec.xmap(
                net.minecraft.util.registry.DynamicRegistryManager.ImmutableImpl::new,
                dynamicRegistryManager -> (Map)dynamicRegistryManager.streamSyncedManagedRegistries()
                        .collect(ImmutableMap.toImmutableMap(entry -> entry.key(), entry -> entry.value()))
        );
    }

    private Stream<net.minecraft.util.registry.DynamicRegistryManager.Entry<?>> streamSyncedManagedRegistries() {
        return this.streamManagedRegistries().filter(entry -> ((net.minecraft.util.registry.DynamicRegistryManager.Info)INFOS.get(entry.key)).isSynced());
    }

    private static <E> DataResult<? extends Codec<E>> getNetworkEntryCodec(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<E>> registryKey) {
        return (DataResult<? extends Codec<E>>)Optional.ofNullable((net.minecraft.util.registry.DynamicRegistryManager.Info)INFOS.get(registryKey))
                .map(info -> info.networkEntryCodec())
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error("Unknown or not serializable registry: " + registryKey));
    }

    private static Map<net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, ? extends net.minecraft.util.registry.MutableRegistry<?>> createMutableRegistries() {
        return (Map<net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, ? extends net.minecraft.util.registry.MutableRegistry<?>>)INFOS.keySet()
                .stream()
                .collect(Collectors.toMap(Function.identity(), net.minecraft.util.registry.DynamicRegistryManager::createSimpleRegistry));
    }

    private static net.minecraft.util.registry.DynamicRegistryManager.Mutable createMutableRegistryManager() {
        return new net.minecraft.util.registry.DynamicRegistryManager.MutableImpl(createMutableRegistries());
    }

    static net.minecraft.util.registry.DynamicRegistryManager.Immutable of(net.minecraft.util.registry.Registry<? extends net.minecraft.util.registry.Registry<?>> registries) {
        return new net.minecraft.util.registry.DynamicRegistryManager.Immutable() {
            @Override
            public <T> Optional<net.minecraft.util.registry.Registry<T>> getOptionalManaged(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<? extends T>> key) {
                net.minecraft.util.registry.Registry<net.minecraft.util.registry.Registry<T>> registry = registries;
                return registry.getOrEmpty(key);
            }

            @Override
            public Stream<net.minecraft.util.registry.DynamicRegistryManager.Entry<?>> streamManagedRegistries() {
                return registries.getEntrySet().stream().map(net.minecraft.util.registry.DynamicRegistryManager.Entry::of);
            }
        };
    }

    static net.minecraft.util.registry.DynamicRegistryManager.Mutable createAndLoad() {
        net.minecraft.util.registry.DynamicRegistryManager.Mutable mutable = createMutableRegistryManager();
        EntryLoader.Impl impl = new EntryLoader.Impl();

        for(java.util.Map.Entry<net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, net.minecraft.util.registry.DynamicRegistryManager.Info<?>> entry : INFOS.entrySet()) {
            addEntriesToLoad(impl, (net.minecraft.util.registry.DynamicRegistryManager.Info)entry.getValue());
        }

        RegistryOps.ofLoaded(JsonOps.INSTANCE, mutable, impl);
        return mutable;
    }

    private static <E> void addEntriesToLoad(EntryLoader.Impl entryLoader, net.minecraft.util.registry.DynamicRegistryManager.Info<E> info) {
        net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<E>> registryKey = info.registry();
        net.minecraft.util.registry.Registry<E> registry = BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER.get(registryKey);

        for(java.util.Map.Entry<net.minecraft.util.registry.RegistryKey<E>, E> entry : registry.getEntrySet()) {
            net.minecraft.util.registry.RegistryKey<E> registryKey2 = (net.minecraft.util.registry.RegistryKey)entry.getKey();
            E object = (E)entry.getValue();
            entryLoader.add(
                    BuiltinRegistries.DYNAMIC_REGISTRY_MANAGER,
                    registryKey2,
                    info.entryCodec(),
                    registry.getRawId(object),
                    object,
                    registry.getEntryLifecycle(object)
            );
        }

    }

    static void load(net.minecraft.util.registry.DynamicRegistryManager.Mutable dynamicRegistryManager, DynamicOps<JsonElement> ops, RegistryLoader registryLoader) {
        RegistryLoader.LoaderAccess loaderAccess = registryLoader.createAccess(dynamicRegistryManager);

        for(net.minecraft.util.registry.DynamicRegistryManager.Info<?> info : INFOS.values()) {
            load(ops, loaderAccess, info);
        }

    }

    private static <E> void load(DynamicOps<JsonElement> ops, RegistryLoader.LoaderAccess loaderAccess, net.minecraft.util.registry.DynamicRegistryManager.Info<E> info) {
        DataResult<? extends net.minecraft.util.registry.Registry<E>> dataResult = loaderAccess.load(info.registry(), info.entryCodec(), ops);
        dataResult.error().ifPresent(partialResult -> {
            throw new JsonParseException("Error loading registry data: " + partialResult.message());
        });
    }

    static net.minecraft.util.registry.DynamicRegistryManager createDynamicRegistryManager(Dynamic<?> dynamic) {
        return new net.minecraft.util.registry.DynamicRegistryManager.ImmutableImpl(
                (Map<? extends net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, ? extends net.minecraft.util.registry.Registry<?>>)INFOS.keySet()
                        .stream()
                        .collect(Collectors.toMap(Function.identity(), registryRef -> createRegistry(registryRef, dynamic)))
        );
    }

    static <E> net.minecraft.util.registry.Registry<E> createRegistry(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<? extends E>> registryRef, Dynamic<?> dynamic) {
        return (net.minecraft.util.registry.Registry<E>)RegistryOps.createRegistryCodec(registryRef)
                .codec()
                .parse(dynamic)
                .resultOrPartial(Util.addPrefix(registryRef + " registry: ", LOGGER::error))
                .orElseThrow(() -> new IllegalStateException("Failed to get " + registryRef + " registry"));
    }

    static <E> net.minecraft.util.registry.MutableRegistry<?> createSimpleRegistry(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>> registryRef) {
        return new SimpleRegistry(registryRef, Lifecycle.stable(), null);
    }

    default net.minecraft.util.registry.DynamicRegistryManager.Immutable toImmutable() {
        return new net.minecraft.util.registry.DynamicRegistryManager.ImmutableImpl(this.streamManagedRegistries().map(net.minecraft.util.registry.DynamicRegistryManager.Entry::freeze));
    }

    default Lifecycle getRegistryLifecycle() {
        return (Lifecycle)this.streamManagedRegistries().map(entry -> entry.value.getLifecycle()).reduce(Lifecycle.stable(), Lifecycle::add);
    }

    public static record Entry<T>(
            net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<T>> key, net.minecraft.util.registry.Registry<T> value) {
        final net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<T>> key;
        final net.minecraft.util.registry.Registry<T> value;

        private static <T, R extends net.minecraft.util.registry.Registry<? extends T>> net.minecraft.util.registry.DynamicRegistryManager.Entry<T> of(
                java.util.Map.Entry<? extends net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, R> entry
        ) {
            return of((net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>)entry.getKey(), (net.minecraft.util.registry.Registry<?>)entry.getValue());
        }

        private static <T> net.minecraft.util.registry.DynamicRegistryManager.Entry<T> of(RegistryEntry.Reference<? extends net.minecraft.util.registry.Registry<? extends T>> entry) {
            return of(entry.registryKey(), (net.minecraft.util.registry.Registry<?>)entry.value());
        }

        private static <T> net.minecraft.util.registry.DynamicRegistryManager.Entry<T> of(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>> key, net.minecraft.util.registry.Registry<?> value) {
            return new net.minecraft.util.registry.DynamicRegistryManager.Entry<>(key, value);
        }

        private net.minecraft.util.registry.DynamicRegistryManager.Entry<T> freeze() {
            return new net.minecraft.util.registry.DynamicRegistryManager.Entry<>(this.key, this.value.freeze());
        }
    }

    public interface Immutable extends net.minecraft.util.registry.DynamicRegistryManager {
        @Override
        default net.minecraft.util.registry.DynamicRegistryManager.Immutable toImmutable() {
            return this;
        }
    }

    public static final class ImmutableImpl implements net.minecraft.util.registry.DynamicRegistryManager.Immutable {
        private final Map<? extends net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, ? extends net.minecraft.util.registry.Registry<?>> registries;

        public ImmutableImpl(Map<? extends net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, ? extends net.minecraft.util.registry.Registry<?>> registries) {
            this.registries = Map.copyOf(registries);
        }

        ImmutableImpl(Stream<net.minecraft.util.registry.DynamicRegistryManager.Entry<?>> stream) {
            this.registries = (Map)stream.collect(ImmutableMap.toImmutableMap(net.minecraft.util.registry.DynamicRegistryManager.Entry::key, net.minecraft.util.registry.DynamicRegistryManager.Entry::value));
        }

        @Override
        public <E> Optional<net.minecraft.util.registry.Registry<E>> getOptionalManaged(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<? extends E>> key) {
            return Optional.ofNullable((net.minecraft.util.registry.Registry)this.registries.get(key)).map(registry -> registry);
        }

        @Override
        public Stream<net.minecraft.util.registry.DynamicRegistryManager.Entry<?>> streamManagedRegistries() {
            return this.registries.entrySet().stream().map(net.minecraft.util.registry.DynamicRegistryManager.Entry::of);
        }
    }

    public static record Info<E>(
            net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<E>> registry, Codec<E> entryCodec, @Nullable Codec<E> networkEntryCodec) {
        public boolean isSynced() {
            return this.networkEntryCodec != null;
        }
    }

    public interface Mutable extends net.minecraft.util.registry.DynamicRegistryManager {
        <E> Optional<net.minecraft.util.registry.MutableRegistry<E>> getOptionalMutable(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<? extends E>> key);

        default <E> net.minecraft.util.registry.MutableRegistry<E> getMutable(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<? extends E>> key) {
            return (net.minecraft.util.registry.MutableRegistry<E>)this.getOptionalMutable(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
        }
    }

    public static final class MutableImpl implements net.minecraft.util.registry.DynamicRegistryManager.Mutable {
        private final Map<? extends net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, ? extends net.minecraft.util.registry.MutableRegistry<?>> mutableRegistries;

        MutableImpl(Map<? extends net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<?>>, ? extends net.minecraft.util.registry.MutableRegistry<?>> mutableRegistries) {
            this.mutableRegistries = mutableRegistries;
        }

        @Override
        public <E> Optional<net.minecraft.util.registry.Registry<E>> getOptionalManaged(net.minecraft.util.registry.RegistryKey<? extends net.minecraft.util.registry.Registry<? extends E>> key) {
            return Optional.ofNullable((net.minecraft.util.registry.MutableRegistry)this.mutableRegistries.get(key)).map(registry -> registry);
        }

        @Override
        public <E> Optional<net.minecraft.util.registry.MutableRegistry<E>> getOptionalMutable(RegistryKey<? extends Registry<? extends E>> key) {
            return Optional.ofNullable((MutableRegistry)this.mutableRegistries.get(key)).map(registry -> registry);
        }

        @Override
        public Stream<net.minecraft.util.registry.DynamicRegistryManager.Entry<?>> streamManagedRegistries() {
            return this.mutableRegistries.entrySet().stream().map(net.minecraft.util.registry.DynamicRegistryManager.Entry::of);
        }
    }
}
*/