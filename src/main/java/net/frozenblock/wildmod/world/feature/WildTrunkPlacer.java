package net.frozenblock.wildmod.world.feature;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.frozenblock.wildmod.world.feature.features.WildTreeFeature;
import net.frozenblock.wildmod.world.feature.features.WildTreeFeatureConfig;
import net.frozenblock.wildmod.world.feature.foliage.WildFoliagePlacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class WildTrunkPlacer {
    public static final Codec<WildTrunkPlacer> TYPE_CODEC;
    private static final int MAX_BASE_HEIGHT = 32;
    private static final int MAX_RANDOM_HEIGHT = 24;
    public static final int field_31530 = 80;
    protected final int baseHeight;
    protected final int firstRandomHeight;
    protected final int secondRandomHeight;

    protected static <P extends WildTrunkPlacer> Products.P3<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer> fillTrunkPlacerFields(RecordCodecBuilder.Instance<P> instance) {
        return instance.group(Codec.intRange(0, 32).fieldOf("base_height").forGetter((placer) -> {
            return placer.baseHeight;
        }), Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter((placer) -> {
            return placer.firstRandomHeight;
        }), Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter((placer) -> {
            return placer.secondRandomHeight;
        }));
    }

    public WildTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        this.baseHeight = baseHeight;
        this.firstRandomHeight = firstRandomHeight;
        this.secondRandomHeight = secondRandomHeight;
    }

    protected abstract WildTrunkPlacerType<?> getType();

    /**
     * Generates the trunk blocks and return a list of tree nodes to place foliage around
     */
    public abstract List<WildFoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, WildTreeFeatureConfig config);

    public int getHeight(Random random) {
        return this.baseHeight + random.nextInt(this.firstRandomHeight + 1) + random.nextInt(this.secondRandomHeight + 1);
    }

    private static boolean canGenerate(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, (state) -> {
            return Feature.isSoil(state) && !state.isOf(Blocks.GRASS_BLOCK) && !state.isOf(Blocks.MYCELIUM);
        });
    }

    protected static void setToDirt(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, WildTreeFeatureConfig config) {
        if (config.forceDirt || !canGenerate(world, pos)) {
            replacer.accept(pos, config.dirtProvider.getBlockState(random, pos));
        }

    }

    protected boolean getAndSetState(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, WildTreeFeatureConfig config) {
        return this.getAndSetState(world, replacer, random, pos, config, Function.identity());
    }

    protected boolean getAndSetState(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, WildTreeFeatureConfig config, Function<BlockState, BlockState> function) {
        if (this.canReplace(world, pos)) {
            replacer.accept(pos, function.apply(config.trunkProvider.getBlockState(random, pos)));
            return true;
        } else {
            return false;
        }
    }

    protected void trySetState(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos.Mutable pos, WildTreeFeatureConfig config) {
        if (this.canReplaceOrIsLog(world, pos)) {
            this.getAndSetState(world, replacer, random, pos, config);
        }

    }

    protected boolean canReplace(TestableWorld world, BlockPos pos) {
        return WildTreeFeature.canReplace(world, pos);
    }

    public boolean canReplaceOrIsLog(TestableWorld world, BlockPos pos) {
        return this.canReplace(world, pos) || world.testBlockState(pos, (state) -> {
            return state.isIn(BlockTags.LOGS);
        });
    }

    static {
        TYPE_CODEC = WildRegistry.TRUNK_PLACER_TYPE.getCodec().dispatch(WildTrunkPlacer::getType, WildTrunkPlacerType::getCodec);
    }
}
