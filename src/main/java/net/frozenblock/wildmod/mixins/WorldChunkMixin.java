package net.frozenblock.wildmod.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.frozenblock.wildmod.block.entity.WildBlockEntityProvider;
import net.frozenblock.wildmod.event.GameEventDispatcher;
import net.frozenblock.wildmod.event.GameEventListener;
import net.frozenblock.wildmod.liukrastapi.ChunkSectionPos;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {

    @Shadow @Final private World world;

    @Shadow public abstract net.minecraft.world.event.listener.GameEventDispatcher getGameEventDispatcher(int ySectionCoord);

    @Shadow @Final private Int2ObjectMap<net.minecraft.world.event.listener.GameEventDispatcher> gameEventDispatchers;

    @Inject(method = "removeGameEventListener", at = @At("TAIL"))
    private <T extends BlockEntity> void removeGameEventListener(T blockEntity, CallbackInfo ci) {
        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof WildBlockEntityProvider) {
            World world = blockEntity.getWorld();
            if (world instanceof ServerWorld serverWorld) {
                GameEventListener gameEventListener = ((WildBlockEntityProvider) block).getWildGameEventListener(serverWorld, blockEntity);
                if (gameEventListener != null) {
                    int i = ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY());
                    GameEventDispatcher gameEventDispatcher = (GameEventDispatcher) this.getGameEventDispatcher(i);
                    gameEventDispatcher.removeListener(gameEventListener);
                    if (gameEventDispatcher.isEmpty()) {
                        this.gameEventDispatchers.remove(i);
                    }
                }
            }
        }
    }

    @Inject(method = "updateGameEventListener", at = @At("TAIL"))
    private <T extends BlockEntity> void updateGameEventListener(T blockEntity, CallbackInfo ci) {
        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof WildBlockEntityProvider) {
            World world = blockEntity.getWorld();
            if (world instanceof ServerWorld serverWorld) {
                GameEventListener gameEventListener = ((WildBlockEntityProvider) block).getWildGameEventListener(serverWorld, blockEntity);
                if (gameEventListener != null) {
                    GameEventDispatcher gameEventDispatcher = (GameEventDispatcher) this.getGameEventDispatcher(ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY()));
                    gameEventDispatcher.addListener(gameEventListener);
                }
            }
        }
    }
}
