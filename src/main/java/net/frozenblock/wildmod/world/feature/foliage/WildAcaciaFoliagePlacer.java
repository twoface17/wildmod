package net.frozenblock.wildmod.world.feature.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.world.feature.features.WildTreeFeatureConfig;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;

import java.util.Random;
import java.util.function.BiConsumer;

public class WildAcaciaFoliagePlacer extends WildFoliagePlacer {
    public static final Codec<WildAcaciaFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return fillFoliagePlacerFields(instance).apply(instance, (WildAcaciaFoliagePlacer::new));
    });

    public WildAcaciaFoliagePlacer(IntProvider intProvider, IntProvider intProvider2) {
        super(intProvider, intProvider2);
    }

    protected WildFoliagePlacerType<?> getType() {
        return WildFoliagePlacerType.ACACIA_FOLIAGE_PLACER;
    }

    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, WildTreeFeatureConfig config, int trunkHeight, WildFoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset) {
        boolean bl = treeNode.isGiantTrunk();
        BlockPos blockPos = treeNode.getCenter().up(offset);
        this.generateSquare(world, replacer, random, config, blockPos, radius + treeNode.getFoliageRadius(), -1 - foliageHeight, bl);
        this.generateSquare(world, replacer, random, config, blockPos, radius - 1, -foliageHeight, bl);
        this.generateSquare(world, replacer, random, config, blockPos, radius + treeNode.getFoliageRadius() - 1, 0, bl);
    }

    public int getRandomHeight(Random random, int trunkHeight, WildTreeFeatureConfig config) {
        return 0;
    }

    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        if (y == 0) {
            return (dx > 1 || dz > 1) && dx != 0 && dz != 0;
        } else {
            return dx == radius && dz == radius && radius > 0;
        }
    }
}
