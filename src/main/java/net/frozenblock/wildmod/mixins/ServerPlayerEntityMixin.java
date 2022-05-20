package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.liukrastapi.WildPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin /*implements WildServerPlayerEntity */{
    //public SculkShriekerWarningManager sculkShriekerWarningManager = new SculkShriekerWarningManager(0, 0, 0);

    //@Override
    //public SculkShriekerWarningManager getSculkShriekerWarningManager() {
        //return this.sculkShriekerWarningManager;
    //}

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = ServerPlayerEntity.class.cast(this);
        ((WildPlayerEntity)player).setLastDeathPos(Optional.of(GlobalPos.create(player.world.getRegistryKey(), player.getBlockPos())));
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntity player = ServerPlayerEntity.class.cast(this);
        ((WildPlayerEntity)player).setLastDeathPos(((WildPlayerEntity)oldPlayer).getLastDeathPos());
    }
}
