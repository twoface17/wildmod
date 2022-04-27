package net.frozenblock.wildmod.registry;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.Map;

public class RegistryKey<T> {
    private static final Map<String, RegistryKey<?>> INSTANCES = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private final Identifier registry;
    private final Identifier value;

    public static <T> Codec<RegistryKey<T>> createCodec(RegistryKey<? extends Registry<T>> registry) {
        return Identifier.CODEC.xmap(id -> of(registry, id), RegistryKey::getValue);
    }

    public static <T> RegistryKey<T> of(RegistryKey<? extends Registry<T>> registry, Identifier value) {
        return of(registry.value, value);
    }

    public static <T> RegistryKey<Registry<T>> ofRegistry(Identifier registry) {
        return of(Registry.ROOT_KEY, registry);
    }

    private static <T> RegistryKey<T> of(Identifier registry, Identifier value) {
        String string = (registry + ":" + value).intern();
        return (RegistryKey<T>) INSTANCES.computeIfAbsent(string, id -> new RegistryKey(registry, value));
    }

    public RegistryKey(Identifier registry, Identifier value) {
        //super(registry, value);
        this.registry = registry;
        this.value = value;
    }

    public String toString() {
        return "ResourceKey[" + this.registry + " / " + this.value + "]";
    }

    public boolean isOf(RegistryKey<? extends Registry<?>> registry) {
        return this.registry.equals(registry.getValue());
    }

    /*public <E> Optional<RegistryKey<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
        return this.isOf(registryRef) ? Optional.of(this) : Optional.empty();
    }

    */public Identifier getValue() {
        return this.value;
    }

    public Identifier getRegistry() {
        return this.registry;
    }
}