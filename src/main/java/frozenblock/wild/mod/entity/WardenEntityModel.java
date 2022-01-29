package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.liukrastapi.AnimationAPI;
import frozenblock.wild.mod.liukrastapi.MathAddon;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import static java.lang.Math.PI;

public class WardenEntityModel<T extends WardenEntity> extends EntityModel<WardenEntity> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart right_ear;
    private final ModelPart left_ear;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public WardenEntityModel(ModelPart root) {
        this.body = root.getChild("body");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
        this.head = this.body.getChild("head");
        this.left_ear = this.head.getChild("left_ear");
        this.right_ear = this.head.getChild("right_ear");
        this.left_leg = root.getChild("left_leg");
        this.right_leg = root.getChild("right_leg");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData1 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0,26).cuboid(-9.0F, -21.0F, -5.0F, 18.0F, 21.0F, 11.0F), ModelTransform.pivot(0.0F,13.0F,0.0F));
        ModelPartData modelPartData2 = modelPartData1.addChild("head", ModelPartBuilder.create().uv(0,0).cuboid(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F), ModelTransform.pivot(0.0F,-21.0F,0.0F));
        modelPartData2.addChild("right_ear", ModelPartBuilder.create().uv(106,36).cuboid(-10.0F, -6.5F, 0.0F, 10.0F, 10.0F, 0.001F), ModelTransform.pivot(-8.0F,-12.5F,0.0F));
        modelPartData2.addChild("left_ear", ModelPartBuilder.create().uv(106,46).cuboid(0.0F, -6.5F, 0.0F, 10.0F, 10.0F, 0.001F), ModelTransform.pivot(8.0F,-12.5F,0.0F));
        modelPartData1.addChild("right_arm", ModelPartBuilder.create().uv(52,0).cuboid(-6.0F, -4.0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(-11.0F,-17.0F,0.0F));
        modelPartData1.addChild("left_arm", ModelPartBuilder.create().uv(84,0).cuboid(-2.0F, -4.0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(11.0F,-17.0F,0.0F));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(82,36).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(6.0F,11.0F,0.0F));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(58,36).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(-6.0F,11.0F,0.0F));
        return TexturedModelData.of(modelData,128,64);
    }

    @Override
    public void setAngles(WardenEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

        int r = entity.getRoarTicksLeft1();
        int emergeticksleft = entity.getEmergeTicksLeft();
        float time = animationProgress / 10;

        /* EMERGE ANIMATION */

        float t = 2; //Multiplier for animation length
        float j = (float) (180/PI);
        float bodyY = 13;
        float legY = 11;
            //Body transforms
            this.body.pivotY = bodyY + 80 + (AnimationAPI.easeOutSine(t * 0, t * 0.68f, 0, time) +
                    AnimationAPI.easeOutSine(t * 0.68f, t * 1.04f, -50f, time) +
                    AnimationAPI.easeOutSine(t * 1.04f, t * 1.2f, +2f, time) +
                    AnimationAPI.easeOutSine(t * 1.2f, t * 3.24f, 0f, time) +
                    AnimationAPI.easeOutSine(t * 3.24f, t * 3.92f, -17f, time) +
                    AnimationAPI.easeOutSine(t * 3.92f, t * 4.6f, -5f, time) +
                    AnimationAPI.easeInSine(t * 4.6f, t * 4.76f, +1.13f, time) +
                    AnimationAPI.easeOutSine(t * 4.76f, t * 5.48f, -1.13f, time) +
                    AnimationAPI.easeInOutSine(t * 5.48f, t * 5.84f, -10f, time)
            );
            this.body.pivotZ = 10 + (AnimationAPI.easeOutSine(0, t * 5.48f, 0, time) +
                    AnimationAPI.easeOutSine(t * 5.48f, t * 5.84f, -10f, time)
            );
            this.body.roll = 10/j + (AnimationAPI.easeOutSine(0, t * 1.12f, 0/j, time) +
                    AnimationAPI.easeInSine(t * 1.12f, t * 1.24f, 7.5f/j, time) +
                    AnimationAPI.easeOutSine(t * 1.28f, t * 1.56f, 13.05f/j, time) +
                    AnimationAPI.easeInSine(t * 1.56f, t * 2.08f, -10.55f/j, time)
            );
            this.body.pitch = (AnimationAPI.easeOutSine(0, t * 1.12f, 0/j, time) +
                    AnimationAPI.easeInSine(t * 1.12f, t * 1.24f, 5f/j, time) +
                    AnimationAPI.easeOutSine(t * 1.28f, t * 1.56f, -16.1f/j, time) +
                    AnimationAPI.easeInSine(t * 1.56f, t * 2.08f, 11.1f/j, time) +
                    AnimationAPI.easeInOutSine(t * 2.8f, t * 3.76f, 32.5f/j, time) +
                    AnimationAPI.easeInSine(t * 3.76f, t * 4.4f, 22.5f/j, time) +
                    AnimationAPI.easeInSine(t * 4.4f, t * 5.04f, -12.5f/j, time) +
                    AnimationAPI.easeInSine(t * 5.04f, t * 6.04f, 25f/j, time) +
                    AnimationAPI.easeOutSine(t * 6.04f, t * 7.44f, -67.5f/j, time)
            );

            //Head transforms
        this.head.pitch = (95 / j) + (AnimationAPI.easeOutSine(0, t * 0.76f, 0 / j, time) +
                AnimationAPI.easeOutSine(t * 0.76f, t * 1.2f, 120f / j, time) +
                AnimationAPI.easeInSine(t * 1.2f, t * 1.44f, 22.5f / j, time) +
                AnimationAPI.easeOutSine(t * 0.76f, t * 1.2f, -32.5f / j, time) +
                AnimationAPI.easeInSine(t * 1.2f, t * 2.48f, 10f / j, time) +
                AnimationAPI.easeInOutSine(t * 2.48f, t * 2.8f, 42f / j, time) +
                AnimationAPI.easeInSine(t * 2.48f, t * 3.76f, 70f / j, time) +
                AnimationAPI.easeInOutSine(t * 3.76f, t * 4.92f, -30f / j, time) +
                AnimationAPI.easeOutSine(t * 6.16f, t * 7f, -57.5f / j, time)
        );
            this.head.roll = (AnimationAPI.easeOutSine(t * 1.2f, t * 1.44f, 2.5f/j, time) +
                    AnimationAPI.easeOutSine(t * 1.44f, t * 1.84f, -2.5f/j, time)
            );

            //Left Leg transforms
            this.left_leg.pivotY = legY + 80 + (AnimationAPI.easeOutSine(0, t * 0.68f, 0, time) +
                    AnimationAPI.easeOutSine(t * 0.68f, t * 1.04f, -50f, time) +
                    AnimationAPI.easeOutSine(t * 1.04f, t * 1.2f, +2f, time) +
                    AnimationAPI.easeOutSine(t * 1.2f, t * 3.24f, 0f, time) +
                    AnimationAPI.easeOutSine(t * 3.24f, t * 3.92f, -17f, time) +
                    AnimationAPI.easeOutSine(t * 3.92f, t * 4.6f, -5f, time) +
                    AnimationAPI.easeInSine(t * 4.6f, t * 4.76f, +1.13f, time) +
                    AnimationAPI.easeOutSine(t * 4.76f, t * 5.48f, -1.13f, time) +
                    AnimationAPI.easeInOutSine(t * 5.48f, t * 5.84f, -10f, time)
            );
            this.left_leg.pivotZ = 10 + (AnimationAPI.easeOutSine(0, t * 5.48f, 0, time) +
                    AnimationAPI.easeOutSine(t * 5.48f, t * 5.84f, -10f, time)
            );

            //Right Leg transforms
            this.right_leg.pivotY = legY + 80 + (AnimationAPI.easeOutSine(0, t * 0.68f, 0, time) +
                    AnimationAPI.easeOutSine(t * 0.68f, t * 1.04f, -50f, time) +
                    AnimationAPI.easeOutSine(t * 1.04f, t * 1.2f, +2f, time) +
                    AnimationAPI.easeOutSine(t * 1.2f, t * 3.24f, 0f, time) +
                    AnimationAPI.easeOutSine(t * 3.24f, t * 3.92f, -17f, time) +
                    AnimationAPI.easeOutSine(t * 3.92f, t * 4.6f, -5f, time) +
                    AnimationAPI.easeInSine(t * 4.6f, t * 4.76f, +1.13f, time) +
                    AnimationAPI.easeOutSine(t * 4.76f, t * 5.48f, -1.13f, time) +
                    AnimationAPI.easeInOutSine(t * 5.48f, t * 5.84f, -10f, time)
            );
            this.right_leg.pivotZ = 10 + (AnimationAPI.easeOutSine(0, t * 5.48f, 0, time) +
                    AnimationAPI.easeOutSine(t * 5.48f, t * 5.84f, -10f, time)
            );
            /* WALK & IDLE ANIMATION */

                if (r > 0) {
                    if (r == 10) {
                        entity.setRoarAnimationProgress(animationProgress);
                    } else {
                        if (emergeticksleft == 0) {
                            double a = animationProgress - entity.getRoarAnimationProgress();

                            this.right_arm.roll = (-MathHelper.sin(limbAngle * 0.6662F) * 0.7F * limbDistance / 4 + (-MathHelper.sin(animationProgress / 20) / 20)) + 0.05F;
                            this.left_arm.roll = (-MathHelper.sin((limbAngle * 0.6662F) - 0.5F) * 0.7F * limbDistance / 4 + (-MathHelper.sin(animationProgress / 20) / 20)) - 0.05F;

                            this.right_arm.pitch = -MathHelper.cos((limbAngle * 0.6662F) - 0.5F) * 1.4F * limbDistance / 2 - MathHelper.cos(animationProgress / 20) / 20;
                            this.left_arm.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance / 2 + MathHelper.cos(animationProgress / 20) / 20;

                            this.right_leg.pitch = MathHelper.clamp(MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance, -35, 35);
                            this.left_leg.pitch = MathHelper.clamp(MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance, -35, 35);

                            this.head.pitch = headPitch * 0.017453292F - (float) MathAddon.cutSin(limbAngle * 0.6662F, 0, false) * 0.7F * limbDistance / 2;
                            this.head.yaw = headYaw * 0.017453292F - (-MathHelper.sin(limbAngle * 0.6662F + 3.1415927F)) * 0.7F * limbDistance / 2;
                            this.head.roll = -MathHelper.sin(limbAngle * 0.6662F + 3.1415927F) * 0.7F * limbDistance / 2;

                            this.body.pitch = -MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance / 2 + MathHelper.cos(animationProgress / 20) / 20;

                            this.left_ear.yaw = MathHelper.sin(animationProgress / 20 * MathHelper.sin(limbAngle * 0.6662F) * 1.4F * limbDistance) / 5 + MathHelper.sin(animationProgress / 20) / 5;
                            this.right_ear.yaw = -MathHelper.sin(animationProgress / 20 * MathHelper.sin(limbAngle * 0.6662F) * 1.4F * limbDistance) / 5 - MathHelper.sin(animationProgress / 20) / 5;

                            this.body.roll = MathHelper.cos(limbAngle * 0.6662F) * 0.7F * limbDistance / 4 + MathHelper.cos(animationProgress / 20) / 20;

                        }
                    }
                }

                int a = entity.getAttackTicksLeft1();
                float eq;
                if (a > 0) {
                    eq = -2.0F + 1.5F * MathHelper.wrap((float) a - animationProgress / 200, 0.2F);
                } else {
                    eq = 0;
                }

                /* ATTACK ANIMATION */
                if (emergeticksleft <= 0) {
                    this.right_arm.roll = (-MathHelper.sin(limbAngle * 0.6662F) * 0.7F * limbDistance / 4 + (-MathHelper.sin(animationProgress / 20) / 20)) + 0.05F;
                    this.left_arm.roll = (-MathHelper.sin((limbAngle * 0.6662F) - 0.5F) * 0.7F * limbDistance / 4 + (-MathHelper.sin(animationProgress / 20) / 20)) - 0.05F;

                    this.right_arm.pitch = MathHelper.clamp(-MathHelper.cos((limbAngle * 0.6662F) - 0.5F) * 1.4F * limbDistance / 2 - MathHelper.cos(animationProgress / 20) / 20 + eq, -15, 15);
                    this.left_arm.pitch = MathHelper.clamp(MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance / 2 - MathHelper.cos(animationProgress / 20) / 20 + eq, -15, 5);

                    this.right_leg.pitch = MathHelper.clamp(MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance, -35, 35);
                    this.left_leg.pitch = MathHelper.clamp(MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance, -35, 35);

                    this.head.pitch = headPitch * 0.017453292F - (float) MathAddon.cutSin(limbAngle * 0.6662F, 0, false) * 0.7F * limbDistance / 2;
                    this.head.yaw = headYaw * 0.017453292F - MathHelper.sin(limbAngle * 0.6662F + 3.1415927F) * 0.7F * limbDistance / 2;
                    this.head.roll = MathHelper.sin(limbAngle * 0.6662F + 3.1415927F) * 0.7F * limbDistance / 2;

                    this.body.pitch = MathHelper.clamp(MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance / 2 + MathHelper.cos(animationProgress / 20) / 20, -1, 1);

                    this.left_ear.yaw = MathHelper.sin(animationProgress / 20 * MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance) / 5 + MathHelper.sin(animationProgress / 20) / 5;
                    this.right_ear.yaw = -MathHelper.sin(animationProgress / 20 * MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance) / 5 - MathHelper.sin(animationProgress / 20) / 5;

                    this.body.roll = MathHelper.cos(limbAngle * 0.6662F) * 0.7F * limbDistance / 4 + MathHelper.cos(animationProgress / 20) / 20;
                }
            }
    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        left_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        right_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}