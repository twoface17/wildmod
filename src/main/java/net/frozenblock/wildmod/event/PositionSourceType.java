package net.frozenblock.wildmod.event;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface PositionSourceType<T extends PositionSource> extends net.minecraft.world.event.PositionSourceType<T> {

    T readFromBuf(PacketByteBuf buf);

    void writeToBuf(PacketByteBuf buf, T positionSource);

    Codec<T> getCodec();

    static <S extends PositionSourceType<T>, T extends PositionSource> S register(String id, S positionSourceType) {
        return (S) Registry.register(WildRegistry.WILD_POSITION_SOURCE_TYPE, id, positionSourceType);
    }


    static PositionSource read(PacketByteBuf buf) {
        Identifier identifier = buf.readIdentifier();
        return WildRegistry.WILD_POSITION_SOURCE_TYPE
                .getOrEmpty(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Unknown position source type " + identifier))
                .readFromBuf(buf);
    }

    /*static <T extends PositionSource> void write(T positionSource, PacketByteBuf buf) {
        buf.writeIdentifier(Registry.WILD_POSITION_SOURCE_TYPE.getId(positionSource.getType()));
        positionSource.getType().writeToBuf(buf, positionSource);
    }
*/}
