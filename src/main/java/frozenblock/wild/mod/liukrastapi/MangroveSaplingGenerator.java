package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.registry.RegisterWorldgen;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.Random;

public class MangroveSaplingGenerator extends SaplingGenerator {
    @Override
    protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
        return RegisterWorldgen.MANGROVE;
    }
}
