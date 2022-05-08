package net.frozenblock.wildmod.liukrastapi;

import com.mojang.serialization.Codec;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public class Vec3d extends net.minecraft.util.math.Vec3d {

    public Vec3d(double x, double y, double z) {
        super(x, y, z);
    }

    public static Object ofCenter(net.minecraft.util.math.Vec3d vec3d) {
        return new Vec3d((double)vec3d.getX() + 0.5, vec3d.getY() + 0.5, vec3d.getZ() + 0.5);
    }

    public Vec3d withBias(Direction direction, double value) {
        Vec3i vec3i = direction.getVector();
        return new Vec3d(this.x + value * (double)vec3i.getX(), this.y + value * (double)vec3i.getY(), this.z + value * (double)vec3i.getZ());
    }

    public static final Codec<Vec3d> CODEC = Codec.DOUBLE.listOf().comapFlatMap((list -> {
        return Util.toArray(list, 3).map((listx) -> {
            return new Vec3d((Double)listx.get(0), (Double)listx.get(1), (Double)listx.get(2));
        });
    }), (vec3d) -> {
        return List.of(vec3d.getX(), vec3d.getY(), vec3d.getZ());
    });

    public static Vec3d ofCenter(Vec3i vec) {
        return new Vec3d((double)vec.getX() + 0.5, (double)vec.getY() + 0.5, (double)vec.getZ() + 0.5);
    }
}
