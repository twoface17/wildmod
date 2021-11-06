package frozenblock.wild.mod.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
@Environment(EnvType.CLIENT)
public class FrogEntityModel extends EntityModel<FrogEntity> {
    private final ModelPart body;
    private final ModelPart mouth;
    private final ModelPart tongue;
    private final ModelPart sac;
    private final ModelPart front_left_leg;
    private final ModelPart front_right_leg;
    private final ModelPart back_left_leg;
    private final ModelPart back_right_leg;
    public FrogEntityModel(ModelPart root) {
        this.body = root.getChild("body");
        this.back_right_leg = this.body.getChild("back_right_leg");
        this.back_left_leg = this.body.getChild("back_left_leg");
        this.front_right_leg = this.body.getChild("front_right_leg");
        this.front_left_leg = this.body.getChild("front_left_leg");
        this.sac = this.body.getChild("sac");
        this.tongue = this.body.getChild("tongue");
        this.mouth = this.body.getChild("mouth");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        Dilation dilation = new Dilation(1F);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData1 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0,12).cuboid(-3.5F, -3.0F, -4.5F, 7.0F, 3.0F, 9.0F).uv(23,18).cuboid(-3.5F, -2.01F, -4.5F, 7.0F, 0.0F, 9.0F), ModelTransform.pivot(0.0F,23.0F,0.0F));
        modelPartData1.addChild("mouth", ModelPartBuilder.create().uv(0,0).cuboid(-3.5F, -2.0F, -7.0F, 7.0F, 3.0F, 9.0F).uv(23,18).cuboid(-3.5F, -1.0F, -7.0F, 7.0F, 0.0F, 9.0F).uv(23,13).cuboid(0.5F, -4.0F, -6.0F, 3.0F, 2.0F, 3.0F).uv(35,13).cuboid(-3.5F, -4.0F, -6.0F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F,-3.0F,2.5F));
        modelPartData1.addChild("tongue", ModelPartBuilder.create().uv(8,24).cuboid(-2.0F, -3.25F, -3.5F, 4.0F, 0.0F, 8.0F), ModelTransform.pivot(0.0F,1.0F,0.0F));
        modelPartData1.addChild("sac", ModelPartBuilder.create().uv(8,24).cuboid(-3.5F, -3.25F, -4.5F, 7.0F, 2.0F, 3.0F, dilation), ModelTransform.pivot(0.0F,1.0F,0.0F));
        modelPartData1.addChild("front_left_leg", ModelPartBuilder.create().uv(32,7).cuboid(0.0F, -1.0F, -1.0F, 2.0F, 3.0F, 3.0F).uv(-4,0).cuboid(-2.0F, 2.0F, -3.0F, 4.0F, 0.0F, 4.0F), ModelTransform.pivot(3.0F,-1.0F,-3.0F));
        modelPartData1.addChild("front_right_leg", ModelPartBuilder.create().uv(42,7).cuboid(-5.0F, -3.0F, -4.0F, 2.0F, 3.0F, 3.0F, true).uv(-4,4).cuboid(-5.0F, 0.0F, -6.0F, 4.0F, 0.0F, 4.0F, true), ModelTransform.pivot(0.0F,1.0F,0.0F));
        modelPartData1.addChild("back_left_leg", ModelPartBuilder.create().uv(23,0).cuboid(-0.5F, -1.0F, -2.5F, 3.0F, 3.0F, 4.0F).uv(-4,12).cuboid(1.5F, 2.0F, -3.5F, 3.0F, 0.0F, 4.0F), ModelTransform.pivot(3.0F,-1.0F,4.0F));
        modelPartData1.addChild("back_right_leg", ModelPartBuilder.create().uv(37,0).cuboid(-2.5F, -1.0F, -2.5F, 3.0F, 3.0F, 4.0F, true).uv(-4,16).cuboid(-4.5F, 2.0F, -3.5F, 3.0F, 0.0F, 4.0F, true), ModelTransform.pivot(-3.0F,-1.0F,4.0F));
        return TexturedModelData.of(modelData,64,32);
    }
    @Override
    public void setAngles(FrogEntity entity, float limbAngle, float limbDistance, float animationProgress, float netHeadYaw, float headPitch){
        this.body.roll = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance/4;
        float leganim = (MathHelper.cos(limbAngle * 0.6662F * 2) * 1.4F * limbDistance/4);
        this.body.pivotY = MathHelper.sqrt(leganim*leganim)*2 + 23;
        this.front_right_leg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.front_left_leg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.back_left_leg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.back_right_leg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.sac.visible = MathHelper.cos(animationProgress/5) > 0.9;

    }




    @Override
    public void render(MatrixStack matrixStack, VertexConsumer	buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

        body.render(matrixStack, buffer, packedLight, packedOverlay);
    }

}