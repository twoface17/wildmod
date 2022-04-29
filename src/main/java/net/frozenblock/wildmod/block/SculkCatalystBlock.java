package net.frozenblock.wildmod.block;

import net.frozenblock.wildmod.fromAccurateSculk.WildBlockEntityType;
import net.frozenblock.wildmod.fromAccurateSculk.WildProperties;
import net.frozenblock.wildmod.fromAccurateSculk.SculkCatalystBlockEntity;
import net.frozenblock.wildmod.fromAccurateSculk.SculkCatalystPhase;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SculkCatalystBlock extends BlockWithEntity implements BlockEntityProvider {
    public final int range;
    public int getRange() {
        return this.range;
    }
    public static final EnumProperty<SculkCatalystPhase> SCULK_CATALYST_PHASE = WildProperties.SCULK_CATALYST_PHASE;
    public static SculkCatalystPhase getPhase(BlockState blockState) {
        return blockState.get(SCULK_CATALYST_PHASE);
    }
    public static boolean isInactive(BlockState blockState) {
        return SculkCatalystBlock.getPhase(blockState) == SculkCatalystPhase.INACTIVE;
    }
    @Override
    protected void dropExperience(ServerWorld serverWorld, BlockPos blockPos, int i) {
        if (serverWorld.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            ExperienceOrbEntity.spawn(serverWorld, Vec3d.ofCenter(blockPos), 20);
        }
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
        world.updateNeighborsAlways(blockPos, SculkCatalystBlock.SCULK_CATALYST_BLOCK);
        world.updateNeighborsAlways(blockPos.offset(Direction.UP.getOpposite()), SculkCatalystBlock.SCULK_CATALYST_BLOCK);
    }
    @Override
    public void onStacksDropped(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, ItemStack itemStack) {
        int i;
        super.onStacksDropped(blockState, serverWorld, blockPos, itemStack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            i=1;
            this.dropExperience(serverWorld, blockPos, i);
        }
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
    public SculkCatalystBlock(AbstractBlock.Settings settings, int i) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SCULK_CATALYST_PHASE, SculkCatalystPhase.INACTIVE));
        this.range = i;
    }

    public static final BlockSoundGroup CATALYSTSOUNDS = new BlockSoundGroup(1.0F, 1.0F,
            RegisterSounds.BLOCK_SCULK_CATALYST_BREAK,
            RegisterSounds.BLOCK_SCULK_CATALYST_STEP,
            RegisterSounds.BLOCK_SCULK_CATALYST_PLACE,
            RegisterSounds.BLOCK_SCULK_CATALYST_HIT,
            RegisterSounds.BLOCK_SCULK_CATALYST_FALL
    );

    public static final AbstractBlock.Settings SCULK_CATALYST_PROPERTIES = FabricBlockSettings
            .of(Material.SCULK)
            .sounds(CATALYSTSOUNDS)
            .luminance(state -> 6);
    public static final Block SCULK_CATALYST_BLOCK = new SculkCatalystBlock(SCULK_CATALYST_PROPERTIES
            .strength(3.0F, 3.0F), 8);

}
