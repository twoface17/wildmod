package net.frozenblock.wildmod.block.deepdark;

import net.frozenblock.wildmod.block.entity.SculkCatalystBlockEntity;
import net.frozenblock.wildmod.block.entity.SculkCatalystPhase;
import net.frozenblock.wildmod.block.entity.WildBlockEntityProvider;
import net.frozenblock.wildmod.block.entity.WildBlockWithEntity;
import net.frozenblock.wildmod.fromAccurateSculk.WildBlockEntityType;
import net.frozenblock.wildmod.fromAccurateSculk.WildProperties;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SculkCatalystBlock extends WildBlockWithEntity implements WildBlockEntityProvider {
    public final int getRange() {
        return 8;
    }
    private final IntProvider experience = ConstantIntProvider.create(20);
    public static final EnumProperty<SculkCatalystPhase> SCULK_CATALYST_PHASE = WildProperties.SCULK_CATALYST_PHASE;
    public static SculkCatalystPhase getPhase(BlockState blockState) {
        return blockState.get(SCULK_CATALYST_PHASE);
    }
    public static boolean isInactive(BlockState blockState) {
        return SculkCatalystBlock.getPhase(blockState) == SculkCatalystPhase.INACTIVE;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
        if (blockEntity instanceof SculkCatalystBlockEntity) {
            return ((SculkCatalystBlockEntity)blockEntity).getEventListener();
        }
        return null;
    }
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world2, BlockState blockState2, BlockEntityType<T> blockEntityType) {
        if (!world2.isClient) {
            return SculkCatalystBlock.checkType(blockEntityType, WildBlockEntityType.SCULK_CATALYST, (world, blockPos, blockState, sculkCatalystBlockEntity) -> sculkCatalystBlockEntity.getEventListener().tick(world));
        }
        return null;
    }
    @Override
    public void onStateReplaced(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.isOf(blockState2.getBlock())) {
            return;
        }
        if (SculkCatalystBlock.getPhase(blockState) == SculkCatalystPhase.ACTIVE) {
            SculkCatalystBlock.updateNeighbors(world, blockPos);
        }
        super.onStateReplaced(blockState, world, blockPos, blockState2, bl);
    }
    @Override
    public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (SculkCatalystBlock.getPhase(blockState) != SculkCatalystPhase.ACTIVE) {
            if (SculkCatalystBlock.getPhase(blockState) == SculkCatalystPhase.COOLDOWN) {
                serverWorld.setBlockState(blockPos, blockState.with(SCULK_CATALYST_PHASE, SculkCatalystPhase.INACTIVE), 3);
            }
            return;
        }
        SculkCatalystBlock.setCooldown(serverWorld, blockPos, blockState);
    }
    public static void setCooldown(World world, BlockPos blockPos, BlockState blockState) {
        world.setBlockState(blockPos, blockState.with(SCULK_CATALYST_PHASE, SculkCatalystPhase.COOLDOWN), 3);
        world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 1);
    }
    private static void updateNeighbors(World world, BlockPos blockPos) {
        world.updateNeighborsAlways(blockPos, RegisterBlocks.SCULK_CATALYST);
        world.updateNeighborsAlways(blockPos.offset(Direction.UP.getOpposite()), RegisterBlocks.SCULK_CATALYST);
    }

    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        this.dropExperienceWhenMined(world, pos, stack, this.experience);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    public static void setActive(World world, BlockPos blockPos, BlockState blockState) {
        world.createAndScheduleBlockTick(new BlockPos(blockPos), blockState.getBlock(), 11 );
        world.setBlockState(blockPos, blockState.with(SCULK_CATALYST_PHASE, SculkCatalystPhase.ACTIVE));
    }

    @Override
    public boolean hasSidedTransparency(BlockState blockState) {
        return false;
    }
    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SculkCatalystBlockEntity(blockPos, blockState);
    }

    //Block States
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SCULK_CATALYST_PHASE);
    }
    public SculkCatalystBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SCULK_CATALYST_PHASE, SculkCatalystPhase.INACTIVE));
    }

    protected void dropExperienceWhenMined(ServerWorld world, BlockPos pos, ItemStack tool, IntProvider experience) {
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
            int i = experience.get(world.random);
            if (i > 0) {
                this.dropExperience(world, pos, i);
            }
        }

    }
}
