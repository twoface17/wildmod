package net.frozenblock.wildmod.event;

import net.frozenblock.wildmod.liukrastapi.ChunkSectionPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EntityGameEventHandler<T extends GameEventListener> {
    private T listener;
    @Nullable
    private ChunkSectionPos sectionPos;

    public EntityGameEventHandler(T listener) {
        this.listener = listener;
    }

    public void onEntitySetPosCallback(ServerWorld world) {
        this.onEntitySetPos(world);
    }

    public void setListener(T listener, @Nullable World world) {
        T gameEventListener = this.listener;
        if (gameEventListener != listener) {
            if (world instanceof ServerWorld serverWorld) {
                updateDispatcher(serverWorld, this.sectionPos, (dispatcher) -> {
                    dispatcher.removeListener(gameEventListener);
                });
                updateDispatcher(serverWorld, this.sectionPos, (dispatcher) -> {
                    dispatcher.addListener(listener);
                });
            }

            this.listener = listener;
        }
    }

    public T getListener() {
        return this.listener;
    }

    public void onEntityRemoval(ServerWorld world) {
        updateDispatcher(world, this.sectionPos, (dispatcher) -> {
            dispatcher.removeListener(this.listener);
        });
    }

    public void onEntitySetPos(ServerWorld world) {
        this.listener.getPositionSource().getPos(world).map(ChunkSectionPos::from).ifPresent((sectionPos) -> {
            if (this.sectionPos == null || !this.sectionPos.equals(sectionPos)) {
                updateDispatcher(world, this.sectionPos, (dispatcher) -> {
                    dispatcher.removeListener(this.listener);
                });
                this.sectionPos = sectionPos;
                updateDispatcher(world, this.sectionPos, (dispatcher) -> {
                    dispatcher.addListener(this.listener);
                });
            }

        });
    }

    private static void updateDispatcher(WorldView world, @Nullable ChunkSectionPos sectionPos, Consumer<GameEventDispatcher> dispatcherConsumer) {
        if (sectionPos != null) {
            Chunk chunk = world.getChunk(sectionPos.getSectionX(), sectionPos.getSectionZ(), ChunkStatus.FULL, false);
            if (chunk != null) {
                dispatcherConsumer.accept((GameEventDispatcher) chunk.getGameEventDispatcher(sectionPos.getSectionY()));
            }

        }
    }
}
