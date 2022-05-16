package net.frozenblock.wildmod.event;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.RegisterRegistries;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public interface PositionSource extends net.minecraft.world.event.PositionSource {
    Codec<PositionSource> CODEC = RegisterRegistries.WILD_POSITION_SOURCE_TYPE.getCodec().dispatch(PositionSource::getType, PositionSourceType::getCodec);

    Optional<BlockPos> getPos(World world);

    PositionSourceType<?> getType();
}
