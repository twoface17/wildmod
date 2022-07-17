package net.frozenblock.wildmod.entity;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;

public interface WildTrackedDataHandler<T> extends TrackedDataHandler<T> {
    void write(PacketByteBuf buf, T value);

    T read(PacketByteBuf buf);

    default TrackedData<T> create(int id) {
        return new TrackedData<>(id, this);
    }

    T copy(T value);

    static <T> TrackedDataHandler<T> of(WildPacketByteBuf.PacketWriter<T> packetWriter, WildPacketByteBuf.PacketReader<T> packetReader) {
        return new ImmutableHandler<T>() {
            public void write(PacketByteBuf buf, T value) {
                packetWriter.accept((WildPacketByteBuf) buf, value);
            }

            public T read(PacketByteBuf buf) {
                return packetReader.apply((WildPacketByteBuf) buf);
            }
        };
    }

    static <T> TrackedDataHandler<T> of(IndexedIterable<T> registry) {
        return of((buf, value) -> {
            buf.writeRegistryValue(registry, value);
        }, (buf) -> {
            return buf.readRegistryValue(registry);
        });
    }

    interface ImmutableHandler<T> extends WildTrackedDataHandler<T> {
        @Override
        default T copy(T value) {
            return value;
        }
    }
}
