package frozenblock.wild.mod.event;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Optional;

public interface PositionSource {
    Codec<net.minecraft.world.event.PositionSource> CODEC = Registry.POSITION_SOURCE_TYPE.getCodec().dispatch(net.minecraft.world.event.PositionSource::getType, net.minecraft.world.event.PositionSourceType::getCodec);

    Optional<Vec3d> getPos(World world);

    PositionSourceType<?> getType();
}
