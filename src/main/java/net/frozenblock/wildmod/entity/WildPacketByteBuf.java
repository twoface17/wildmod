package net.frozenblock.wildmod.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class WildPacketByteBuf extends PacketByteBuf {
    public WildPacketByteBuf(ByteBuf parent) {
        super(parent);
    }

    @FunctionalInterface
    public interface class_7461<T> extends Function<WildPacketByteBuf, T> {
        default class_7461<Optional<T>> asOptional() {
            return packetByteBuf -> packetByteBuf.readOptional(this);
        }
    }

    public  <T> Optional<T> readOptional(class_7461<T> arg) {
        return this.readBoolean() ? Optional.of(arg.apply(this)) : Optional.empty();
    }

    @FunctionalInterface
    public interface class_7462<T> extends BiConsumer<WildPacketByteBuf, T> {
        default class_7462<Optional<T>> asOptional() {
            return (packetByteBuf, optional) -> packetByteBuf.writeOptional(optional, this);
        }
    }

    public <T> void writeOptional(Optional<T> value, class_7462<T> arg) {
        if (value.isPresent()) {
            this.writeBoolean(true);
            arg.accept(this, value.get());
        } else {
            this.writeBoolean(false);
        }

    }

    public <T> void writeRegistryValue(IndexedIterable<T> registry, T value) {
        int i = registry.getRawId(value);
        if (i == -1) {
            throw new IllegalArgumentException("Can't find id for '" + value + "' in map " + registry);
        } else {
            this.writeVarInt(i);
        }
    }

    @Nullable
    public <T> T readRegistryValue(IndexedIterable<T> registry) {
        int i = this.readVarInt();
        return (T)registry.get(i);
    }
}
