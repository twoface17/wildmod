package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.registry.RegisterWorldGen;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

import java.util.Random;

public class MangroveSaplingGenerator extends SaplingGenerator {
    @Override
    protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
        return RegisterWorldGen.MANGROVE;
    }
}
