package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.event.WildGameEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ChorusFruitItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChorusFruitItem.class)
public class ChorusFruitItemMixin extends Item {

    public ChorusFruitItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "finishUsing", at = @At("RETURN"))
    private void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient) {

            for(int i = 0; i < 16; ++i) {
                double g = user.getX() + (user.getRandom().nextDouble() - 0.5) * 16.0;
                double h = MathHelper.clamp(
                        user.getY() + (double)(user.getRandom().nextInt(16) - 8),
                        world.getBottomY(),
                        world.getBottomY() + ((ServerWorld)world).getLogicalHeight() - 1
                );
                double j = user.getZ() + (user.getRandom().nextDouble() - 0.5) * 16.0;

                if (user.teleport(g, h, j, true)) {
                    world.emitGameEvent(user, WildGameEvent.TELEPORT, user.getBlockPos());
                }
            }
        }
    }
}
