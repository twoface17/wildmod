package net.frozenblock.wildmod.event;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public interface PositionSource extends net.minecraft.world.event.PositionSource {
    Codec<PositionSource> CODEC = WildRegistry.WILD_POSITION_SOURCE_TYPE.getCodec().dispatch(PositionSource::getType, WildPositionSourceType::getCodec);

    Optional<BlockPos> getPos(World world);

    WildPositionSourceType<?> getType();
}
