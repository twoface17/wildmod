package net.frozenblock.wildmod.liukrastapi.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.liukrastapi.ExpandedSinglePartEntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AnimationHelper {
	public static void animate(SinglePartEntityModel<?> model, Animation animation, long runningTime, float f, Vec3f vec3f) {
		float g = getRunningSeconds(animation, runningTime);
		Iterator<Entry<String, List<Transformation>>> var7 = animation.boneAnimations().entrySet().iterator();

		while(var7.hasNext()) {
			Map.Entry<String, List<Transformation>> entry = var7.next();
			Optional<ModelPart> optional = ((ExpandedSinglePartEntityModel)model).getChild(entry.getKey());
			List<Transformation> list = entry.getValue();
			optional.ifPresent((part) -> {
				list.forEach((transformation) -> {
					Keyframe[] keyframes = transformation.keyframes();
					int i = Math.max(0, MathHelper.binarySearch(0, keyframes.length, (index) -> {
						return g <= keyframes[index].timestamp();
					}) - 1);
					int j = Math.min(keyframes.length - 1, i + 1);
					Keyframe keyframe = keyframes[i];
					Keyframe keyframe2 = keyframes[j];
					float h = g - keyframe.timestamp();
					float k = MathHelper.clamp(h / (keyframe2.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
					keyframe2.interpolation().apply(vec3f, k, keyframes, i, j, f);
					transformation.target().apply(part, vec3f);
				});
			});
		}

	}

	private static float getRunningSeconds(Animation animation, long runningTime) {
		float f = (float)runningTime / 1000.0F;
		return animation.looping() ? f % animation.lengthInSeconds() : f;
	}

	public static Vec3f method_41823(float f, float g, float h) {
		return new Vec3f(f, -g, h);
	}

	public static Vec3f method_41829(float f, float g, float h) {
		return new Vec3f(f * 0.017453292F, g * 0.017453292F, h * 0.017453292F);
	}

	public static Vec3f method_41822(double d, double e, double f) {
		return new Vec3f((float)(d - 1.0), (float)(e - 1.0), (float)(f - 1.0));
	}
}
