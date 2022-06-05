package net.frozenblock.wildmod.world.feature.foliage;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.world.feature.features.WildTreeFeatureConfig;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import java.util.Random;
import java.util.function.BiConsumer;

public class WildBlobFoliagePlacer extends WildFoliagePlacer {
    public static final Codec<WildBlobFoliagePlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return createCodec(instance).apply(instance, WildBlobFoliagePlacer::new);
    });
    protected final int height;

    protected static <P extends WildBlobFoliagePlacer> Products.P3<RecordCodecBuilder.Mu<P>, IntProvider, IntProvider, Integer> createCodec(RecordCodecBuilder.Instance<P> builder) {
        return fillFoliagePlacerFields(builder).and(Codec.intRange(0, 16).fieldOf("height").forGetter((placer) -> {
            return placer.height;
        }));
    }

    public WildBlobFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset);
        this.height = height;
    }

    protected WildFoliagePlacerType<?> getType() {
        return WildFoliagePlacerType.BLOB_FOLIAGE_PLACER;
    }

    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, WildTreeFeatureConfig config, int trunkHeight, WildFoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset) {
        for(int i = offset; i >= offset - foliageHeight; --i) {
            int j = Math.max(radius + treeNode.getFoliageRadius() - 1 - i / 2, 0);
            this.generateSquare(world, replacer, random, config, treeNode.getCenter(), j, i, treeNode.isGiantTrunk());
        }

    }

    public int getRandomHeight(Random random, int trunkHeight, WildTreeFeatureConfig config) {
        return this.height;
    }

    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx == radius && dz == radius && (random.nextInt(2) == 0 || y == 0);
    }
}
