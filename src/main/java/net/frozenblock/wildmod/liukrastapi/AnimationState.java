package net.frozenblock.wildmod.liukrastapi;

import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class AnimationState {
	private static final long field_39275 = Long.MIN_VALUE;
	private static final long field_37417 = Long.MAX_VALUE;
	private long updatedAt = Long.MAX_VALUE;
	private long timeRunning;

	public void start() {
		this.updatedAt = Long.MIN_VALUE;
		this.timeRunning = 0L;
	}

	public void startIfNotRunning() {
		if (!this.isRunning()) {
			this.start();
		}

	}

	public void stop() {
		this.updatedAt = Long.MAX_VALUE;
	}

	public void run(Consumer<AnimationState> consumer) {
		if (this.isRunning()) {
			consumer.accept(this);
		}

	}

	public void update(float f, float g) {
		if (this.isRunning()) {
			long l = MathHelper.lfloor((double)(f * 1000.0F / 20.0F));
			if (this.updatedAt == Long.MIN_VALUE) {
				this.updatedAt = l;
			}

			this.timeRunning += (long)((float)(l - this.updatedAt) * g);
			this.updatedAt = l;
		}
	}

	public long getTimeRunning() {
		return this.timeRunning;
	}

	public boolean isRunning() {
		return this.updatedAt != Long.MAX_VALUE;
	}
}
