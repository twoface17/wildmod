package net.frozenblock.wildmod.block;

import com.google.common.annotations.VisibleForTesting;
import net.frozenblock.wildmod.entity.TadpoleEntity;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.frozenblock.wildmod.world.gen.random.WildAbstractRandom;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.random.AbstractRandom;

import java.util.Random;

public class FrogspawnBlock extends Block {
    private static final int MIN_TADPOLES = 2;
    private static final int MAX_TADPOLES = 5;
    private static final int MIN_HATCH_TIME = 3600;
    private static final int MAX_HATCH_TIME = 12000;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.5, 16.0);
    private static int minHatchTime = 3600;
    private static int maxHatchTime = 12000;

    public FrogspawnBlock(TransparentBlock.Settings settings) {
        super(settings);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return canLayAt(world, pos.down());
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.createAndScheduleBlockTick(pos, this, getHatchTime((WildAbstractRandom) world.getRandom()));
    }

    private static int getHatchTime(WildAbstractRandom random) {
        return random.nextBetweenExclusive(minHatchTime, maxHatchTime);
    }

    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        return !this.canPlaceAt(state, world, pos)
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.canPlaceAt(state, world, pos)) {
            this.breakWithoutDrop(world, pos);
        } else {
            this.hatch(world, pos, (WildAbstractRandom) random);
        }
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity.getType().equals(EntityType.FALLING_BLOCK)) {
            this.breakWithoutDrop(world, pos);
        }

    }

    private static boolean canLayAt(BlockView world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        FluidState fluidState2 = world.getFluidState(pos.up());
        return fluidState.getFluid() == Fluids.WATER && fluidState2.getFluid() == Fluids.EMPTY;
    }

    private void hatch(ServerWorld world, BlockPos pos, WildAbstractRandom random) {
        this.breakWithoutDrop(world, pos);
        world.playSound(null, pos, RegisterSounds.BLOCK_FROGSPAWN_HATCH, SoundCategory.BLOCKS, 1.0F, 1.0F);
        this.spawnTadpoles(world, pos, random);
    }

    private void breakWithoutDrop(World world, BlockPos pos) {
        world.breakBlock(pos, false);
    }

    private void spawnTadpoles(ServerWorld world, BlockPos pos, WildAbstractRandom random) {
        int i = random.nextBetweenExclusive(2, 6);

        for(int j = 1; j <= i; ++j) {
            TadpoleEntity tadpoleEntity = RegisterEntities.TADPOLE.create(world);
            double d = random.nextDouble();
            double e = random.nextDouble();
            double f = (double)pos.getX() + d;
            double g = (double)pos.getZ() + e;
            int k = random.nextBetweenExclusive(1, 361);
            tadpoleEntity.refreshPositionAndAngles(f, (double)pos.getY() - 0.5, g, (float)k, 0.0F);
            tadpoleEntity.setPersistent();
            world.spawnEntity(tadpoleEntity);
        }

    }

    @VisibleForTesting
    public static void setHatchTimeRange(int min, int max) {
        minHatchTime = min;
        maxHatchTime = max;
    }

    @VisibleForTesting
    public static void resetHatchTimeRange() {
        minHatchTime = 3600;
        maxHatchTime = 12000;
    }
}
