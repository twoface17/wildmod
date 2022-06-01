package net.frozenblock.wildmod.liukrastapi.animation;

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
 */
@Environment(EnvType.CLIENT)
public record Animation(float lengthInSeconds, boolean looping, Map<String, List<Transformation>> boneAnimations) {
	public Animation(float lengthInSeconds, boolean looping, Map<String, List<Transformation>> boneAnimations) {
		this.lengthInSeconds = lengthInSeconds;
		this.looping = looping;
		this.boneAnimations = boneAnimations;
	}

	public float lengthInSeconds() {
		return this.lengthInSeconds;
	}

	public boolean looping() {
		return this.looping;
	}

	public Map<String, List<Transformation>> boneAnimations() {
		return this.boneAnimations;
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final float lengthInSeconds;
		private final Map<String, List<Transformation>> transformations = Maps.newHashMap();
		private boolean looping;

		public static Builder create(float lengthInSeconds) {
			return new Builder(lengthInSeconds);
		}

		private Builder(float lengthInSeconds) {
			this.lengthInSeconds = lengthInSeconds;
		}

		public Builder looping() {
			this.looping = true;
			return this;
		}

		public Builder addBoneAnimation(String name, Transformation transformation) {
			((List)this.transformations.computeIfAbsent(name, (namex) -> {
				return Lists.newArrayList();
			})).add(transformation);
			return this;
		}

		public Animation build() {
			return new Animation(this.lengthInSeconds, this.looping, this.transformations);
		}
	}
}
