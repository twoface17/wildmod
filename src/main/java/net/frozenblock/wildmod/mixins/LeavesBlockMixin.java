package net.frozenblock.wildmod.mixins;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin extends Block implements Waterloggable {
    @Final
    @Shadow
    public static final IntProperty DISTANCE = Properties.DISTANCE_1_7;
    @Final
    @Shadow
    public static final BooleanProperty PERSISTENT = Properties.PERSISTENT;

    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public LeavesBlockMixin(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(DISTANCE, 7).with(PERSISTENT, false).with(WATERLOGGED, false));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void LeavesBlock(Settings settings, CallbackInfo ci) {
        this.setDefaultState(this.stateManager.getDefaultState().with(DISTANCE, 7).with(PERSISTENT, false).with(WATERLOGGED, false));
    }


    /**
     * @author FrozenBlock
     * @reason idk
     */
    @Overwrite
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, updateDistanceFromLogs(state, world, pos), Block.NOTIFY_ALL);
    }

    /**
     * @author FrozenBlock
     * @reason idk
     */
    @Overwrite
    private static BlockState updateDistanceFromLogs(BlockState state, WorldAccess world, BlockPos pos) {
        int i = 7;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Direction[] var5 = Direction.values();

        for (Direction direction : var5) {
            mutable.set(pos, direction);
            i = Math.min(i, getDistanceFromLog(world.getBlockState(mutable)) + 1);
            if (i == 1) {
                break;
            }
        }

        return state.with(DISTANCE, i);
    }

    /**
     * @author FrozenBlock
     * @reason idk
     */
    @Overwrite
    private static int getDistanceFromLog(BlockState state) {
        if (state.isIn(BlockTags.LOGS)) {
            return 0;
        } else {
            return state.getBlock() instanceof LeavesBlock ? state.get(DISTANCE) : 7;
        }
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("TAIL"), cancellable = true)
    public void getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        int i = getDistanceFromLog(neighborState) + 1;
        if (i != 1 || state.get(DISTANCE) != i) {
            world.createAndScheduleBlockTick(pos, this, 1);
        }

        cir.setReturnValue(state);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Shadow
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.hasRain(pos.up())) {
            if (random.nextInt(15) == 1) {
                BlockPos blockPos = pos.down();
                BlockState blockState = world.getBlockState(blockPos);
                if (!blockState.isOpaque() || !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
                    double d = (double) pos.getX() + random.nextDouble();
                    double e = (double) pos.getY() - 0.05;
                    double f = (double) pos.getZ() + random.nextDouble();
                    world.addParticle(ParticleTypes.DRIPPING_WATER, d, e, f, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(WATERLOGGED);
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    public void getPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockState blockState = this.getDefaultState().with(PERSISTENT, true).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        cir.setReturnValue(updateDistanceFromLogs(blockState, ctx.getWorld(), ctx.getBlockPos()));
    }
}
