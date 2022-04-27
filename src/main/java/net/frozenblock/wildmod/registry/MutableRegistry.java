package net.frozenblock.wildmod.registry;

import com.mojang.serialization.Lifecycle;

import java.util.OptionalInt;

public abstract class MutableRegistry<T> extends Registry<T> {
    public MutableRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
    }

    public abstract RegistryEntry<T> set(int rawId, RegistryKey<T> key, T value, Lifecycle lifecycle);

    public abstract RegistryEntry<T> add(RegistryKey<T> key, T entry, Lifecycle lifecycle);

    public abstract RegistryEntry<T> replace(OptionalInt rawId, RegistryKey<T> key, T newEntry, Lifecycle lifecycle);

    public abstract boolean isEmpty();
}
