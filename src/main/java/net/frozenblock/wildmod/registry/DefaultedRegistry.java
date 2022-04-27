package net.frozenblock.wildmod.registry;

import com.mojang.serialization.Lifecycle;
import net.frozenblock.wildmod.world.gen.random.WildAbstractRandom;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class DefaultedRegistry<T> extends SimpleRegistry<T> {
    private final Identifier defaultId;
    private RegistryEntry<T> defaultEntry;

    public DefaultedRegistry(
            String defaultId, RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, @Nullable Function<T, RegistryEntry.Reference<T>> valueToEntryFunction
    ) {
        super(key, lifecycle, valueToEntryFunction);
        this.defaultId = new Identifier(defaultId);
    }

    public RegistryEntry<T> set(int rawId, RegistryKey<T> key, T value, Lifecycle lifecycle) {
        RegistryEntry<T> registryEntry = super.set(rawId, key, value, lifecycle);
        if (this.defaultId.equals(key.getValue())) {
            this.defaultEntry = registryEntry;
        }

        return registryEntry;
    }

    public int getRawId(@Nullable T value) {
        int i = super.getRawId(value);
        return i == -1 ? super.getRawId(this.defaultEntry.value()) : i;
    }

    @NotNull
    public Identifier getId(T value) {
        Identifier identifier = super.getId(value);
        return identifier == null ? this.defaultId : identifier;
    }

    @NotNull
    public T get(@Nullable Identifier id) {
        T object = (T)super.get(id);
        return (T)(object == null ? this.defaultEntry.value() : object);
    }

    public Optional<T> getOrEmpty(@Nullable Identifier id) {
        return Optional.ofNullable(super.get(id));
    }

    @NotNull
    public T get(int index) {
        T object = (T)super.get(index);
        return (T)(object == null ? this.defaultEntry.value() : object);
    }

    public Optional<RegistryEntry<T>> getRandom(WildAbstractRandom random) {
        return super.getRandom(random).or(() -> Optional.of(this.defaultEntry));
    }

    public Identifier getDefaultId() {
        return this.defaultId;
    }
}
