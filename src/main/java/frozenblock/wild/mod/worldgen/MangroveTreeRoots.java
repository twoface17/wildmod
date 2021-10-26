package frozenblock.wild.mod.worldgen;

import com.mojang.serialization.Codec;
import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.registry.MangroveWoods;
import frozenblock.wild.mod.registry.RegisterWorldgen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/*public class MangroveTreeRoots extends TreeDecorator {
    public static final MangroveTreeRoots INSTANCE = new MangroveTreeRoots();
    // Our constructor doesn't have any arguments, so we create a unit codec that returns the singleton instance
    public static final Codec<MangroveTreeRoots> CODEC = Codec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> getType() {
        return RegisterWorldgen.MANGROVE_TREE_ROOTS;
    }

    public void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions) {
        // Iterate through block positions
        for (BlockPos logPosition : logPositions) {
            // Pick a value from 0 (inclusive) to 4 (exclusive) and if it's 0, continue
            // This is the chance for spawning the gold block

                // Offset the log position by the resulting side

            if (world.getBlockState(logPosition.down()) == MangroveWoods.MANGROVE_LOG.getDefaultState()) {
                BlockPos targetPosition = logPosition.offset(Direction.Axis.X, 1);
                BlockPos currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.X, -1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.Z, 1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.Z, -1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                // Place the gold block using the replacer BiConsumer
                // This is the standard way of placing blocks in TrunkPlacers, FoliagePlacers and TreeDecorators
            } else if (world.getBlockState(logPosition.down(2)) == MangroveWoods.MANGROVE_LOG.getDefaultState()) {
                BlockPos targetPosition = logPosition.offset(Direction.Axis.X, 1);
                BlockPos currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.X, -1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.Z, 1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.Z, -1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                // Place the gold block using the replacer BiConsumer
                // This is the standard way of placing blocks in TrunkPlacers, FoliagePlacers and TreeDecorators
            } else if (world.getBlockState(logPosition.down(3)) == MangroveWoods.MANGROVE_LOG.getDefaultState()) {
                BlockPos targetPosition = logPosition.offset(Direction.Axis.X, 1);
                BlockPos currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.X, -1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.Z, 1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.Z, -1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                // Place the gold block using the replacer BiConsumer
                // This is the standard way of placing blocks in TrunkPlacers, FoliagePlacers and TreeDecorators
            } else if (world.getBlockState(logPosition.down(4)) == MangroveWoods.MANGROVE_LOG.getDefaultState()) {
                BlockPos targetPosition = logPosition.offset(Direction.Axis.X, 1);
                BlockPos currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.X, -1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.Z, 1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                targetPosition = logPosition.offset(Direction.Axis.Z, -1);
                currentPosition = targetPosition;
                while (world.getBlockState(currentPosition) == Blocks.AIR.getDefaultState() || world.getBlockState(currentPosition) == Blocks.WATER.getDefaultState() || world.getBlockState(currentPosition) == Blocks.CAVE_AIR.getDefaultState()) {
                    replacer.accept(currentPosition, MangroveWoods.MANGROVE_ROOTS.getDefaultState());
                    currentPosition = currentPosition.down();
                }
                // Place the gold block using the replacer BiConsumer
                // This is the standard way of placing blocks in TrunkPlacers, FoliagePlacers and TreeDecorators
            }
        }
    }

}
*/

