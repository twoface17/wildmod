package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.misc.RideableInventory;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onClientCommand", at = @At("HEAD"))
    private void onClientCommand(ClientCommandC2SPacket packet, CallbackInfo ci) {
        if (packet.getMode() == ClientCommandC2SPacket.Mode.OPEN_INVENTORY) {
            Entity entity = this.player.getVehicle();
            if (entity instanceof RideableInventory rideableInventory) {
                rideableInventory.openInventory(this.player);
            }
        }
    }
}
