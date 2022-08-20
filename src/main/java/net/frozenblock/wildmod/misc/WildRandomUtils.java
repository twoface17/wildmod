package net.frozenblock.wildmod.misc;

import net.frozenblock.wildmod.world.gen.random.BlockingSimpleRandom;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;

import java.util.Random;

public class WildRandomUtils {

    @Deprecated
    public static AbstractRandom createBlocking() {
        return new BlockingSimpleRandom(System.nanoTime());
    }

    public static AbstractRandom createAtomic() {
        return createAtomic(System.nanoTime());
    }

    public static AbstractRandom createAtomic(long seed) {
        return new AtomicSimpleRandom(seed);
    }

    public static int nextBetweenExclusive(Random random, int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("bound - origin is non positive");
        } else {
            return min + random.nextInt(max - min);
        }
    }
}
