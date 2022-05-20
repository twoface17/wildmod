package net.frozenblock.wildmod.block.entity;

import net.frozenblock.wildmod.event.GameEventListener;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface WildBlockEntityProvider extends BlockEntityProvider {
    @Nullable
    BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Nullable
    default <T extends BlockEntity> GameEventListener getWildGameEventListener(ServerWorld world, T blockEntity) {
        return null;
    }
}
