package net.frozenblock.wildmod.liukrastapi;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.WildMod;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

/**
 * A GUI interface which handles keyboard and mouse callbacks for child GUI elements.
 * The implementation of a parent element can decide whether a child element receives keyboard and mouse callbacks.
 */
@Environment(EnvType.CLIENT)
public record AnimationChannel(AnimationChannel.Target target, Keyframe... keyframes) {

	@Environment(EnvType.CLIENT)
	public interface Interpolation {
		Codec<Interpolation> CODEC = Codec.unit(() -> (vector3f, f, keyframes, i, j, g) -> null);
		Vec3f apply(Vec3f vec3f, float delta, Keyframe[] keyframes, int start, int end, float f);
	}

	@Environment(EnvType.CLIENT)
	public static class Interpolations {
		public static final AnimationChannel.Interpolation LINEAR = AnimationEasing::easing;
		public static final AnimationChannel.Interpolation CATMULLROM = WildMod.registerInRegistryVanilla(
				WildMod.ANIMATION_CHANNEL_INTERPOLATIONS,
				new Identifier(WildMod.MOD_ID, "catmullrom"),
				(vector3f, f, keyframes, i, j, g) -> {
					Vec3f vector3f2 = keyframes[Math.max(0, i - 1)].target();
					Vec3f vector3f3 = keyframes[i].target();
					Vec3f vector3f4 = keyframes[j].target();
					Vec3f vector3f5 = keyframes[Math.min(keyframes.length - 1, j + 1)].target();
					vector3f.set(
							MathAddon.catmullrom(f, vector3f2.getX(), vector3f3.getX(), vector3f4.getX(), vector3f5.getX()) * g,
							MathAddon.catmullrom(f, vector3f2.getY(), vector3f3.getY(), vector3f4.getY(), vector3f5.getY()) * g,
							MathAddon.catmullrom(f, vector3f2.getZ(), vector3f3.getZ(), vector3f4.getZ(), vector3f5.getZ()) * g
					);
					return vector3f;
				}
		);
		public static final Interpolation EASE_IN_SINE = AnimationEasing.interpolation(AnimationEasing.EasingCategories.SINE, AnimationEasing.Type.IN);
		public static final Interpolation EASE_OUT_SINE = AnimationEasing.interpolation(AnimationEasing.EasingCategories.SINE, AnimationEasing.Type.OUT);
		public static final Interpolation EASE_IN_OUT_SINE = AnimationEasing.interpolation(AnimationEasing.EasingCategories.SINE, AnimationEasing.Type.IN_OUT);
		public static final Interpolation EASE_IN_QUAD = AnimationEasing.interpolation(AnimationEasing.EasingCategories.QUAD, AnimationEasing.Type.IN);
		public static final Interpolation EASE_OUT_QUAD = AnimationEasing.interpolation(AnimationEasing.EasingCategories.QUAD, AnimationEasing.Type.OUT);
		public static final Interpolation EASE_IN_OUT_QUAD = AnimationEasing.interpolation(AnimationEasing.EasingCategories.QUAD, AnimationEasing.Type.IN_OUT);
		public static final Interpolation EASE_IN_CUBIC = AnimationEasing.interpolation(AnimationEasing.EasingCategories.CUBIC, AnimationEasing.Type.IN);
		public static final Interpolation EASE_OUT_CUBIC = AnimationEasing.interpolation(AnimationEasing.EasingCategories.CUBIC, AnimationEasing.Type.OUT);
		public static final Interpolation EASE_IN_OUT_CUBIC = AnimationEasing.interpolation(AnimationEasing.EasingCategories.CUBIC, AnimationEasing.Type.IN_OUT);
		public static final Interpolation EASE_IN_QUART = AnimationEasing.interpolation(AnimationEasing.EasingCategories.QUART, AnimationEasing.Type.IN);
		public static final Interpolation EASE_OUT_QUART = AnimationEasing.interpolation(AnimationEasing.EasingCategories.QUART, AnimationEasing.Type.OUT);
		public static final Interpolation EASE_IN_OUT_QUART = AnimationEasing.interpolation(AnimationEasing.EasingCategories.QUART, AnimationEasing.Type.IN_OUT);
		public static final Interpolation EASE_IN_QUINT = AnimationEasing.interpolation(AnimationEasing.EasingCategories.QUINT, AnimationEasing.Type.IN);
		public static final Interpolation EASE_OUT_QUINT = AnimationEasing.interpolation(AnimationEasing.EasingCategories.QUINT, AnimationEasing.Type.OUT);
		public static final Interpolation EASE_IN_OUT_QUINT = AnimationEasing.interpolation(AnimationEasing.EasingCategories.QUINT, AnimationEasing.Type.IN_OUT);
		public static final Interpolation EASE_IN_EXPO = AnimationEasing.interpolation(AnimationEasing.EasingCategories.EXPO, AnimationEasing.Type.IN);
		public static final Interpolation EASE_OUT_EXPO = AnimationEasing.interpolation(AnimationEasing.EasingCategories.EXPO, AnimationEasing.Type.OUT);
		public static final Interpolation EASE_IN_OUT_EXPO = AnimationEasing.interpolation(AnimationEasing.EasingCategories.EXPO, AnimationEasing.Type.IN_OUT);
		public static final Interpolation EASE_IN_CIRC = AnimationEasing.interpolation(AnimationEasing.EasingCategories.CIRC, AnimationEasing.Type.IN);
		public static final Interpolation EASE_OUT_CIRC = AnimationEasing.interpolation(AnimationEasing.EasingCategories.CIRC, AnimationEasing.Type.OUT);
		public static final Interpolation EASE_IN_OUT_CIRC = AnimationEasing.interpolation(AnimationEasing.EasingCategories.CIRC, AnimationEasing.Type.IN_OUT);
		public static final Interpolation EASE_IN_BACK = AnimationEasing.interpolation(AnimationEasing.EasingCategories.BACK, AnimationEasing.Type.IN);
		public static final Interpolation EASE_OUT_BACK = AnimationEasing.interpolation(AnimationEasing.EasingCategories.BACK, AnimationEasing.Type.OUT);
		public static final Interpolation EASE_IN_OUT_BACK = AnimationEasing.interpolation(AnimationEasing.EasingCategories.BACK, AnimationEasing.Type.IN_OUT);
		public static final Interpolation EASE_IN_ELASTIC = AnimationEasing.interpolation(AnimationEasing.EasingCategories.ELASTIC, AnimationEasing.Type.IN);
		public static final Interpolation EASE_OUT_ELASTIC = AnimationEasing.interpolation(AnimationEasing.EasingCategories.ELASTIC, AnimationEasing.Type.OUT);
		public static final Interpolation EASE_IN_OUT_ELASTIC = AnimationEasing.interpolation(AnimationEasing.EasingCategories.ELASTIC, AnimationEasing.Type.IN_OUT);
		public static final Interpolation EASE_IN_BOUNCE = AnimationEasing.interpolation(AnimationEasing.EasingCategories.BOUNCE, AnimationEasing.Type.IN);
		public static final Interpolation EASE_OUT_BOUNCE = AnimationEasing.interpolation(AnimationEasing.EasingCategories.BOUNCE, AnimationEasing.Type.OUT);
		public static final Interpolation EASE_IN_OUT_BOUNCE = AnimationEasing.interpolation(AnimationEasing.EasingCategories.BOUNCE, AnimationEasing.Type.IN_OUT);

		public static void init() {}
	}

	@Environment(EnvType.CLIENT)
	public interface Target {
		Codec<Target> CODEC = Codec.unit(() -> (modelPart, vec3f) ->{});
		void apply(ModelPart modelPart, Vec3f vector3f);
	}

	@Environment(EnvType.CLIENT)
	public static class Targets {
		public static final AnimationChannel.Target TRANSLATE = (modelPart, vector3f) -> ((ExpandedModelPart)modelPart).offsetPos(vector3f);
		public static final AnimationChannel.Target ROTATE = (modelPart, vector3f) -> ((ExpandedModelPart)modelPart).offsetRotation(vector3f);
		public static final AnimationChannel.Target SCALE = (modelPart, vector3f) -> ((ExpandedModelPart)modelPart).offsetScale(vector3f);

		public Targets() {
		}
	}
}
