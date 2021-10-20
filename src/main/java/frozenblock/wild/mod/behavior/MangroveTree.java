package frozenblock.wild.mod.behavior;

import frozenblock.wild.mod.blocks.mangrove.MangroveLog;
import frozenblock.wild.mod.registry.MangroveWoods;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public class MangroveTree {

    // TREE CLASS

    public static void generateTree(BlockState state, ServerWorld world, BlockPos pos) {
        if(world.getBlockState(new BlockPos(pos.up())).getMaterial() == Material.AIR) {
            generateGroundTree(state, world, pos);
        } else if(world.getBlockState(new BlockPos(pos.up())).getMaterial() == Material.WATER) {
            generateDrownedTree(state, world, pos);
        }
    }

    public static void generateGroundTree(BlockState state, ServerWorld world, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        world.setBlockState(pos, MangroveWoods.MANGROVE_LOG.getDefaultState());
        world.setBlockState(pos.up(), MangroveWoods.MANGROVE_LOG.getDefaultState());
        world.setBlockState(new BlockPos(x, y + 2, z), MangroveWoods.MANGROVE_LOG.getDefaultState());
        world.setBlockState(new BlockPos(x, y + 3, z), MangroveWoods.MANGROVE_LOG.getDefaultState());
        generateTop(state, world, new BlockPos(x, y + 4, z));
    }

    public static void generateDrownedTree(BlockState state, ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, Blocks.DARK_PRISMARINE.getDefaultState());
    }

    public static void generateTop(BlockState state, ServerWorld world, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(Math.random() > 0.5) {
            generateLeavesNow(state, world, pos);
        } else {
            world.setBlockState(pos, MangroveWoods.MANGROVE_LOG.getDefaultState());
            if(Math.random() > 0.3) {
                generateLeavesNow(state, world, pos.up());
            } else {
                world.setBlockState(pos.up(), MangroveWoods.MANGROVE_LOG.getDefaultState());
                if(Math.random() > 0.1) {
                    generateLeavesNow(state, world, new BlockPos(x, y + 2, z));
                } else {
                    world.setBlockState(new BlockPos(x, y + 2, z), MangroveWoods.MANGROVE_LOG.getDefaultState());
                    generateLeavesNow(state, world, new BlockPos(x, y + 3, z));
                }
            }
        }
    }

    public static void generateLeavesNow(BlockState state, ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, Blocks.DARK_PRISMARINE.getDefaultState());
    }
}
