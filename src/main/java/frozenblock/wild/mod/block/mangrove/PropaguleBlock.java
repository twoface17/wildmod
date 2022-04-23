package frozenblock.wild.mod.block.mangrove;


import frozenblock.wild.mod.liukrastapi.MangroveSaplingGenerator;
import frozenblock.wild.mod.registry.MangroveWoods;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PropaguleBlock extends SaplingBlock implements Waterloggable, Fertilizable {
    public static final IntProperty AGE;
    public static final int MAX_AGE = 4;
    private static final VoxelShape[] SHAPE_PER_AGE;
    public static final BooleanProperty HANGING = BooleanProperty.of("hanging");
    public static final BooleanProperty WATERLOGGED = BooleanProperty.of("waterlogged");


    public PropaguleBlock(Settings settings) {
        super(new MangroveSaplingGenerator(), settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(WATERLOGGED, false)
                .with(HANGING, false)
                .with(AGE, 0)
                .with(STAGE, 0)
        );
    }

    @Override
    protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
        return state.isIn(BlockTags.DIRT) || state.isOf(Blocks.FARMLAND) || state.isOf(Blocks.CLAY) || state.isOf(RegisterBlocks.MUD);
    }

    protected static Direction attachedDirection(BlockState state) {
        return state.get(HANGING) ? Direction.DOWN : Direction.UP;
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return (BlockState)((BlockState)super.getPlacementState(ctx).with(WATERLOGGED, bl)).with(AGE, 4);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGE, HANGING, WATERLOGGED, AGE);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return isHanging(state) ? world.getBlockState(pos.up()).isOf(MangroveWoods.MANGROVE_LEAVES) : super.canPlaceAt(state, world, pos);
    }

    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return attachedDirection(state).getOpposite() == direction && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean) state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    private static boolean isHanging(BlockState state) {
        return (Boolean)state.get(HANGING);
    }

    private static boolean isFullyGrown(BlockState state) {
        return (Integer)state.get(AGE) == 4;
    }

    public static BlockState createNewHangingPropagule() {
        return (BlockState)((BlockState)MangroveWoods.MANGROVE_PROPAGULE.getDefaultState().with(HANGING, true)).with(AGE, 0);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d offset = state.getModelOffset(world, pos);
        VoxelShape voxelShape;
        if (!state.get(HANGING)) {
            voxelShape = SHAPE_PER_AGE[4];
        } else {
            voxelShape = SHAPE_PER_AGE[(Integer)state.get(AGE)];
        }

        return voxelShape.offset(offset.x, offset.y, offset.z);
    }

    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!isHanging(state)) {
            if (random.nextInt(7) == 0) {
                this.generate(world, pos, state, random);
            }

        } else {
            if (!isFullyGrown(state)) {
                world.setBlockState(pos, (BlockState)state.cycle(AGE), 2);
            }

        }
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return !isHanging(state) || !isFullyGrown(state);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return isHanging(state) ? !isFullyGrown(state) : super.canGrow(world, random, pos, state);
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.generate(world, pos, state, random);
    }

    public void generate(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        if (isHanging(state) && !isFullyGrown(state)) {
            world.setBlockState(pos, (BlockState)state.cycle(AGE), 2);
        } else {
            super.generate(world, pos, state, random);
        }
    }

    static {
        AGE = IntProperty.of("age", 0, 4);
        SHAPE_PER_AGE = new VoxelShape[]{Block.createCuboidShape(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.createCuboidShape(7.0D, 10.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.createCuboidShape(7.0D, 7.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.createCuboidShape(7.0D, 3.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.createCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
    }
}
