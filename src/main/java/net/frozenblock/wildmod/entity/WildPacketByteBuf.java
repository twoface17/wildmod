package net.frozenblock.wildmod.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class WildPacketByteBuf extends PacketByteBuf {
    public WildPacketByteBuf(ByteBuf parent) {
        super(parent);
    }

    @FunctionalInterface
    public interface PacketReader<T> extends Function<WildPacketByteBuf, T> {
        default PacketReader<Optional<T>> asOptional() {
            return packetByteBuf -> packetByteBuf.readOptional(this);
        }
    }

    public <T> Optional<T> readOptional(PacketReader<T> arg) {
        return this.readBoolean() ? Optional.of(arg.apply(this)) : Optional.empty();
    }

    @FunctionalInterface
    public interface PacketWriter<T> extends BiConsumer<WildPacketByteBuf, T> {
        default PacketWriter<Optional<T>> asOptional() {
            return (packetByteBuf, optional) -> packetByteBuf.writeOptional(optional, this);
        }
    }

    public <T> void writeOptional(Optional<T> value, PacketWriter<T> arg) {
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

    public <T> RegistryKey<T> readRegistryKey(RegistryKey<? extends Registry<T>> registryRef) {
        Identifier identifier = this.readIdentifier();
        return RegistryKey.of(registryRef, identifier);
    }

    public void writeRegistryKey(RegistryKey<?> key) {
        this.writeIdentifier(key.getValue());
    }

    public GlobalPos readGlobalPos() {
        RegistryKey<World> registryKey = this.readRegistryKey(Registry.WORLD_KEY);
        BlockPos blockPos = this.readBlockPos();
        return GlobalPos.create(registryKey, blockPos);
    }

    /**
     * Writes a global position to this buf. A global position is represented by
     * {@linkplain #writeRegistryKey the registry key} of the dimension followed by
     * {@linkplain #writeBlockPos the block position}.
     *
     * @see #readGlobalPos()
     */
    public void writeGlobalPos(GlobalPos pos) {
        this.writeRegistryKey(pos.getDimension());
        this.writeBlockPos(pos.getPos());
    }

    @Nullable
    public <T> T readRegistryValue(IndexedIterable<T> registry) {
        int i = this.readVarInt();
        return registry.get(i);
    }
}
