package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.liukrastapi.AnimationAPI;
import frozenblock.wild.mod.liukrastapi.MathAddon;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class WardenEntityModel extends EntityModel<WardenEntity> {
    private final ModelPart rig;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart left_ear;
    private final ModelPart right_ear;
    private final ModelPart arms;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart left_leg;
    private final ModelPart right_leg;
    public WardenEntityModel(ModelPart root) {
        this.rig = root.getChild("rig");
        this.right_leg = this.rig.getChild("right_leg");
        this.left_leg = this.rig.getChild("left_leg");
        this.body = this.rig.getChild("body");
        this.arms = this.body.getChild("arms");
        this.left_arm = this.arms.getChild("left_arm");
        this.right_arm = this.arms.getChild("right_arm");
        this.head = this.body.getChild("head");
        this.right_ear = this.head.getChild("right_ear");
        this.left_ear = this.head.getChild("left_ear");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData modelPartData1 = modelPartData.addChild("rig", ModelPartBuilder.create(), ModelTransform.pivot(0.0F,24.0F,0.0F));
        ModelPartData modelPartData2 = modelPartData1.addChild("body", ModelPartBuilder.create().uv(0,26).cuboid(-9.0F, -21.0F, -5.0F, 18.0F, 21.0F, 11.0F), ModelTransform.pivot(0.0F,-13.0F,0.0F));
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(0,0).cuboid(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F), ModelTransform.pivot(0.0F,-21.0F,0.0F));
        modelPartData3.addChild("left_ear", ModelPartBuilder.create().uv(106,46).cuboid(0.0F, -6.5F, 0.0F, 10.0F, 10.0F, 0.0F), ModelTransform.pivot(8.0F,-12.5F,0.0F));
        modelPartData3.addChild("right_ear", ModelPartBuilder.create().uv(106,36).cuboid(-10.0F, -6.5F, 0.0F, 10.0F, 10.0F, 0.0F), ModelTransform.pivot(-8.0F,-12.5F,0.0F));
        ModelPartData modelPartData4 = modelPartData2.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F,0.0F,0.0F));
        modelPartData4.addChild("right_arm", ModelPartBuilder.create().uv(52,0).cuboid(-6.0F, -4.0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(-11.0F,-17.0F,0.0F));
        modelPartData4.addChild("left_arm", ModelPartBuilder.create().uv(84,0).cuboid(-2.0F, -4.0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(11.0F,-17.0F,0.0F));
        modelPartData1.addChild("left_leg", ModelPartBuilder.create().uv(82,36).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(6.0F,-13.0F,0.0F));
        modelPartData1.addChild("right_leg", ModelPartBuilder.create().uv(58,36).cuboid(-3.0F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(-6.0F,-13.0F,0.0F));
        return TexturedModelData.of(modelData,128,64);

    }
    @Override
    public void setAngles(WardenEntity entity, float limbSwing, float limbSwingAmount, float animationProgress, float netHeadYaw, float headPitch) {
        float time = animationProgress/10;
        // Default pivots of the anim
        float rigY = 24;
        float left_armX = 11;
        float left_armY = -17;
        float bodyY = -13;

        this.rig.pivotY = rigY + 40 + (
                AnimationAPI.linear(0, 0.68f, 0, time)
                + AnimationAPI.linear(0.68f, 1.04f, -10F, time)
                + AnimationAPI.linear(1.04f, 1.2f, +2F, time)
                + AnimationAPI.linear(1.2f, 3.24f, 0F, time)
                + AnimationAPI.linear(3.24f, 3.92f, -17F, time)
                + AnimationAPI.linear(3.92f, 4.6f, -5F, time)
                + AnimationAPI.linear(4.6f, 4.76f, +1.13F, time)
                + AnimationAPI.linear(4.76f, 5.48f, -1.13F, time)
                + AnimationAPI.linear(5.48f, 5.84f, -10F, time)
                );
        this.body.pitch = (float)Math.toRadians(0);
        this.body.roll = (float)Math.toRadians(-10);

        this.head.pitch = (float)Math.toRadians(95);

        this.right_arm.pitch = (float)Math.toRadians(-202.5);
        this.right_arm.yaw = (float)Math.toRadians(87.5);
        this.right_arm.roll = (float)Math.toRadians(180);

        this.left_arm.pivotX = left_armX - 0.65f;
        this.left_arm.pivotY = left_armY +3.35f;
        this.left_arm.pivotZ = 0;

    }
    @Override
    public void render(MatrixStack matrixStack, VertexConsumer	buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        rig.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}