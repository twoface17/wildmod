package net.frozenblock.wildmod.world.gen;

/*import net.frozenblock.wildmod.registry.RegisterWorldgen;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class MangroveTreeDecorator extends TreeDecorator {
    public static final MangroveTreeDecorator INSTANCE = new MangroveTreeDecorator();
    // Our constructor doesn't have any arguments, so we create a unit codec that returns the singleton instance
    public static final Codec<MangroveTreeDecorator> CODEC = Codec.unit(() -> INSTANCE);

    private static void placeVines(TestableWorld world, BlockPos pos, BooleanProperty facing, BiConsumer<BlockPos, BlockState> replacer) {
        placeVine(replacer, pos, facing);
        int i = 4;

        for (pos = pos.down(); Feature.isAir(world, pos) && i > 0; --i) {
            placeVine(replacer, pos, facing);
            pos = pos.down();
        }

    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return RegisterWorldgen.MANGROVE_TREE_DECORATOR;
    }

    public void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions) {
        // Iterate through block positions
        for (BlockPos logPosition : logPositions) {
            if (!world.testBlockState(logPosition.down(4), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState())) && !world.testBlockState(logPosition.down(4), Predicate.isEqual(Blocks.AIR.getDefaultState())) && world.testBlockState(logPosition.down(3), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState())) && world.testBlockState(logPosition.down(2), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState())) && world.testBlockState(logPosition.down(), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState()))) {
                //Positive X
                int PosXLength = random.nextInt(3) + 1;
                BlockPos targetPosition = logPosition.down().offset(Direction.Axis.X, PosXLength);
                BlockPos currentPosition = targetPosition;
                while (world.testBlockState(currentPosition, Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.WATER.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                    placeRootBlock(currentPosition, world, replacer);
                    currentPosition = currentPosition.down();
                }
                currentPosition.up();
                placeRootBottom(currentPosition, world, replacer);
                for (int i=1; i<=PosXLength-1; i++) {
                    placeRootBlock(logPosition.offset(Direction.Axis.X, i), world, replacer);
                }
                //Negative X
                int NegXLength = random.nextInt(4);
                targetPosition = logPosition.down().offset(Direction.Axis.X, -NegXLength);
                currentPosition = targetPosition;
                while (world.testBlockState(currentPosition, Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.WATER.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                    placeRootBlock(currentPosition, world, replacer);
                    currentPosition = currentPosition.down();
                }
                currentPosition.up();
                placeRootBottom(currentPosition, world, replacer);
                for (int i=1; i<=NegXLength-1; i++) {
                    placeRootBlock(logPosition.offset(Direction.Axis.X, -i), world, replacer);
                }
                //Positive Z
                int PosZLength = random.nextInt(4);
                targetPosition = logPosition.down().offset(Direction.Axis.Z, PosZLength);
                currentPosition = targetPosition;
                while (world.testBlockState(currentPosition, Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.WATER.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                    placeRootBlock(currentPosition, world, replacer);
                    currentPosition = currentPosition.down();
                }
                currentPosition.up();
                placeRootBottom(currentPosition, world, replacer);
                for (int i=1; i<=PosZLength-1; i++) {
                    placeRootBlock(logPosition.offset(Direction.Axis.Z, i), world, replacer);
                }
                //Negative Z
                int NegZLength = random.nextInt(4);
                targetPosition = logPosition.down().offset(Direction.Axis.Z, -NegZLength);
                currentPosition = targetPosition;
                while (world.testBlockState(currentPosition, Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.WATER.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                    placeRootBlock(currentPosition, world, replacer);
                    currentPosition = currentPosition.down();
                }
                currentPosition.up();
                placeRootBottom(currentPosition, world, replacer);
                for (int i=1; i<=NegZLength-1; i++) {
                    placeRootBlock(logPosition.offset(Direction.Axis.Z, -i), world, replacer);
                }
                if (world.testBlockState(logPosition.down(), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState()))) {
                    replacer.accept(logPosition.down(), Blocks.AIR.getDefaultState());
                }
                if (world.testBlockState(logPosition.down(2), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState()))) {
                    replacer.accept(logPosition.down(2), Blocks.AIR.getDefaultState());
                }
                if (world.testBlockState(logPosition.down(3), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState()))) {
                    replacer.accept(logPosition.down(3), Blocks.AIR.getDefaultState());
                }
            }
        }
        for (BlockPos leavesPosition : leavesPositions) {
            if (world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.WATER.getDefaultState())) || world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                if (Math.random() > 0.85) {
                    Random r = new Random();
                    int low = 0;
                    int high = 4;
                    int result = r.nextInt(high-low) + low;
                    if (world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                        replacer.accept(leavesPosition.down(), MangroveWoods.MANGROVE_PROPAGULE.getDefaultState().with(PropaguleBlock.HANGING, true).with(PropaguleBlock.AGE, result));
                    } else if (world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.WATER.getDefaultState()))) {
                        replacer.accept(leavesPosition.down(), MangroveWoods.MANGROVE_PROPAGULE.getDefaultState().with(PropaguleBlock.HANGING, true).with(PropaguleBlock.AGE, result).with(PropaguleBlock.WATERLOGGED, true));
                    }
                }
            }
            BlockPos blockPos4;
            if (random.nextInt(4) == 0) {
                blockPos4 = leavesPosition.west();
                if (Feature.isAir(world, blockPos4)) {
                    placeVines(world, blockPos4, VineBlock.EAST, replacer);
                }
            }

            if (random.nextInt(4) == 0) {
                blockPos4 = leavesPosition.east();
                if (Feature.isAir(world, blockPos4)) {
                    placeVines(world, blockPos4, VineBlock.WEST, replacer);
                }
            }

            if (random.nextInt(4) == 0) {
                blockPos4 = leavesPosition.north();
                if (Feature.isAir(world, blockPos4)) {
                    placeVines(world, blockPos4, VineBlock.SOUTH, replacer);
                }
            }

            if (random.nextInt(4) == 0) {
                blockPos4 = leavesPosition.south();
                if (Feature.isAir(world, blockPos4)) {
                    placeVines(world, blockPos4, VineBlock.NORTH, replacer);
                }
            }
        }
    }

    private void placeRootBlock(BlockPos currentPosition, TestableWorld world, BiConsumer<BlockPos, BlockState> replacer) {
        if (world.testBlockState(currentPosition, Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.DANDELION.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.GRASS.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.TALL_GRASS.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.FERN.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.BLUE_ORCHID.getDefaultState()))) {
            replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
        } else if (world.testBlockState(currentPosition, Predicate.isEqual(Blocks.WATER.getDefaultState()))) {
            replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState().with(MangroveRootsBlock.WATERLOGGED, true));
        }
        if (world.testBlockState(currentPosition.up(), Predicate.isEqual(Blocks.LILY_PAD.getDefaultState()))) {
            replacer.accept(currentPosition.up(), Blocks.AIR.getDefaultState());
        }
    }

    private void placeRootBottom(BlockPos targetPosition, TestableWorld world, BiConsumer<BlockPos, BlockState> replacer) {
        BlockPos currentPosition = targetPosition;
        for (int i = 0; i < 6; i++) {
            placeRootBlock(currentPosition, world, replacer);
            int dir = (int) MathHelper.clamp(Math.random() * 10f, 0, 5);
            if (dir == 0) {
                currentPosition = currentPosition.offset(Direction.Axis.X, 1);
            } else if (dir == 1) {
                currentPosition = currentPosition.offset(Direction.Axis.X, 1);
            } else if (dir == 2) {
                currentPosition = currentPosition.offset(Direction.Axis.Z, 1);
            } else if (dir == 3) {
                currentPosition = currentPosition.offset(Direction.Axis.Z,1);
            } else if (dir == 4) {
                currentPosition = currentPosition.down();
            } else if (dir == 5) {
                currentPosition = currentPosition.up();
            }
            if (!currentPosition.isWithinDistance(targetPosition, 2)) {
                currentPosition = targetPosition;
                i--;
            }
        }
    }
}
*/