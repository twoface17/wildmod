package net.frozenblock.wildmod.block.mangrove;

import net.frozenblock.wildmod.registry.MangroveWoods;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Iterator;
import java.util.Random;

public class MangroveLeaves extends LeavesBlock implements Fertilizable {
    public static final int GROWTH_CHANCE = 5;
    public MangroveLeaves() {
        super(FabricBlockSettings
                .of(Material.LEAVES)
                .sounds(BlockSoundGroup.AZALEA_LEAVES)
                .nonOpaque()
                .strength(0.2f)
        );
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (random.nextInt(5) == 0 && !(Boolean)state.get(PERSISTENT)) {
            BlockPos blockPos2 = pos.down();
            if (world.getBlockState(blockPos2).isAir() && world.getBlockState(blockPos2.down()).isAir() && !isTooCloseToAnotherPropagule(world, blockPos2)) {
                world.setBlockState(blockPos2, PropaguleBlock.createNewHangingPropagule());
            }

        }
    }

    private static boolean isTooCloseToAnotherPropagule(WorldAccess worldAccess, BlockPos pos) {
        Iterable<BlockPos> iterable = BlockPos.iterate(pos.up().north().east(), pos.down().south().west());
        Iterator var3 = iterable.iterator();

        BlockPos blockPos2;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            blockPos2 = (BlockPos)var3.next();
        } while(!worldAccess.getBlockState(blockPos2).isOf(MangroveWoods.MANGROVE_PROPAGULE));

        return true;
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return world.getBlockState(pos.down()).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos.down(), PropaguleBlock.createNewHangingPropagule(), 2);
    }
}

