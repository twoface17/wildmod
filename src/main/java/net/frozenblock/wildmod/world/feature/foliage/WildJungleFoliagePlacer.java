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

public class WildJungleFoliagePlacer extends WildFoliagePlacer {
    public static final Codec<WildJungleFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return fillFoliagePlacerFields(instance).and(Codec.intRange(0, 16).fieldOf("height").forGetter((placer) -> {
            return placer.height;
        })).apply(instance, (WildJungleFoliagePlacer::new));
    });
    protected final int height;

    public WildJungleFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset);
        this.height = height;
    }

    protected WildFoliagePlacerType<?> getType() {
        return WildFoliagePlacerType.JUNGLE_FOLIAGE_PLACER;
    }

    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, WildTreeFeatureConfig config, int trunkHeight, WildFoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset) {
        int i = treeNode.isGiantTrunk() ? foliageHeight : 1 + random.nextInt(2);

        for (int j = offset; j >= offset - i; --j) {
            int k = radius + treeNode.getFoliageRadius() + 1 - j;
            this.generateSquare(world, replacer, random, config, treeNode.getCenter(), k, j, treeNode.isGiantTrunk());
        }

    }

    public int getRandomHeight(Random random, int trunkHeight, WildTreeFeatureConfig config) {
        return this.height;
    }

    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        if (dx + dz >= 7) {
            return true;
        } else {
            return dx * dx + dz * dz > radius * radius;
        }
    }
}
