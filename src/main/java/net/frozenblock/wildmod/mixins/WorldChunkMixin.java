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

    @Shadow
    @Final
    World world;

    @Shadow
    public abstract net.minecraft.world.event.listener.GameEventDispatcher getGameEventDispatcher(int ySectionCoord);

    @Shadow
    @Final
    private Int2ObjectMap<net.minecraft.world.event.listener.GameEventDispatcher> gameEventDispatchers;

    @Inject(method = "removeGameEventListener", at = @At("TAIL"))
    private <T extends BlockEntity> void removeGameEventListener(T blockEntity, CallbackInfo ci) {
        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof WildBlockEntityProvider wildBlockEntityProvider) {
            World world = this.world;
            if (world instanceof ServerWorld serverWorld) {
                GameEventListener gameEventListener = wildBlockEntityProvider.getWildGameEventListener(serverWorld, blockEntity);
                int i = ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY());
                net.minecraft.world.event.listener.GameEventDispatcher gameEventDispatcher = this.getGameEventDispatcher(i);
                if (gameEventListener != null && gameEventDispatcher instanceof GameEventDispatcher gameEventDispatcher1) {
                    gameEventDispatcher1.removeListener(gameEventListener);
                    if (gameEventDispatcher1.isEmpty()) {
                        this.gameEventDispatchers.remove(i);
                    }
                }
            }
        }
    }

    @Inject(method = "updateGameEventListener", at = @At("TAIL"))
    private <T extends BlockEntity> void updateGameEventListener(T blockEntity, CallbackInfo ci) {
        Block block = blockEntity.getCachedState().getBlock();
        if (block instanceof WildBlockEntityProvider wildBlockEntityProvider) {
            World world = this.world;
            if (world instanceof ServerWorld serverWorld) {
                GameEventListener gameEventListener = wildBlockEntityProvider.getWildGameEventListener(serverWorld, blockEntity);
                net.minecraft.world.event.listener.GameEventDispatcher gameEventDispatcher = this.getGameEventDispatcher(ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY()));
                if (gameEventListener != null && gameEventDispatcher instanceof GameEventDispatcher gameEventDispatcher1) {
                    gameEventDispatcher1.addListener(gameEventListener);
                }
            }
        }
    }

}
