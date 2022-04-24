/*package frozenblock.wild.mod.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class BlockPositionSource  implements PositionSource {
    public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockPos.CODEC.fieldOf("pos").forGetter((blockPositionSource) -> {
            return blockPositionSource.pos;
        })).apply(instance, BlockPositionSource::new);
    });
    final BlockPos pos;

    public BlockPositionSource(BlockPos pos) {
        this.pos = pos;
    }

    public Optional<Vec3d> getPos(World world) {
        return Optional.of((this.pos));
    }

    public PositionSourceType<?> getType() {
        return (PositionSourceType<?>) PositionSourceType.BLOCK;
    }

    public static class Type implements net.minecraft.world.event.PositionSourceType<BlockPositionSource> {
        public Type() {
        }

        public BlockPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
            return new BlockPositionSource(packetByteBuf.readBlockPos());
        }

        public void writeToBuf(PacketByteBuf packetByteBuf, BlockPositionSource blockPositionSource) {
            packetByteBuf.writeBlockPos(blockPositionSource.pos);
        }

        public Codec<BlockPositionSource> getCodec() {
            return BlockPositionSource.CODEC;
        }
    }
}
*/