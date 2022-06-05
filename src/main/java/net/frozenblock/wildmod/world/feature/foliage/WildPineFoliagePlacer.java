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

public class WildPineFoliagePlacer extends WildFoliagePlacer {
    public static final Codec<WildPineFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return fillFoliagePlacerFields(instance).and(IntProvider.createValidatingCodec(0, 24).fieldOf("height").forGetter((placer) -> {
            return placer.height;
        })).apply(instance, (WildPineFoliagePlacer::new));
    });
    private final IntProvider height;

    public WildPineFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
        super(radius, offset);
        this.height = height;
    }

    protected WildFoliagePlacerType<?> getType() {
        return WildFoliagePlacerType.PINE_FOLIAGE_PLACER;
    }

    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, WildTreeFeatureConfig config, int trunkHeight, WildFoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset) {
        int i = 0;

        for(int j = offset; j >= offset - foliageHeight; --j) {
            this.generateSquare(world, replacer, random, config, treeNode.getCenter(), i, j, treeNode.isGiantTrunk());
            if (i >= 1 && j == offset - foliageHeight + 1) {
                --i;
            } else if (i < radius + treeNode.getFoliageRadius()) {
                ++i;
            }
        }

    }

    public int getRandomRadius(Random random, int baseHeight) {
        return super.getRandomRadius(random, baseHeight) + random.nextInt(Math.max(baseHeight + 1, 1));
    }

    public int getRandomHeight(Random random, int trunkHeight, WildTreeFeatureConfig config) {
        return this.height.get(random);
    }

    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx == radius && dz == radius && radius > 0;
    }
}
