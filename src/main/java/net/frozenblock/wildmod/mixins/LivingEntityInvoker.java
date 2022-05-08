package net.frozenblock.wildmod.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityInvoker {

    @Invoker("getXpToDrop")
    static int callGetXpToDrop(PlayerEntity playerEntity) {
        throw new IllegalStateException();
    }
}
