package net.frozenblock.wildmod.block.swamp;


import net.frozenblock.wildmod.fromAccurateSculk.WildProperties;
import net.frozenblock.wildmod.liukrastapi.MangroveSaplingGenerator;
import net.frozenblock.wildmod.registry.MangroveWoods;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

public class PropaguleBlock extends SaplingBlock implements Waterloggable, Fertilizable {
    public static final IntProperty AGE = WildProperties.AGE_4;
    public static final int field_37589 = 4;
    private static final VoxelShape[] SHAPES = new VoxelShape[]{Block.createCuboidShape(7.0, 13.0, 7.0, 9.0, 16.0, 9.0), Block.createCuboidShape(7.0, 10.0, 7.0, 9.0, 16.0, 9.0), Block.createCuboidShape(7.0, 7.0, 7.0, 9.0, 16.0, 9.0), Block.createCuboidShape(7.0, 3.0, 7.0, 9.0, 16.0, 9.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)};
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty HANGING = Properties.HANGING;
    private static final float field_38749 = 0.85F;

    public PropaguleBlock(AbstractBlock.Settings settings) {
        super(new MangroveSaplingGenerator(0.85F), settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(STAGE, 0).with(AGE, 0).with(WATERLOGGED, false).with(HANGING, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGE).add(AGE).add(WATERLOGGED).add(HANGING);
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos) || floor.isOf(Blocks.CLAY);
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return Objects.requireNonNull(super.getPlacementState(ctx)).with(WATERLOGGED, bl).with(AGE, 4);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d vec3d = state.getModelOffset(world, pos);
        VoxelShape voxelShape;
        if (!(Boolean) state.get(HANGING)) {
            voxelShape = SHAPES[4];
        } else {
            voxelShape = SHAPES[state.get(AGE)];
        }

        return voxelShape.offset(vec3d.x, vec3d.y, vec3d.z);
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return isHanging(state) ? world.getBlockState(pos.up()).isOf(MangroveWoods.MANGROVE_LEAVES) : super.canPlaceAt(state, world, pos);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return direction == Direction.UP && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!isHanging(state)) {
            if (random.nextInt(7) == 0) {
                this.generate(world, pos, state, random);
            }

        } else {
            if (!isFullyGrown(state)) {
                world.setBlockState(pos, state.cycle(AGE), Block.NOTIFY_LISTENERS);
            }

        }
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return !isHanging(state) || !isFullyGrown(state);
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return isHanging(state) ? !isFullyGrown(state) : super.canGrow(world, random, pos, state);
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if (isHanging(state) && !isFullyGrown(state)) {
            world.setBlockState(pos, state.cycle(AGE), Block.NOTIFY_LISTENERS);
        } else {
            super.grow(world, random, pos, state);
        }

    }

    private static boolean isHanging(BlockState state) {
        return state.get(HANGING);
    }

    private static boolean isFullyGrown(BlockState state) {
        return state.get(AGE) == 4;
    }

    public static BlockState getDefaultHangingState() {
        return getHangingState(0);
    }

    public static BlockState getHangingState(int age) {
        return MangroveWoods.MANGROVE_PROPAGULE.getDefaultState().with(HANGING, true).with(AGE, age);
    }
}
