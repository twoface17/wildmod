package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.misc.WildBoatType;
import net.frozenblock.wildmod.registry.RegisterItems;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public class BoatDropsMixin {
    @Inject(method = "asItem", at = @At("HEAD"), cancellable = true)
    public void asItem(CallbackInfoReturnable<Item> ci) {
        if (((BoatEntity) (Object) this).getBoatType() == WildBoatType.MANGROVE) {
            ci.setReturnValue(RegisterItems.MANGROVE_BOAT);
        }
    }
}