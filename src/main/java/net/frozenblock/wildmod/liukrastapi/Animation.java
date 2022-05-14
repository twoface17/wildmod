/*package net.frozenblock.wildmod.liukrastapi;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

/**
 * Base GUI interface for handling callbacks related to
 * keyboard or mouse actions.
 * 
 * Mouse coordinate is bounded by the size of the window in
 * pixels.
 *//*
@Environment(EnvType.CLIENT)
public record Animation(float lengthInSeconds, boolean looping, Map<String, List<Transformation>> boneAnimations) {
	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final float lengthInSeconds;
		private final Map<String, List<Transformation>> transformations = Maps.newHashMap();
		private boolean looping;

		public static Animation.Builder create(float lengthInSeconds) {
			return new Animation.Builder(lengthInSeconds);
		}

		private Builder(float lengthInSeconds) {
			this.lengthInSeconds = lengthInSeconds;
		}

		public Animation.Builder looping() {
			this.looping = true;
			return this;
		}

		public Animation.Builder addBoneAnimation(String name, Transformation transformation) {
			(this.transformations.computeIfAbsent(name, namex -> Lists.newArrayList())).add(transformation);
			return this;
		}

		public Animation build() {
			return new Animation(this.lengthInSeconds, this.looping, this.transformations);
		}
	}
}
*/