package frozenblock.wild.mod.worldgen;

import com.mojang.serialization.Codec;
import frozenblock.wild.mod.blocks.mangrove.MangrovePropagule;
import frozenblock.wild.mod.blocks.mangrove.MangroveRoots;
import frozenblock.wild.mod.registry.MangroveWoods;
import frozenblock.wild.mod.registry.RegisterWorldgen;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
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

            // Pick a value from 0 (inclusive) to 4 (exclusive) and if it's 0, continue
            // This is the chance for spawning the gold block
            // Offset the log position by the resulting side

            if (!world.testBlockState(logPosition.down(4), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState())) && !world.testBlockState(logPosition.down(4), Predicate.isEqual(Blocks.AIR.getDefaultState())) && world.testBlockState(logPosition.down(3), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState())) && world.testBlockState(logPosition.down(2), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState())) && world.testBlockState(logPosition.down(), Predicate.isEqual(MangroveWoods.MANGROVE_LOG.getDefaultState()))) {
                BlockPos targetPosition = logPosition.down().offset(Direction.Axis.X, (int) MathHelper.clamp(Math.random() * 10f, 1, 3));
                BlockPos currentPosition = targetPosition;
                while (world.testBlockState(currentPosition, Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.WATER.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                    placeRootBlock(currentPosition, world, replacer);
                    currentPosition = currentPosition.down();
                }
                BlockPos posXPos = currentPosition;
                targetPosition = logPosition.down().offset(Direction.Axis.X, (int) MathHelper.clamp(Math.random() * -10f, -1, -3));
                currentPosition = targetPosition;
                while (world.testBlockState(currentPosition, Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.WATER.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                    placeRootBlock(currentPosition, world, replacer);
                    currentPosition = currentPosition.down();
                }
                BlockPos posXNeg = currentPosition;
                targetPosition = logPosition.down().offset(Direction.Axis.Z, (int) MathHelper.clamp(Math.random() * 10f, 1, 3));
                currentPosition = targetPosition;
                while (world.testBlockState(currentPosition, Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.WATER.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                    placeRootBlock(currentPosition, world, replacer);
                    currentPosition = currentPosition.down();
                }
                BlockPos posZPos = currentPosition;
                targetPosition = logPosition.down().offset(Direction.Axis.Z, (int) MathHelper.clamp(Math.random() * -10f, -1, -3));
                currentPosition = targetPosition;
                while (world.testBlockState(currentPosition, Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.WATER.getDefaultState())) || world.testBlockState(currentPosition, Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                    placeRootBlock(currentPosition, world, replacer);
                    currentPosition = currentPosition.down();
                }
                BlockPos posZNeg = currentPosition;
                if (posXPos.getY() <= posXNeg.getY() && posXPos.getY() <= posZPos.getY() && posXPos.getY() <= posZNeg.getY()) {
                    targetPosition = posXPos.offset(Direction.Axis.X, (int) MathHelper.clamp(Math.random() * -10f, -1, -3));
                } else if (posXNeg.getY() <= posXPos.getY() && posXNeg.getY() <= posZPos.getY() && posXNeg.getY() <= posZNeg.getY()) {
                    targetPosition = posXPos.offset(Direction.Axis.X, (int) MathHelper.clamp(Math.random() * 10f, 1, 3));
                } else if (posZPos.getY() <= posXPos.getY() && posZPos.getY() <= posXNeg.getY() && posZPos.getY() <= posZNeg.getY()) {
                    targetPosition = posXPos.offset(Direction.Axis.Z, (int) MathHelper.clamp(Math.random() * -10f, -1, -3));
                } else if (posZNeg.getY() <= posXPos.getY() && posZNeg.getY() <= posXNeg.getY() && posZNeg.getY() <= posZPos.getY()) {
                    targetPosition = posXPos.offset(Direction.Axis.Z, (int) MathHelper.clamp(Math.random() * 10f, 1, 3));
                }
                targetPosition = targetPosition.up();
                placeRootBottom(targetPosition, world, replacer);
            }
        }
        for (BlockPos leavesPosition : leavesPositions) {

            // Pick a value from 0 (inclusive) to 4 (exclusive) and if it's 0, continue
            // This is the chance for spawning the gold block

            // Offset the log position by the resulting side

            if (world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.WATER.getDefaultState())) || world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                if (Math.random() > 0.85) {
                    if (world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.AIR.getDefaultState())) || world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.CAVE_AIR.getDefaultState()))) {
                        replacer.accept(leavesPosition.down(), MangroveWoods.MANGROVE_PROPAGULE.getDefaultState().with(MangrovePropagule.HANGING, true));
                    } else if (world.testBlockState(leavesPosition.down(), Predicate.isEqual(Blocks.WATER.getDefaultState()))) {
                        replacer.accept(leavesPosition.down(), MangroveWoods.MANGROVE_PROPAGULE.getDefaultState().with(MangrovePropagule.HANGING, true).with(MangrovePropagule.WATERLOGGED, true));
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
            replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState().with(MangroveRoots.WATERLOGGED, true));
        }
        if (world.testBlockState(currentPosition.up(), Predicate.isEqual(Blocks.LILY_PAD.getDefaultState()))) {
            replacer.accept(currentPosition.up(), Blocks.AIR.getDefaultState());
        }
    }

    private void placeRootBottom(BlockPos targetPosition, TestableWorld world, BiConsumer<BlockPos, BlockState> replacer) {
        BlockPos currentPosition = targetPosition;
        for (int i = 0; i < 6; i++) {
            placeRootBlock(currentPosition, world, replacer);
            int dir = (int) MathHelper.clamp(Math.random() * 10f, 0, 3);
            if (dir == 0) {
                currentPosition = currentPosition.offset(Direction.Axis.X, (int) MathHelper.clamp(Math.random() * 10f, 1, 3));
            } else if (dir == 1) {
                currentPosition = currentPosition.offset(Direction.Axis.X, (int) MathHelper.clamp(Math.random() * -10, -1, -3));
            } else if (dir == 2) {
                currentPosition = currentPosition.offset(Direction.Axis.Z, (int) MathHelper.clamp(Math.random() * 10, 1, 3));
            } else if (dir == 3) {
                currentPosition = currentPosition.offset(Direction.Axis.Z, (int) MathHelper.clamp(Math.random() * -10, -1, -3));
            }
            if (!currentPosition.isWithinDistance(targetPosition, 3)) {
                currentPosition = targetPosition;
                i--;
            }
        }
    }
}



