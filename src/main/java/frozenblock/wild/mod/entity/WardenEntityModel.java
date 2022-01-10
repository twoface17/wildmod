package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.liukrastapi.MathAddon;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class WardenEntityModel extends EntityModel<WardenEntity> {
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
        ModelPartData modelPartData1 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0,26).cuboid(-9.0F, -21.0F, -5.0F, 18.0F, 21.0F, 11.0F), ModelTransform.pivot(0.0F,11.0F,0.0F));
        ModelPartData modelPartData2 = modelPartData1.addChild("head", ModelPartBuilder.create().uv(0,0).cuboid(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F), ModelTransform.pivot(0.0F,-21.0F,0.0F));
        modelPartData2.addChild("right_ear", ModelPartBuilder.create().uv(106,36).cuboid(-10.0F, -6.5F, 0.0F, 10.0F, 10.0F, 0.0F), ModelTransform.pivot(-8.0F,-12.5F,0.0F));
        modelPartData2.addChild("left_ear", ModelPartBuilder.create().uv(106,46).cuboid(0.0F, -6.5F, 0.0F, 10.0F, 10.0F, 0.0F), ModelTransform.pivot(8.0F,-12.5F,0.0F));
        modelPartData1.addChild("right_arm", ModelPartBuilder.create().uv(52,0).cuboid(-6.0F, -4.0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(-11.0F,-17.0F,0.0F));
        modelPartData1.addChild("left_arm", ModelPartBuilder.create().uv(84,0).cuboid(-2.0F, -4.0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(11.0F,-17.0F,0.0F));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(82,36).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(6.0F,11.0F,0.0F));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(58,36).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(-6.0F,11.0F,0.0F));
        return TexturedModelData.of(modelData,128,64);
    }

    @Override
    public void setAngles(WardenEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

        int r = entity.getRoarTicksLeft1();



        if(r > 0) {
            if(r == 10) {
                entity.setRoarAnimationProgress(animationProgress);
            } else {
                double a = animationProgress - entity.getRoarAnimationProgress();

                this.right_arm.roll = 90f;
                this.left_arm.roll = -90f;

                this.right_arm.pitch = -MathHelper.cos((limbAngle * 0.6662F) - 0.5F) * 1.4F * limbDistance / 2 - MathHelper.cos(animationProgress / 20) / 20;
                this.left_arm.pitch = -MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance / 2 - MathHelper.cos(animationProgress / 20) / 20;

                this.right_leg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
                this.left_leg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;

                this.head.pitch = headPitch * 0.017453292F - (float) MathAddon.cutSin(limbAngle * 0.6662F, 0, false) * 0.7F * limbDistance / 2;
                this.head.yaw = headYaw * 0.017453292F - (-MathHelper.sin(limbAngle * 0.6662F + 3.1415927F)) * 0.7F * limbDistance / 2;
                this.head.roll = -MathHelper.sin(limbAngle * 0.6662F + 3.1415927F) * 0.7F * limbDistance / 2;

                this.body.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance / 2 + MathHelper.cos(animationProgress / 20) / 20;

                this.left_ear.yaw = MathHelper.cos(animationProgress / 20 * MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance) / 5 + MathHelper.cos(animationProgress / 20) / 5;
                this.right_ear.yaw = -MathHelper.cos(animationProgress / 20 * MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance) / 5 - MathHelper.cos(animationProgress / 20) / 5;

                this.body.roll = MathHelper.cos(limbAngle * 0.6662F) * 0.7F * limbDistance / 4 + MathHelper.cos(animationProgress / 20) / 20;

            }
        }


            int a = entity.getAttackTicksLeft1();
            float eq;
            if (a > 0) {
                eq = -2.0F + 1.5F * MathHelper.wrap((float) a - animationProgress / 200, 0.2F);
            } else {
                eq = 0;
            }

            this.right_arm.roll = (-MathHelper.sin(limbAngle * 0.6662F) * 0.7F * limbDistance / 4 + (-MathHelper.sin(animationProgress / 20) / 20)) + 0.05F;
            this.left_arm.roll = (-MathHelper.sin((limbAngle * 0.6662F) - 0.5F) * 0.7F * limbDistance / 4 + (-MathHelper.sin(animationProgress / 20) / 20)) - 0.05F;

            this.right_arm.pitch = -MathHelper.cos((limbAngle * 0.6662F) - 0.5F) * 1.4F * limbDistance / 2 - MathHelper.cos(animationProgress / 20) / 20 + eq;
            this.left_arm.pitch = -MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance / 2 - MathHelper.cos(animationProgress / 20) / 20 + eq;

            this.right_leg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
            this.left_leg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;

            this.head.pitch = headPitch * 0.017453292F - (float) MathAddon.cutSin(limbAngle * 0.6662F, 0, false) * 0.7F * limbDistance / 2;
            this.head.yaw = headYaw * 0.017453292F - MathHelper.sin(limbAngle * 0.6662F + 3.1415927F) * 0.7F * limbDistance / 2;
            this.head.roll = MathHelper.sin(limbAngle * 0.6662F + 3.1415927F) * 0.7F * limbDistance / 2;

            this.body.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance / 2 + MathHelper.cos(animationProgress / 20) / 20;

            this.left_ear.yaw = MathHelper.cos(animationProgress / 20 * MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance) / 5 + MathHelper.cos(animationProgress / 20) / 5;
            this.right_ear.yaw = -MathHelper.cos(animationProgress / 20 * MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance) / 5 - MathHelper.cos(animationProgress / 20) / 5;

            this.body.roll = MathHelper.cos(limbAngle * 0.6662F) * 0.7F * limbDistance / 4 + MathHelper.cos(animationProgress / 20) / 20;

    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        body.render(matrixStack, buffer, packedLight, packedOverlay);
        left_leg.render(matrixStack, buffer, packedLight, packedOverlay);
        right_leg.render(matrixStack, buffer, packedLight, packedOverlay);
    }

}