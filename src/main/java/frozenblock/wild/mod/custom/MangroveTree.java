package frozenblock.wild.mod.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MangroveTree {

    public static void generateTree(BlockState state, ServerWorld world, BlockPos pos) {

        // THIS CLASS CAN BE USED TO GENERATE TREES: You can add 'MangroveTree.generateTree(state, world, pos, random);' to generate a tree using this class
        // This class is used in MangroveSapling class and in next versions also in

        world.setBlockState(pos, Blocks.STONE.getDefaultState());
    }
}
