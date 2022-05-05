package net.frozenblock.wildmod.event;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.registry.Registry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface PositionSourceType<T extends PositionSource> {

    PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
    PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.Type());

    T readFromBuf(PacketByteBuf buf);

    void writeToBuf(PacketByteBuf buf, T positionSource);

    Codec<T> getCodec();

    static <S extends PositionSourceType<T>, T extends PositionSource> S register(String id, S positionSourceType) {
        return (S)Registry.register(Registry.WILD_POSITION_SOURCE_TYPE, id, positionSourceType);
    }


    static PositionSource read(PacketByteBuf buf) {
        Identifier identifier = buf.readIdentifier();
        return Registry.WILD_POSITION_SOURCE_TYPE
                .getOrEmpty(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Unknown position source type " + identifier))
                .readFromBuf(buf);
    }

    /*static <T extends PositionSource> void write(T positionSource, PacketByteBuf buf) {
        buf.writeIdentifier(Registry.WILD_POSITION_SOURCE_TYPE.getId(positionSource.getType()));
        positionSource.getType().writeToBuf(buf, positionSource);
    }
*/}
