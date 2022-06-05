package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.liukrastapi.WildInput;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin implements WildInput {


    @Shadow @Final private GameOptions settings;


    @Shadow
    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }

    @Override
    public void tick(boolean slowDown, float swiftSneakFactor) {
        KeyboardInput key = KeyboardInput.class.cast(this);
        key.pressingForward = this.settings.forwardKey.isPressed();
        key.pressingBack = this.settings.backKey.isPressed();
        key.pressingLeft = this.settings.leftKey.isPressed();
        key.pressingRight = this.settings.rightKey.isPressed();
        key.movementForward = getMovementMultiplier(key.pressingForward, key.pressingBack);
        key.movementSideways = getMovementMultiplier(key.pressingLeft, key.pressingRight);
        key.jumping = this.settings.jumpKey.isPressed();
        key.sneaking = this.settings.sneakKey.isPressed();
        if (slowDown) {
            key.movementSideways *= swiftSneakFactor;
            key.movementForward *= swiftSneakFactor;
        }

    }

    /**
     * @author FrozenBlock
     * @reason swift sneak
     */
    @Overwrite
    public void tick(boolean slowDown) {
    }
}
