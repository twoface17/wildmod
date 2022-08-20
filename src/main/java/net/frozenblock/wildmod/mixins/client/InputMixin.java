package net.frozenblock.wildmod.mixins.client;

import net.frozenblock.wildmod.misc.WildInput;
import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Input.class)
public class InputMixin implements WildInput {

    @Override
    public void tick(boolean slowDown, float f) {
    }
}
