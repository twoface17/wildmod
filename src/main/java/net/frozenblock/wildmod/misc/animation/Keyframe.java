package net.frozenblock.wildmod.misc.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public record Keyframe(float timestamp, Vec3f target, Transformation.Interpolation interpolation) {
    public Keyframe(float timestamp, Vec3f target, Transformation.Interpolation interpolation) {
        this.timestamp = timestamp;
        this.target = target;
        this.interpolation = interpolation;
    }

    public float timestamp() {
        return this.timestamp;
    }

    public Vec3f target() {
        return this.target;
    }

    public Transformation.Interpolation interpolation() {
        return this.interpolation;
    }
}
