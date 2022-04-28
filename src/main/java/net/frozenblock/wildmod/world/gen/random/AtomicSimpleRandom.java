package net.frozenblock.wildmod.world.gen.random;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.LockHelper;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.BaseSimpleRandom;
import net.minecraft.world.gen.random.GaussianGenerator;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicSimpleRandom implements BaseSimpleRandom {
    private static final int INT_BITS = 48;
    private static final long SEED_MASK = 281474976710655L;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();
    private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

    public AtomicSimpleRandom(long seed) {
        this.setSeed(seed);
    }

    public AbstractRandom derive() {
        return new AtomicSimpleRandom(this.nextLong());
    }

    public net.minecraft.world.gen.random.RandomDeriver createRandomDeriver() {
        return new AtomicSimpleRandom.RandomDeriver(this.nextLong());
    }

    public void setSeed(long seed) {
        if (!this.seed.compareAndSet(this.seed.get(), (seed ^ 25214903917L) & 281474976710655L)) {
            throw LockHelper.crash("LegacyRandomSource", null);
        } else {
            this.gaussianGenerator.reset();
        }
    }

    public int next(int bits) {
        long l = this.seed.get();
        long m = l * 25214903917L + 11L & 281474976710655L;
        if (!this.seed.compareAndSet(l, m)) {
            throw LockHelper.crash("LegacyRandomSource", null);
        } else {
            return (int)(m >> 48 - bits);
        }
    }

    public double nextGaussian() {
        return this.gaussianGenerator.next();
    }

    public static class RandomDeriver implements net.minecraft.world.gen.random.RandomDeriver {
        private final long seed;

        public RandomDeriver(long seed) {
            this.seed = seed;
        }

        public AbstractRandom createRandom(int x, int y, int z) {
            long l = MathHelper.hashCode(x, y, z);
            long m = l ^ this.seed;
            return new AtomicSimpleRandom(m);
        }

        public AbstractRandom createRandom(String string) {
            int i = string.hashCode();
            return new AtomicSimpleRandom((long)i ^ this.seed);
        }

        @VisibleForTesting
        public void addDebugInfo(StringBuilder info) {
            info.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
        }
    }
}
