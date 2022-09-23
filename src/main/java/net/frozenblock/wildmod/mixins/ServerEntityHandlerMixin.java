package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.entity.WildHostileEntity;
import net.frozenblock.wildmod.entity.WildPathAwareEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.ServerEntityHandler.class)
public final class ServerEntityHandlerMixin {

    @Inject(method = "startTracking(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    public void startTracking(Entity entity, CallbackInfo ci) {
        if (entity instanceof WildHostileEntity wildHostileEntity) {
            //wildHostileEntity.updateEventHandler(WildEntityGameEventHandler::onEntitySetPosCallback);
            // my brain cant understand this aaa
        } else if (entity instanceof WildPathAwareEntity wildPathAwareEntity) {
            //wildPathAwareEntity.updateEventHandler(WildEntityGameEventHandler::onEntitySetPosCallback);
            // my brain cant understand this aaa
        }
    }

    @Inject(method = "stopTracking(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    public void stopTracking(Entity entity, CallbackInfo ci) {
        if (entity instanceof WildHostileEntity wildHostileEntity) {
            //wildHostileEntity.updateEventHandler(WildEntityGameEventHandler::onEntityRemoval);
            // my brain cant understand this aaa
        } else if (entity instanceof WildPathAwareEntity wildPathAwareEntity) {
            //wildPathAwareEntity.updateEventHandler(WildEntityGameEventHandler::onEntityRemoval);
            // my brain cant understand this aaa
        }
    }
}
