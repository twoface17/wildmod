package net.frozenblock.wildmod.mixins;

import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SignType.class)
public interface SignTypeInvoker {

    @Invoker
    static SignType callRegister(SignType type) {
        throw new IllegalStateException();
    }
}
