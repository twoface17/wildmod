package net.frozenblock.wildmod.misc;

import com.mojang.serialization.Codec;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public class WildVec3d extends net.minecraft.util.math.Vec3d {

    public WildVec3d(double x, double y, double z) {
        super(x, y, z);
    }

    public static Object ofCenter(net.minecraft.util.math.Vec3d vec3d) {
        return new WildVec3d(vec3d.getX() + 0.5, vec3d.getY() + 0.5, vec3d.getZ() + 0.5);
    }

    public WildVec3d withBias(Direction direction, double value) {
        Vec3i vec3i = direction.getVector();
        return new WildVec3d(this.x + value * (double) vec3i.getX(), this.y + value * (double) vec3i.getY(), this.z + value * (double) vec3i.getZ());
    }

    public static WildVec3d withBias(Vec3d vec3d, Direction direction, double value) {
        Vec3i vec3i = direction.getVector();
        return new WildVec3d(vec3d.x + value * (double) vec3i.getX(), vec3d.y + value * (double) vec3i.getY(), vec3d.z + value * (double) vec3i.getZ());
    }

    public static final Codec<Vec3d> CODEC = Codec.DOUBLE.listOf().comapFlatMap((list -> {
        return Util.toArray(list, 3).map((listx) -> {
            return new Vec3d(listx.get(0), listx.get(1), listx.get(2));
        });
    }), (vec3d) -> {
        return List.of(vec3d.getX(), vec3d.getY(), vec3d.getZ());
    });

    public static WildVec3d ofCenter(Vec3i vec) {
        return new WildVec3d((double) vec.getX() + 0.5, (double) vec.getY() + 0.5, (double) vec.getZ() + 0.5);
    }
}
