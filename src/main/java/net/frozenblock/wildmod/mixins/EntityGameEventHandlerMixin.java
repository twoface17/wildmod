package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.misc.WildEventHandler;
import net.frozenblock.wildmod.misc.WildUtils;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityGameEventHandler.class)
public class EntityGameEventHandlerMixin<T extends GameEventListener> implements WildEventHandler<T> {

    @Mutable
    @Final
    @Shadow
    private T listener;

    @Shadow private @Nullable ChunkSectionPos sectionPos;

    @Override
    public void setListener(T listener, @Nullable World world) {
        T gameEventListener = this.listener;
        if (gameEventListener != listener) {
            if (world instanceof ServerWorld serverWorld) {
                WildUtils.updateDispatcher(serverWorld, this.sectionPos, dispatcher -> dispatcher.removeListener(gameEventListener));
                WildUtils.updateDispatcher(serverWorld, this.sectionPos, dispatcher -> dispatcher.addListener(listener));
            }

            this.listener = listener;
        }
    }
}
