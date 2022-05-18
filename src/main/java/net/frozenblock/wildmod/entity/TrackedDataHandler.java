package net.frozenblock.wildmod.entity;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.Int2ObjectBiMap;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface TrackedDataHandler<T> extends net.minecraft.entity.data.TrackedDataHandler<T> {
    void write(WildPacketByteBuf buf, T value);

    T read(WildPacketByteBuf buf);

    default TrackedData<T> create(int id) {
        return new TrackedData<>(id, this);
    }

    T copy(T value);
    public interface ImmutableHandler<T> extends TrackedDataHandler<T> {
        @Override
        default T copy(T value) {
            return value;
        }
    }
}
