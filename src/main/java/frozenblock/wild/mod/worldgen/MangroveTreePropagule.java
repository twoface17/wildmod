package frozenblock.wild.mod.worldgen;

import com.mojang.serialization.Codec;
import frozenblock.wild.mod.blocks.mangrove.MangrovePropagule;
import frozenblock.wild.mod.blocks.mangrove.MangroveRoots;
import frozenblock.wild.mod.registry.MangroveWoods;
import frozenblock.wild.mod.registry.RegisterWorldgen;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class MangroveTreePropagule extends TreeDecorator {
    public static final MangroveTreePropagule INSTANCE = new MangroveTreePropagule();
    // Our constructor doesn't have any arguments, so we create a unit codec that returns the singleton instance
    public static final Codec<MangroveTreePropagule> CODEC = Codec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> getType() {
        return RegisterWorldgen.MANGROVE_TREE_PROPAGULE;
    }

    public void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions) {
        // Iterate through block positions
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
        }
    }
}



