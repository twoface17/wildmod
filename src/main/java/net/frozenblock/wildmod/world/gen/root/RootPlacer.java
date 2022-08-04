package net.frozenblock.wildmod.world.gen.root;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.frozenblock.wildmod.world.feature.features.WildTreeFeatureConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;

public abstract class RootPlacer {
    public static final Codec<RootPlacer> TYPE_CODEC = WildRegistry.ROOT_PLACER_TYPE.getCodec().dispatch(RootPlacer::getType, RootPlacerType::getCodec);
    protected final IntProvider trunkOffsetY;
    protected final BlockStateProvider rootProvider;
    protected final Optional<AboveRootPlacement> aboveRootPlacement;

    protected static <P extends RootPlacer> Products.P3<RecordCodecBuilder.Mu<P>, IntProvider, BlockStateProvider, Optional<AboveRootPlacement>> method_43182(RecordCodecBuilder.Instance<P> instance) {
        return instance.group(
                IntProvider.VALUE_CODEC.fieldOf("trunk_offset_y").forGetter(rootPlacer -> rootPlacer.trunkOffsetY),
                BlockStateProvider.TYPE_CODEC.fieldOf("root_provider").forGetter(rootPlacer -> rootPlacer.rootProvider),
                AboveRootPlacement.CODEC.optionalFieldOf("above_root_placement").forGetter(rootPlacer -> rootPlacer.aboveRootPlacement)
        );
    }

    public RootPlacer(IntProvider trunkOffsetY, BlockStateProvider rootProvider, Optional<AboveRootPlacement> aboveRootPlacement) {
        this.trunkOffsetY = trunkOffsetY;
        this.rootProvider = rootProvider;
        this.aboveRootPlacement = aboveRootPlacement;
    }

    protected abstract RootPlacerType<?> getType();

    public abstract boolean generate(
            TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, BlockPos trunkPos, WildTreeFeatureConfig config
    );

    protected boolean canGrowThrough(TestableWorld testableWorld, BlockPos blockPos) {
        return TreeFeature.canReplace(testableWorld, blockPos);
    }

    protected void placeRoots(TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, BlockPos blockPos, WildTreeFeatureConfig config) {
        if (this.canGrowThrough(testableWorld, blockPos)) {
            biConsumer.accept(blockPos, this.applyWaterlogging(testableWorld, blockPos, this.rootProvider.getBlockState(random, blockPos)));
            if (this.aboveRootPlacement.isPresent()) {
                AboveRootPlacement aboveRootPlacement = this.aboveRootPlacement.get();
                BlockPos blockPos2 = blockPos.up();
                if (random.nextFloat() < aboveRootPlacement.aboveRootPlacementChance() && testableWorld.testBlockState(blockPos2, AbstractBlock.AbstractBlockState::isAir)) {
                    biConsumer.accept(
                            blockPos2, this.applyWaterlogging(testableWorld, blockPos2, aboveRootPlacement.aboveRootProvider().getBlockState(random, blockPos2))
                    );
                }
            }
        }
    }

    protected BlockState applyWaterlogging(TestableWorld world, BlockPos pos, BlockState state) {
        return state.contains(Properties.WATERLOGGED)
                ? state.with(Properties.WATERLOGGED, world.testFluidState(pos, fluid -> fluid.isIn(FluidTags.WATER)))
                : state;
    }

    public BlockPos trunkOffset(BlockPos pos, Random random) {
        return pos.up(this.trunkOffsetY.get(random));
    }
}
