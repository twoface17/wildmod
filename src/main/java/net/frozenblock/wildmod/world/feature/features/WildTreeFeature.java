package net.frozenblock.wildmod.world.feature.features;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.world.gen.UpwardsBranchingTrunkPlacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;

import java.util.*;
import java.util.function.BiConsumer;

public class WildTreeFeature extends Feature<WildTreeFeatureConfig> {
    private static final int FORCE_STATE_AND_NOTIFY_ALL = 19;

    public WildTreeFeature(Codec<WildTreeFeatureConfig> codec) {
        super(codec);
    }

    public static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
        return canReplace(world, pos) || world.testBlockState(pos, state -> state.isIn(BlockTags.LOGS));
    }

    private static boolean isVine(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(Blocks.VINE));
    }

    private static boolean isWater(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(Blocks.WATER));
    }

    public static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isAir() || state.isIn(BlockTags.LEAVES));
    }

    private static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> {
            Material material = state.getMaterial();
            return material == Material.REPLACEABLE_PLANT;
        });
    }

    private static void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, 19);
    }

    public static boolean canReplace(TestableWorld world, BlockPos pos) {
        return isAirOrLeaves(world, pos) || isReplaceablePlant(world, pos) || isWater(world, pos);
    }

    private boolean generate(
            StructureWorldAccess world,
            Random random,
            BlockPos pos,
            BiConsumer<BlockPos, BlockState> rootsReplacer,
            BiConsumer<BlockPos, BlockState> trunkReplacer,
            BiConsumer<BlockPos, BlockState> foliageReplacer,
            WildTreeFeatureConfig config
    ) {
        int trunkHeight = config.trunkPlacer.getHeight(random);
        int foliageHeight = config.foliagePlacer.getRandomHeight(random, trunkHeight, config);
        int trunkLength = trunkHeight - foliageHeight;
        int foliageRadius = config.foliagePlacer.getRandomRadius(random, trunkLength);
        BlockPos rootOffset = config.rootPlacer.map(rootPlacer -> rootPlacer.trunkOffset(pos, random)).orElse(pos);
        int minHeight = Math.min(pos.getY(), rootOffset.getY());
        int maxHeight = Math.max(pos.getY(), rootOffset.getY()) + trunkHeight + 1;
        if (minHeight >= world.getBottomY() + 1 && maxHeight <= world.getTopY()) {
            OptionalInt optionalInt = config.minimumSize.getMinClippedHeight();
            if (this.getTopPosition(world, trunkHeight, rootOffset, config) >= trunkHeight || optionalInt.isPresent() && this.getTopPosition(world, trunkHeight, rootOffset, config) >= optionalInt.getAsInt()) {
                if (config.rootPlacer.isPresent() && !(config.rootPlacer.get()).generate(world, rootsReplacer, random, pos, rootOffset, config)) {
                    return false;
                } else {
                    List<FoliagePlacer.TreeNode> list = config.trunkPlacer.generate(world, trunkReplacer, random, this.getTopPosition(world, trunkHeight, rootOffset, config), rootOffset, config);
                    list.forEach(
                            node -> config.foliagePlacer.generate(world, foliageReplacer, random, config, this.getTopPosition(world, trunkHeight, rootOffset, config), node, foliageHeight, foliageRadius)
                    );
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private int getTopPosition(TestableWorld world, int height, BlockPos pos, WildTreeFeatureConfig config) {
        Mutable mutable = new Mutable();

        for (int i = 0; i <= height + 1; ++i) {
            int j = config.minimumSize.getRadius(height, i);

            for (int k = -j; k <= j; ++k) {
                for (int l = -j; l <= j; ++l) {
                    boolean bl;
                    label: {
                        mutable.set(pos, k, i, l);
                        label2:
                        if (!TreeFeature.canReplace(world, pos) && !world.testBlockState(pos, state -> state.isIn(BlockTags.LOGS))) {
                            var var12 = config.trunkPlacer;
                            if (var12 instanceof UpwardsBranchingTrunkPlacer trunk && world.testBlockState(pos, state -> state.isIn(trunk.canGrowThrough))) {
                                break label2;
                            }

                            bl = false;
                            break label;
                        }

                        bl = true;
                    }

                    boolean isValid = bl;
                    if (!isValid || !config.ignoreVines && TreeFeature.isVine(world, mutable)) {
                        return i - 2;
                    }
                }
            }
        }

        return height;
    }

    public final boolean generate(FeatureContext<WildTreeFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        BlockPos blockPos = context.getOrigin();
        WildTreeFeatureConfig treeFeatureConfig = context.getConfig();
        HashSet<BlockPos> rootPos = Sets.newHashSet();
        HashSet<BlockPos> trunkPos = Sets.newHashSet();
        HashSet<BlockPos> foliagePos = Sets.newHashSet();
        HashSet<BlockPos> decoratorPos = Sets.newHashSet();
        BiConsumer<BlockPos, BlockState> rootReplacer = (pos, state) -> {
            rootPos.add(pos.toImmutable());
            world.setBlockState(pos, state, 19);
        };
        BiConsumer<BlockPos, BlockState> trunkReplacer = (pos, state) -> {
            trunkPos.add(pos.toImmutable());
            world.setBlockState(pos, state, 19);
        };
        BiConsumer<BlockPos, BlockState> foliageReplacer = (pos, state) -> {
            foliagePos.add(pos.toImmutable());
            world.setBlockState(pos, state, 19);
        };
        BiConsumer<BlockPos, BlockState> decoratorReplacer = (position, state) -> {
            decoratorPos.add(position.toImmutable());
            world.setBlockState(position, state, 19);
        };
        boolean generate = this.generate(world, random, blockPos, rootReplacer, trunkReplacer, foliageReplacer, treeFeatureConfig);
        if (generate && (!trunkPos.isEmpty() || !foliagePos.isEmpty())) {
            if (!treeFeatureConfig.decorators.isEmpty()) {
                ArrayList<BlockPos> rootPositions = Lists.newArrayList(rootPos);
                ArrayList<BlockPos> trunkPositions = Lists.newArrayList(trunkPos);
                ArrayList<BlockPos> foliagePositions = Lists.newArrayList(foliagePos);
                trunkPositions.sort(Comparator.comparingInt(Vec3i::getY));
                foliagePositions.sort(Comparator.comparingInt(Vec3i::getY));
                rootPositions.sort(Comparator.comparingInt(Vec3i::getY));
                treeFeatureConfig.decorators.forEach(treeDecorator -> treeDecorator.generate(world, decoratorReplacer, random, trunkPositions, foliagePositions));
            }

            return BlockBox.encompassPositions(Iterables.concat(trunkPos, foliagePos, decoratorPos)).map(box -> {
                VoxelSet voxelSet = placeLogsAndLeaves(world, box, trunkPos, decoratorPos);
                Structure.updateCorner(world, 3, voxelSet, box.getMinX(), box.getMinY(), box.getMinZ());
                return true;
            }).orElse(false);
        } else {
            return false;
        }
    }

    private static VoxelSet placeLogsAndLeaves(WorldAccess world, BlockBox box, Set<BlockPos> trunkPositions, Set<BlockPos> decorationPositions) {
        ArrayList<Set<BlockPos>> list = Lists.newArrayList();
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ());
        int i = 6;

        for (int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }

        Mutable mutable = new Mutable();

        for (BlockPos blockPos : Lists.newArrayList(decorationPositions)) {
            if (box.contains(blockPos)) {
                voxelSet.set(blockPos.getX() - box.getMinX(), blockPos.getY() - box.getMinY(), blockPos.getZ() - box.getMinZ());
            }
        }

        for (BlockPos blockPos : Lists.newArrayList(trunkPositions)) {
            if (box.contains(blockPos)) {
                voxelSet.set(blockPos.getX() - box.getMinX(), blockPos.getY() - box.getMinY(), blockPos.getZ() - box.getMinZ());
            }

            for (Direction direction : Direction.values()) {
                mutable.set(blockPos, direction);
                if (!trunkPositions.contains(mutable)) {
                    BlockState blockState = world.getBlockState(mutable);
                    if (blockState.contains(Properties.DISTANCE_1_7)) {
                        list.get(0).add(mutable.toImmutable());
                        setBlockStateWithoutUpdatingNeighbors(world, mutable, blockState.with(Properties.DISTANCE_1_7, 1));
                        if (box.contains(mutable)) {
                            voxelSet.set(mutable.getX() - box.getMinX(), mutable.getY() - box.getMinY(), mutable.getZ() - box.getMinZ());
                        }
                    }
                }
            }
        }

        for (int k = 1; k < 6; ++k) {
            Set<BlockPos> set = list.get(k - 1);
            Set<BlockPos> set2 = list.get(k);

            for (BlockPos blockPos2 : set) {
                if (box.contains(blockPos2)) {
                    voxelSet.set(blockPos2.getX() - box.getMinX(), blockPos2.getY() - box.getMinY(), blockPos2.getZ() - box.getMinZ());
                }

                for (Direction direction2 : Direction.values()) {
                    mutable.set(blockPos2, direction2);
                    if (!set.contains(mutable) && !set2.contains(mutable)) {
                        BlockState blockState2 = world.getBlockState(mutable);
                        if (blockState2.contains(Properties.DISTANCE_1_7)) {
                            int l = blockState2.get(Properties.DISTANCE_1_7);
                            if (l > k + 1) {
                                BlockState blockState3 = blockState2.with(Properties.DISTANCE_1_7, k + 1);
                                setBlockStateWithoutUpdatingNeighbors(world, mutable, blockState3);
                                if (box.contains(mutable)) {
                                    voxelSet.set(mutable.getX() - box.getMinX(), mutable.getY() - box.getMinY(), mutable.getZ() - box.getMinZ());
                                }

                                set2.add(mutable.toImmutable());
                            }
                        }
                    }
                }
            }
        }

        return voxelSet;
    }
}
