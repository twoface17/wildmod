package net.frozenblock.wildmod.entity.render.allay;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.entity.AllayEntity;
import net.frozenblock.wildmod.liukrastapi.ExpandedModelPart;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class AllayEntityModel extends SinglePartEntityModel<AllayEntity> implements ModelWithArms {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private static final float field_38999 = 0.6981317F;
    private static final float field_39000 = -0.7853982F;
    private static final float field_39001 = -1.0471976F;

    public AllayEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
    }

    public ModelPart getPart() {
        return this.root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 23.5F, 0.0F));
        modelPartData2.addChild(
                "head",
                ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -3.99F, 0.0F)
        );
        ModelPartData modelPartData3 = modelPartData2.addChild(
                "body",
                ModelPartBuilder.create()
                        .uv(0, 10)
                        .cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 16)
                        .cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.0F, -4.0F, 0.0F)
        );
        modelPartData3.addChild(
                "right_arm",
                ModelPartBuilder.create().uv(23, 0).cuboid(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(-0.01F)),
                ModelTransform.pivot(-1.75F, 0.5F, 0.0F)
        );
        modelPartData3.addChild(
                "left_arm",
                ModelPartBuilder.create().uv(23, 6).cuboid(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(-0.01F)),
                ModelTransform.pivot(1.75F, 0.5F, 0.0F)
        );
        modelPartData3.addChild(
                "right_wing",
                ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.5F, 0.0F, 0.65F)
        );
        modelPartData3.addChild(
                "left_wing",
                ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.5F, 0.0F, 0.65F)
        );
        return TexturedModelData.of(modelData, 32, 32);
    }

    public void setAngles(AllayEntity allayEntity, float f, float g, float h, float i, float j) {
        this.getPart().traverse().forEach(modelPart -> ((ExpandedModelPart) modelPart).resetTransform());
        float k = h * 20.0F * 0.017453292F + g;
        float l = MathHelper.cos(k) * 3.1415927F * 0.15F;
        float m = h - (float) allayEntity.age;
        float n = h * 9.0F * 0.017453292F;
        float o = Math.min(g / 0.3F, 1.0F);
        float p = 1.0F - o;
        float q = allayEntity.method_43397(m);
        this.rightWing.pitch = 0.43633232F;
        this.rightWing.yaw = -0.61086524F + l;
        this.leftWing.pitch = 0.43633232F;
        this.leftWing.yaw = 0.61086524F - l;
        float r = o * 0.6981317F;
        this.body.pitch = r;
        float s = MathHelper.lerp(q, r, MathHelper.lerp(o, (float) (-Math.PI / 3), (float) (-Math.PI / 4)));
        this.root.pivotY += (float) Math.cos(n) * 0.25F * p;
        this.rightArm.pitch = s;
        this.leftArm.pitch = s;
        float t = p * (1.0F - q);
        float u = 0.43633232F - MathHelper.cos(n + 4.712389F) * 3.1415927F * 0.075F * t;
        this.leftArm.roll = -u;
        this.rightArm.roll = u;
        this.rightArm.yaw = 0.27925268F * q;
        this.leftArm.yaw = -0.27925268F * q;

        this.leftArm.pivotX = 1.75F;
        this.rightArm.pivotX = -1.75F;

        this.root.pivotY = 16.0F;
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay);
    }

    public void setArmAngle(Arm arm, MatrixStack matrices) {
        float f = -1.5F;
        float g = 1.5F;
        this.root.rotate(matrices);
        this.body.rotate(matrices);
        matrices.translate(0.0, -0.09375, 0.09375);
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(this.rightArm.pitch + 0.43633232F));
        matrices.scale(0.7F, 0.7F, 0.7F);
        matrices.translate(0.0625, 0.0, 0.0);
    }
}