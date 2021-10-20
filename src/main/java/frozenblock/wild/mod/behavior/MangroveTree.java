package frozenblock.wild.mod.behavior;

import frozenblock.wild.mod.registry.MangroveWoods;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MangroveTree {

    // TREE CLASS

    public static void generateTree(BlockState state, ServerWorld world, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(world.getBlockState(new BlockPos(x, y + 1, z)).getMaterial() == Material.AIR) {
            generateGroundTree(state, world, pos);
        } else if(world.getBlockState(new BlockPos(x, y + 1, z)).getMaterial() == Material.WATER) {
            generateDrownedTree(state, world, pos);
        }
    }

    public static void generateGroundTree(BlockState state, ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, MangroveWoods.MANGROVE_LOG.getDefaultState());
    }

    public static void generateDrownedTree(BlockState state, ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, Blocks.DARK_PRISMARINE.getDefaultState());
    }
}
