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

public class WildSpruceFoliagePlacer extends WildFoliagePlacer {
    public static final Codec<WildSpruceFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return fillFoliagePlacerFields(instance).and(IntProvider.createValidatingCodec(0, 24).fieldOf("trunk_height").forGetter((placer) -> {
            return placer.trunkHeight;
        })).apply(instance, WildSpruceFoliagePlacer::new);
    });
    private final IntProvider trunkHeight;

    public WildSpruceFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider trunkHeight) {
        super(radius, offset);
        this.trunkHeight = trunkHeight;
    }

    protected WildFoliagePlacerType<?> getType() {
        return WildFoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
    }

    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, WildTreeFeatureConfig config, int trunkHeight, WildFoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset) {
        BlockPos blockPos = treeNode.getCenter();
        int i = random.nextInt(2);
        int j = 1;
        int k = 0;

        for(int l = offset; l >= -foliageHeight; --l) {
            this.generateSquare(world, replacer, random, config, blockPos, i, l, treeNode.isGiantTrunk());
            if (i >= j) {
                i = k;
                k = 1;
                j = Math.min(j + 1, radius + treeNode.getFoliageRadius());
            } else {
                ++i;
            }
        }

    }

    public int getRandomHeight(Random random, int trunkHeight, WildTreeFeatureConfig config) {
        return Math.max(4, trunkHeight - this.trunkHeight.get(random));
    }

    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx == radius && dz == radius && radius > 0;
    }
}
