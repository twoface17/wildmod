package frozenblock.wild.mod.liukrastapi;

import net.minecraft.util.Util;

import java.util.function.Consumer;

public class AnimationState {
	private static final long STOPPED = 9223372036854775807L;
	private long startedAt = 9223372036854775807L;

	public AnimationState() {
	}

	public void start() {
		this.startedAt = Util.getMeasuringTimeMs();
	}

	public void startIfNotRunning() {
		if (!this.isRunning()) {
			this.start();
		}
	}

	public void stop() {
		this.startedAt = STOPPED;
	}

	public long getStartTime() {
		return this.startedAt;
	}

	public void run(Consumer<AnimationState> consumer) {
		if (this.isRunning()) {
			consumer.accept(this);
		}
	}

	private boolean isRunning() {
		return this.startedAt != STOPPED;
	}
}
