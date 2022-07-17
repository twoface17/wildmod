package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.WildModClient;
import net.frozenblock.wildmod.liukrastapi.AdvancedMath;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin<T extends LivingEntity> {
    @Shadow
    public BipedEntityModel.ArmPose rightArmPose;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart head;

    @Shadow
    public BipedEntityModel.ArmPose leftArmPose;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Inject(method = "positionRightArm", at = @At("TAIL"))
    private void positionRightArm(T entity, CallbackInfo ci) {
        if (this.rightArmPose == WildModClient.TOOT_HORN_ARM) {
            this.rightArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
            this.rightArm.yaw = this.head.yaw - (float) (AdvancedMath.PI / 6);
        }
    }

    @Inject(method = "positionLeftArm", at = @At("TAIL"))
    private void positionLeftArm(T entity, CallbackInfo ci) {
        if (this.leftArmPose == WildModClient.TOOT_HORN_ARM) {
            this.leftArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
            this.leftArm.yaw = this.head.yaw + (float) (AdvancedMath.PI / 6);
        }
    }
}
