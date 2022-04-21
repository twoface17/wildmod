package frozenblock.wild.mod.event;

import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface PositionSourceType<T extends PositionSource> extends net.minecraft.world.event.PositionSourceType {
   // PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
    PositionSourceType<EntityPositionSource> ENTITY = net.minecraft.world.event.PositionSourceType.register("entity", new EntityPositionSource.Type());

    net.minecraft.world.event.PositionSource readFromBuf(PacketByteBuf buf);

    void writeToBuf(PacketByteBuf buf, T positionSource);

    Codec<T> getCodec();


    static PositionSource read(PacketByteBuf buf) {
        Identifier identifier = buf.readIdentifier();
        return (PositionSource) ((PositionSourceType<?>)Registry.POSITION_SOURCE_TYPE.getOrEmpty(identifier).orElseThrow(() -> {
            return new IllegalArgumentException("Unknown position source type " + identifier);
        })).readFromBuf(buf);
    }

    static <T extends PositionSource> void write(PositionSource positionSource, PacketByteBuf buf) {
        buf.writeIdentifier(Registry.POSITION_SOURCE_TYPE.getId((net.minecraft.world.event.PositionSourceType<?>) positionSource.getType()));
        positionSource.getType().writeToBuf(buf, (net.minecraft.world.event.PositionSource) positionSource);
    }
}
