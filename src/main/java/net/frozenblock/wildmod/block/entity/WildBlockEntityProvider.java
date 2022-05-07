package net.frozenblock.wildmod.block.entity;

import net.frozenblock.wildmod.event.GameEventListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface WildBlockEntityProvider {
    @Nullable
    BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Nullable
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    @Nullable
    default <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
        return null;
    }
}
