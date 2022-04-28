package net.frozenblock.wildmod.event;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.registry.Registry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public interface PositionSource {
    Codec<PositionSource> CODEC = Registry.WILD_POSITION_SOURCE_TYPE.getCodec().dispatch(PositionSource::getType, PositionSourceType::getCodec);

    Optional<Vec3d> getPos(World world);

    PositionSourceType<?> getType();
}
