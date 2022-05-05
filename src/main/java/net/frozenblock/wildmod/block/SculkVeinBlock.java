package net.frozenblock.wildmod.block;

import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class SculkVeinBlock extends GlowLichenBlock/*AbstractLichenBlock */implements /*SculkSpreadable, */Waterloggable {
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    //private final LichenGrower allGrowTypeGrower = new LichenGrower(new SculkVeinBlock.SculkVeinGrowChecker(LichenGrower.GROW_TYPES));
    //private final LichenGrower samePositionOnlyGrower = new LichenGrower(new SculkVeinBlock.SculkVeinGrowChecker(LichenGrower.GrowType.SAME_POSITION));

    public SculkVeinBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false).with(Properties.DOWN, true).with(Properties.UP, false));
    }

    /*public LichenGrower getGrower() {
        return allGrowTypeGrower;
    }

    public LichenGrower getSamePositionOnlyGrower() {
        return this.samePositionOnlyGrower;
    }

    public static boolean place(WorldAccess world, BlockPos pos, BlockState state, Collection<Direction> directions) {
        boolean bl = false;
        BlockState blockState = RegisterBlocks.SCULK_VEIN.getDefaultState();

        for(Direction direction : directions) {
            BlockPos blockPos = pos.offset(direction);
            if (canGrowOn(world, direction, blockPos, world.getBlockState(blockPos))) {
                blockState = (BlockState)blockState.with(getProperty(direction), true);
                bl = true;
            }
        }

        if (!bl) {
            return false;
        } else {
            if (!state.getFluidState().isEmpty()) {
                blockState = blockState.with(WATERLOGGED, true);
            }

            world.setBlockState(pos, blockState, 3);
            return true;
        }
    }

    public void spreadAtSamePosition(WorldAccess world, BlockState state, BlockPos pos, WildAbstractRandom random) {
        if (state.isOf(this)) {
            for(Direction direction : DIRECTIONS) {
                BooleanProperty booleanProperty = getProperty(direction);
                if (state.get(booleanProperty) && world.getBlockState(pos.offset(direction)).isOf(RegisterBlocks.SCULK)) {
                    state = state.with(booleanProperty, false);
                }
            }
        }
    }

    */public static ToIntFunction<BlockState> getLuminanceSupplier(int i) {
        return blockState -> AbstractLichenBlock.hasAnyDirection(blockState) ? i : 0;
    }
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        FluidState fluidState = itemPlacementContext.getWorld().getFluidState(blockPos);
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess worldAccess, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.get(WATERLOGGED)) {
            worldAccess.createAndScheduleFluidTick(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(worldAccess));
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, worldAccess, blockPos, blockPos2);
    }

    @Override
    public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
        return !itemPlacementContext.getStack().isOf(SCULK_VEIN.asItem()) || super.canReplace(blockState, itemPlacementContext);
    }


    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(blockState);
    }

    @Override
    public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return blockState.getFluidState().isEmpty();
    }

    public static final GlowLichenBlock SCULK_VEIN = new GlowLichenBlock(GlowLichenBlock.Settings.of(Material.SCULK).noCollision().strength(0.2F).sounds(new BlockSoundGroup(1.0F, 1.0F,
            RegisterSounds.BLOCK_SCULK_VEIN_BREAK,
            RegisterSounds.BLOCK_SCULK_STEP,
            RegisterSounds.BLOCK_SCULK_VEIN_PLACE,
            RegisterSounds.BLOCK_SCULK_HIT,
            RegisterSounds.BLOCK_SCULK_FALL
    )));
}

