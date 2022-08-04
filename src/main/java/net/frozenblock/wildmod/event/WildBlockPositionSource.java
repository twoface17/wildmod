package net.frozenblock.wildmod.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.WildMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class WildBlockPositionSource implements WildPositionSource {
    public static final Codec<WildBlockPositionSource> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockPos.CODEC.fieldOf("pos").forGetter((blockPositionSource) -> {
            return blockPositionSource.pos;
        })).apply(instance, WildBlockPositionSource::new);
    });
    final BlockPos pos;

    public WildBlockPositionSource(BlockPos pos) {
        this.pos = pos;
    }

    public Optional<BlockPos> getPos(World world) {
        return Optional.ofNullable(this.pos);
    }

    public WildPositionSourceType<?> getType() {
        return WildMod.BLOCK;
    }

    public static class Type implements WildPositionSourceType<WildBlockPositionSource> {
        public Type() {
        }

        public WildBlockPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
            return new WildBlockPositionSource(packetByteBuf.readBlockPos());
        }

        public void writeToBuf(PacketByteBuf packetByteBuf, WildBlockPositionSource blockPositionSource) {
            packetByteBuf.writeBlockPos(blockPositionSource.pos);
        }

        public Codec<WildBlockPositionSource> getCodec() {
            return WildBlockPositionSource.CODEC;
        }
    }
}
