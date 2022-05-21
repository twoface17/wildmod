package net.frozenblock.wildmod.entity;

import net.minecraft.entity.data.TrackedData;

public interface WildTrackedDataHandler<T> extends net.minecraft.entity.data.TrackedDataHandler<T> {
    void write(WildPacketByteBuf buf, T value);

    T read(WildPacketByteBuf buf);

    default TrackedData<T> create(int id) {
        return new TrackedData<>(id, this);
    }

    T copy(T value);
    public interface ImmutableHandler<T> extends WildTrackedDataHandler<T> {
        @Override
        default T copy(T value) {
            return value;
        }
    }
}
