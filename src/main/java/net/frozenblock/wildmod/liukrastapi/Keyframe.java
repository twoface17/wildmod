package net.frozenblock.wildmod.liukrastapi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.liukrastapi.Transformation.Interpolation;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public record Keyframe(float timestamp, Vec3f target, Interpolation interpolation) {
}
