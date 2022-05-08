package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.entity.AllayEntity;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.event.EntityGameEventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.ServerEntityHandler.class)
public class ServerEntityHandlerMixin {

    @Inject(method = "startTracking(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    public void startTracking(Entity entity, CallbackInfo ci) {
        if (entity instanceof WardenEntity warden) {
            warden.updateEventHandler(EntityGameEventHandler::onEntitySetPosCallback);
            // my brain cant understand this aaa
        } else if (entity instanceof AllayEntity allay) {
            allay.updateEventHandler(EntityGameEventHandler::onEntitySetPosCallback);
            // my brain cant understand this aaa
        }
    }

    @Inject(method = "stopTracking(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    public void stopTracking(Entity entity, CallbackInfo ci) {
        if (entity instanceof WardenEntity warden) {
            warden.updateEventHandler(EntityGameEventHandler::onEntityRemoval);
            // my brain cant understand this aaa
        } else if (entity instanceof AllayEntity allay) {
            allay.updateEventHandler(EntityGameEventHandler::onEntityRemoval);
            // my brain cant understand this aaa
        }
    }
}
