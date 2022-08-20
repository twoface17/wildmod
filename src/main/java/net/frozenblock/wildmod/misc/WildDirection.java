package net.frozenblock.wildmod.misc;

import net.minecraft.util.math.Direction;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;

public class WildDirection {

    public static Stream<Direction> stream() {
        return Stream.of(Direction.values());
    }

    public static Collection<Direction> shuffle(Random random) {
        return WildUtil.copyShuffled(Direction.values(), random);
    }

}
