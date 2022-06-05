package net.frozenblock.wildmod.liukrastapi;

import net.frozenblock.wildmod.world.feature.WildConfiguredFeatures;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class MangroveSaplingGenerator extends SaplingGenerator {
    private final float tallChance;

    public MangroveSaplingGenerator(float tallChance) {
        this.tallChance = tallChance;
    }

    @Nullable
    protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
        return random.nextFloat() < this.tallChance ? WildConfiguredFeatures.TALL_MANGROVE : WildConfiguredFeatures.MANGROVE;
    }
}
