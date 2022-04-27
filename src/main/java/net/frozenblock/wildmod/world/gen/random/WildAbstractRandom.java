package net.frozenblock.wildmod.world.gen.random;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import net.minecraft.world.gen.random.SimpleRandom;

public interface WildAbstractRandom extends net.minecraft.world.gen.random.AbstractRandom {
    @Deprecated
    double field_38930 = 2.297;

    static AbstractRandom createAtomic() {
        return createAtomic(System.nanoTime());
    }

    @Deprecated
    static AbstractRandom createBlocking() {
        return new BlockingSimpleRandom(System.nanoTime());
    }

    static AbstractRandom createAtomic(long seed) {
        return new AtomicSimpleRandom(seed);
    }

    static AbstractRandom create() {
        return new SimpleRandom(ThreadLocalRandom.current().nextLong());
    }

    AbstractRandom derive();

    RandomDeriver createRandomDeriver();

    void setSeed(long seed);

    int nextInt();

    int nextInt(int bound);

    default int nextBetween(int min, int max) {
        return this.nextInt(max - min + 1) + min;
    }

    long nextLong();

    boolean nextBoolean();

    float nextFloat();

    double nextDouble();

    double nextGaussian();

    default double nextPredictable(double base, double variance) {
        return base + variance * (this.nextDouble() - this.nextDouble());
    }

    default void skip(int count) {
        for(int i = 0; i < count; ++i) {
            this.nextInt();
        }

    }

    default int nextBetweenExclusive(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("bound - origin is non positive");
        } else {
            return min + this.nextInt(max - min);
        }
    }
}
