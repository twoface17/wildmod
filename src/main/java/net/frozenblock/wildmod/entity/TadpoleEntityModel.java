package net.frozenblock.wildmod.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;


public class TadpoleEntityModel extends EntityModel<TadpoleEntity> {
    private final ModelPart body;
    private final ModelPart tail;
    public TadpoleEntityModel(ModelPart root) {
        this.body = root.getChild("body");
        this.tail = this.body.getChild("tail");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData1 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0,0).cuboid(-1.5F, -2.0F, -2.0F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,24.0F,0.0F));
        modelPartData1.addChild("tail", ModelPartBuilder.create().uv(0,0).cuboid(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 7.0F), ModelTransform.pivot(0.0F,-1.0F,1.0F));
        return TexturedModelData.of(modelData,16,16);
    }
    @Override
    public void setAngles(TadpoleEntity entity, float limbSwing, float limbSwingAmount, float time, float netHeadYaw, float headPitch){
        this.tail.yaw = MathHelper.cos(time/5)/5;
    }
    @Override
    public void render(MatrixStack matrixStack, VertexConsumer	buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

        body.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}