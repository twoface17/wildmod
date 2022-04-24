package frozenblock.wild.mod.world.gen.random;

import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;

public class WildAbstractRandom {
    public static AbstractRandom createAtomic() {
        return createAtomic(System.nanoTime());
    }

    public static AbstractRandom createAtomic(long seed) {
        return new AtomicSimpleRandom(seed);
    }
}
