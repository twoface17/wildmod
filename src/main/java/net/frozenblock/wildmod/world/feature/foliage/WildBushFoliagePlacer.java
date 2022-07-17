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

public class WildBushFoliagePlacer extends WildBlobFoliagePlacer {
    public static final Codec<WildBushFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return createCodec(instance).apply(instance, (WildBushFoliagePlacer::new));
    });

    public WildBushFoliagePlacer(IntProvider intProvider, IntProvider intProvider2, int i) {
        super(intProvider, intProvider2, i);
    }

    protected WildFoliagePlacerType<?> getType() {
        return WildFoliagePlacerType.BUSH_FOLIAGE_PLACER;
    }

    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, WildTreeFeatureConfig config, int trunkHeight, WildFoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset) {
        for (int i = offset; i >= offset - foliageHeight; --i) {
            int j = radius + treeNode.getFoliageRadius() - 1 - i;
            this.generateSquare(world, replacer, random, config, treeNode.getCenter(), j, i, treeNode.isGiantTrunk());
        }

    }

    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx == radius && dz == radius && random.nextInt(2) == 0;
    }
}
