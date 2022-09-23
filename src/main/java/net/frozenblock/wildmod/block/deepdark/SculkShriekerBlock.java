/*
 * Decompiled with CFR 0.0.9 (FabricMC cc05e23f).
 */
package net.frozenblock.wildmod.block.deepdark;

import net.frozenblock.wildmod.block.entity.SculkShriekerBlockEntity;
import net.frozenblock.wildmod.block.entity.WildBlockWithEntity;
import net.frozenblock.wildmod.fromAccurateSculk.RegisterBlockEntities;
import net.frozenblock.wildmod.fromAccurateSculk.WildProperties;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SculkShriekerBlock extends WildBlockWithEntity implements Waterloggable {
    public static final BooleanProperty SHRIEKING = WildProperties.SHRIEKING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty CAN_SUMMON = WildProperties.CAN_SUMMON;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    public static final double TOP = SHAPE.getMax(Direction.Axis.Y);

    public SculkShriekerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager
                        .getDefaultState()
                        .with(SHRIEKING, Boolean.valueOf(false))
                        .with(WATERLOGGED, Boolean.valueOf(false))
                        .with(CAN_SUMMON, Boolean.valueOf(false))
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHRIEKING);
        builder.add(WATERLOGGED);
        builder.add(CAN_SUMMON);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world instanceof ServerWorld serverWorld) {
            ServerPlayerEntity serverPlayerEntity = SculkShriekerBlockEntity.findResponsiblePlayerFromEntity(entity);
            if (serverPlayerEntity != null) {
                serverWorld.getBlockEntity(pos, RegisterBlockEntities.SCULK_SHRIEKER).ifPresent(blockEntity -> blockEntity.shriek(serverWorld, serverPlayerEntity));
            }
        }

        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (world instanceof ServerWorld serverWorld && state.get(SHRIEKING) && !state.isOf(newState.getBlock())) {
            serverWorld.getBlockEntity(pos, RegisterBlockEntities.SCULK_SHRIEKER).ifPresent(blockEntity -> blockEntity.warn(serverWorld));
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(SHRIEKING)) {
            world.setBlockState(pos, state.with(SHRIEKING, Boolean.valueOf(false)), Block.NOTIFY_ALL);
            world.getBlockEntity(pos, RegisterBlockEntities.SCULK_SHRIEKER).ifPresent(blockEntity -> blockEntity.warn(world));
        }

    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SculkShriekerBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public void onStacksDropped(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, ItemStack itemStack) {
        int i;
        super.onStacksDropped(blockState, serverWorld, blockPos, itemStack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            i = 1;
            this.dropExperience(serverWorld, blockPos, i);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
        return blockEntity instanceof SculkShriekerBlockEntity sculkShriekerBlockEntity ? sculkShriekerBlockEntity.getVibrationListener() : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient
                ? BlockWithEntity.checkType(type, RegisterBlockEntities.SCULK_SHRIEKER, (worldx, pos, statex, blockEntity) -> blockEntity.getVibrationListener().tick(worldx))
                : null;
    }
}
