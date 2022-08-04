package net.frozenblock.wildmod.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.misc.WildInput;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin implements WildInput {


    @Shadow
    @Final
    private GameOptions settings;


    @Shadow
    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }

    private float swiftSneakFactor = 1.0F;

    @Override
    public void tick(boolean slowDown, float swiftSneakFactor) {
        this.swiftSneakFactor = swiftSneakFactor;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(boolean slowDown, CallbackInfo ci) {
        KeyboardInput key = KeyboardInput.class.cast(this);
        if (slowDown) {
            key.movementSideways *= this.swiftSneakFactor;
            key.movementForward *= this.swiftSneakFactor;
        }
    }
}
