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

public class WildRandomSpreadFoliagePlacer extends WildFoliagePlacer {
    public static final Codec<WildRandomSpreadFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return fillFoliagePlacerFields(instance).and(instance.group(IntProvider.createValidatingCodec(1, 512).fieldOf("foliage_height").forGetter((placer) -> {
            return placer.foliageHeight;
        }), Codec.intRange(0, 256).fieldOf("leaf_placement_attempts").forGetter((placer) -> {
            return placer.leafPlacementAttempts;
        }))).apply(instance, (WildRandomSpreadFoliagePlacer::new));
    });
    private final IntProvider foliageHeight;
    private final int leafPlacementAttempts;

    public WildRandomSpreadFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider foliageHeight, int leafPlacementAttempts) {
        super(radius, offset);
        this.foliageHeight = foliageHeight;
        this.leafPlacementAttempts = leafPlacementAttempts;
    }

    protected WildFoliagePlacerType<?> getType() {
        return WildFoliagePlacerType.RANDOM_SPREAD_FOLIAGE_PLACER;
    }

    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, WildTreeFeatureConfig config, int trunkHeight, WildFoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset) {
        BlockPos blockPos = treeNode.getCenter();
        BlockPos.Mutable mutable = blockPos.mutableCopy();

        for (int i = 0; i < this.leafPlacementAttempts; ++i) {
            mutable.set(blockPos, random.nextInt(radius) - random.nextInt(radius), random.nextInt(foliageHeight) - random.nextInt(foliageHeight), random.nextInt(radius) - random.nextInt(radius));
            placeFoliageBlock(world, replacer, random, config, mutable);
        }

    }

    public int getRandomHeight(Random random, int trunkHeight, WildTreeFeatureConfig config) {
        return this.foliageHeight.get(random);
    }

    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return false;
    }
}
