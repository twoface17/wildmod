package net.frozenblock.wildmod.liukrastapi.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.liukrastapi.ExpandedModelPart;
import net.frozenblock.wildmod.liukrastapi.MathAdvanced;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public record Transformation(Target target, Keyframe... keyframes) {
	public Transformation(Target target, Keyframe... keyframes) {
		this.target = target;
		this.keyframes = keyframes;
	}

	public Target target() {
		return this.target;
	}

	public Keyframe[] keyframes() {
		return this.keyframes;
	}

	@Environment(EnvType.CLIENT)
	public interface Target {
		void apply(ModelPart modelPart, Vec3f vec3f);
	}

	@Environment(EnvType.CLIENT)
	public static class Interpolations {
		public static final Interpolation field_37884 = (vec3f, delta, keyframes, start, end, f) -> {
			Vec3f vec3f2 = keyframes[start].target();
			Vec3f vec3f3 = keyframes[end].target();
			vec3f.set(MathHelper.lerp(delta, vec3f2.getX(), vec3f3.getX()) * f, MathHelper.lerp(delta, vec3f2.getY(), vec3f3.getY()) * f, MathHelper.lerp(delta, vec3f2.getZ(), vec3f3.getZ()) * f);
			return vec3f;
		};
		public static final Interpolation field_37885 = (vec3f, delta, keyframes, start, end, f) -> {
			Vec3f vec3f2 = keyframes[Math.max(0, start - 1)].target();
			Vec3f vec3f3 = keyframes[start].target();
			Vec3f vec3f4 = keyframes[end].target();
			Vec3f vec3f5 = keyframes[Math.min(keyframes.length - 1, end + 1)].target();
			vec3f.set(MathAdvanced.method_41303(delta, vec3f2.getX(), vec3f3.getX(), vec3f4.getX(), vec3f5.getX()) * f, MathAdvanced.method_41303(delta, vec3f2.getY(), vec3f3.getY(), vec3f4.getY(), vec3f5.getY()) * f, MathAdvanced.method_41303(delta, vec3f2.getZ(), vec3f3.getZ(), vec3f4.getZ(), vec3f5.getZ()) * f);
			return vec3f;
		};

		public static void init() {
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Targets {
		public static final Target TRANSLATE = (modelPart, vector3f) -> ((ExpandedModelPart)modelPart).translate(vector3f);
		public static final Target ROTATE = (modelPart, vector3f) -> ((ExpandedModelPart)modelPart).rotate(vector3f);
		public static final Target SCALE = (modelPart, vector3f) -> ((ExpandedModelPart)modelPart).scale(vector3f);

		public Targets() {
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Interpolation {
		Vec3f apply(Vec3f vec3f, float delta, Keyframe[] keyframes, int start, int end, float f);
	}
}
