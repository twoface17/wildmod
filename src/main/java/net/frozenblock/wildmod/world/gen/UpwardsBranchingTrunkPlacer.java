package net.frozenblock.wildmod.world.gen;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.world.feature.WildTreeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class UpwardsBranchingTrunkPlacer extends TrunkPlacer {
    public static final Codec<UpwardsBranchingTrunkPlacer> CODEC = RecordCodecBuilder.create(
            instance -> fillTrunkPlacerFields(instance)
                    .and(
                            instance.group(
                                    IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter(trunkPlacer -> trunkPlacer.extraBranchSteps),
                                    Codec.floatRange(0.0F, 1.0F)
                                            .fieldOf("place_branch_per_log_probability")
                                            .forGetter(trunkPlacer -> trunkPlacer.placeBranchPerLogProbability),
                                    IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter(trunkPlacer -> trunkPlacer.extraBranchLength),
                                    RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("can_grow_through").forGetter(trunkPlacer -> trunkPlacer.canGrowThrough)
                            )
                    )
                    .apply(instance, UpwardsBranchingTrunkPlacer::new)
    );
    private final IntProvider extraBranchSteps;
    private final float placeBranchPerLogProbability;
    private final IntProvider extraBranchLength;
    public final RegistryEntryList<Block> canGrowThrough;

    public UpwardsBranchingTrunkPlacer(
            int baseHeight,
            int firstRandomHeight,
            int secondRandomHeight,
            IntProvider extraBranchSteps,
            float placeBranchPerLogProbability,
            IntProvider extraBranchLength,
            RegistryEntryList<Block> canGrowThrough
    ) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
        this.extraBranchSteps = extraBranchSteps;
        this.placeBranchPerLogProbability = placeBranchPerLogProbability;
        this.extraBranchLength = extraBranchLength;
        this.canGrowThrough = canGrowThrough;
    }

    public TrunkPlacerType<?> getType() {
        return WildTreeRegistry.UPWARDS_BRANCHING_TRUNK_PLACER;
    }


    public List<FoliagePlacer.TreeNode> generate(
            TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config
    ) {
        List<FoliagePlacer.TreeNode> list = Lists.newArrayList();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int i = 0; i < height; ++i) {
            int j = startPos.getY() + i;
            if (getAndSetState(world, replacer, random, mutable.set(startPos.getX(), j, startPos.getZ()), config)
                    && i < height - 1
                    && random.nextFloat() < this.placeBranchPerLogProbability) {
                Direction direction = Direction.Type.HORIZONTAL.random(random);
                int k = this.extraBranchLength.get(random);
                int l = Math.max(0, k - this.extraBranchLength.get(random) - 1);
                int m = this.extraBranchSteps.get(random);
                this.generateExtraBranch(world, replacer, random, height, config, list, mutable, j, direction, l, m);
            }

            if (i == height - 1) {
                list.add(new FoliagePlacer.TreeNode(mutable.set(startPos.getX(), j + 1, startPos.getZ()), 0, false));
            }
        }

        return list;
    }

    private void generateExtraBranch(
            TestableWorld world,
            BiConsumer<BlockPos, BlockState> replacer,
            Random random,
            int height,
            TreeFeatureConfig config,
            List<FoliagePlacer.TreeNode> nodes,
            BlockPos.Mutable pos,
            int yOffset,
            Direction direction,
            int length,
            int steps
    ) {
        int i = 0;
        int j = pos.getX();
        int k = pos.getZ();

        for (int l = length; l < height && steps > 0; --steps) {
            if (l >= 1) {
                int m = yOffset + l;
                j += direction.getOffsetX();
                k += direction.getOffsetZ();
                if (getAndSetState(world, replacer, random, pos.set(j, m, k), config)) {
                    i = m + 1;
                }

                nodes.add(new FoliagePlacer.TreeNode(pos.toImmutable(), 0, false));
            }

            ++l;
        }

        if (i - yOffset > 1) {
            BlockPos blockPos = new BlockPos(j, i, k);
            nodes.add(new FoliagePlacer.TreeNode(blockPos, 0, false));
            nodes.add(new FoliagePlacer.TreeNode(blockPos.down(2), 0, false));
        }

    }
}
