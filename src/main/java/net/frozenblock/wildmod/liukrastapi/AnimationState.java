package net.frozenblock.wildmod.liukrastapi;

import net.minecraft.util.Util;

import java.util.function.Consumer;

public class AnimationState {
	private static final long field_37417 = Long.MAX_VALUE;
	private long startedAt = Long.MAX_VALUE;
	private long field_39112;

	public AnimationState() {
	}

	public void start() {
		this.startedAt = Util.getMeasuringTimeMs();
		this.field_39112 = 0L;
	}

	public void startIfNotRunning() {
		if (!this.isRunning()) {
			this.start();
		}
	}

	public void stop() {
		this.startedAt = Long.MAX_VALUE;
	}

	public void run(Consumer<AnimationState> consumer) {
		if (this.isRunning()) {
			consumer.accept(this);
		}
	}

	public void method_43686(boolean bl, float f) {
		if (this.isRunning()) {
			long l = Util.getMeasuringTimeMs();
			if (!bl) {
				this.field_39112 += (l - this.startedAt) * f;
			}

			this.startedAt = l;
		}
	}

	public long method_43687() {
		return this.field_39112;
	}

	public boolean isRunning() {
		return this.startedAt != Long.MAX_VALUE;
	}
}
