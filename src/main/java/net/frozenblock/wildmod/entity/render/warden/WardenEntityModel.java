package net.frozenblock.wildmod.entity.render.warden;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.liukrastapi.ExpandedModelPart;
import net.frozenblock.wildmod.liukrastapi.ExpandedSinglePartEntityModel;
import net.frozenblock.wildmod.liukrastapi.animation.WardenAnimations;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

import java.util.List;

@Environment(EnvType.CLIENT)
public class WardenEntityModel<T extends WardenEntity> extends SinglePartEntityModel<T> {
    private static final float field_38324 = 13.0F;
    private static final float field_38325 = 1.0F;
    private final ModelPart root;
    protected final ModelPart bone;
    protected final ModelPart body;
    protected final ModelPart head;
    protected final ModelPart rightTendril;
    protected final ModelPart leftTendril;
    protected final ModelPart leftLeg;
    protected final ModelPart leftArm;
    protected final ModelPart leftRibcage;
    protected final ModelPart rightArm;
    protected final ModelPart rightLeg;
    protected final ModelPart rightRibcage;
    private final List<ModelPart> tendrils;
    private final List<ModelPart> justBody;
    private final List<ModelPart> headAndLimbs;
    private final List<ModelPart> bodyHeadAndLimbs;

    public WardenEntityModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.root = root;
        this.bone = root.getChild("bone");
        this.body = this.bone.getChild("body");
        this.head = this.body.getChild("head");
        this.rightLeg = this.bone.getChild("right_leg");
        this.leftLeg = this.bone.getChild("left_leg");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightTendril = this.head.getChild("right_tendril");
        this.leftTendril = this.head.getChild("left_tendril");
        this.rightRibcage = this.body.getChild("right_ribcage");
        this.leftRibcage = this.body.getChild("left_ribcage");
        this.tendrils = ImmutableList.of(this.leftTendril, this.rightTendril);
        this.justBody = ImmutableList.of(this.body);
        this.headAndLimbs = ImmutableList.of(this.head, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
        this.bodyHeadAndLimbs = ImmutableList.of(this.body, this.head, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData1 = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F,24.0F,0.0F));
        ModelPartData modelPartData2 = modelPartData1.addChild("body", ModelPartBuilder.create().uv(0,0).cuboid(-9.0F, -13.0F, -4.0F, 18.0F, 21.0F, 11.0F), ModelTransform.pivot(0.0F,-21.0F,0.0F));
        modelPartData2.addChild("right_ribcage", ModelPartBuilder.create().uv(90,11).cuboid(-2.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F), ModelTransform.pivot(-7.0F,-2.0F,-4.0F));
        modelPartData2.addChild("left_ribcage", ModelPartBuilder.create().uv(90,11).cuboid(-7.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F, true), ModelTransform.pivot(7.0F,-2.0F,-4.0F));
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(0,32).cuboid(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F), ModelTransform.pivot(0.0F,-26.0F,0.0F));
        modelPartData3.addChild("right_tendril", ModelPartBuilder.create().uv(52,32).cuboid(-16.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F), ModelTransform.pivot(-8.0F,-12.0F,0.0F));
        modelPartData3.addChild("left_tendril", ModelPartBuilder.create().uv(58,0).cuboid(0.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F), ModelTransform.pivot(8.0F,-12.0F,0.0F));
        modelPartData2.addChild("right_arm", ModelPartBuilder.create().uv(44,50).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(-13.0F,-13.0F,1.0F));
        modelPartData2.addChild("left_arm", ModelPartBuilder.create().uv(0,58).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(13.0F,-13.0F,1.0F));
        modelPartData1.addChild("right_leg", ModelPartBuilder.create().uv(76,48).cuboid(-3.1F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(-11.9F,-8.0F,0.0F));
        modelPartData1.addChild("left_leg", ModelPartBuilder.create().uv(76,76).cuboid(-2.9F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(11.9F,-8.0F,0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    public void setAngles(T wardenEntity, float f, float g, float h, float i, float j) {
        this.getPart().traverse().forEach(modelPart -> ((ExpandedModelPart)modelPart).resetTransform());
        float k = h - (float)wardenEntity.age;
        this.setHeadAngle(i, j);
        this.setLimbAngles(f, g);
        this.setHeadAndBodyAngles(h);
        this.setTendrilPitches(wardenEntity, h, k);
        ((ExpandedSinglePartEntityModel)this).updateAnimation(wardenEntity.attackingAnimationState, WardenAnimations.ATTACKING, h);
        ((ExpandedSinglePartEntityModel)this).updateAnimation(wardenEntity.chargingSonicBoomAnimationState, WardenAnimations.CHARGING_SONIC_BOOM, h);
        ((ExpandedSinglePartEntityModel)this).updateAnimation(wardenEntity.diggingAnimationState, WardenAnimations.DIGGING, h);
        ((ExpandedSinglePartEntityModel)this).updateAnimation(wardenEntity.emergingAnimationState, WardenAnimations.EMERGING, h);
        ((ExpandedSinglePartEntityModel)this).updateAnimation(wardenEntity.roaringAnimationState, WardenAnimations.ROARING, h);
        ((ExpandedSinglePartEntityModel)this).updateAnimation(wardenEntity.sniffingAnimationState, WardenAnimations.SNIFFING, h);
    }

    private void setHeadAngle(float yaw, float pitch) {
        this.head.pitch = pitch * (float) (Math.PI / 180.0);
        this.head.yaw = yaw * (float) (Math.PI / 180.0);
    }

    private void setHeadAndBodyAngles(float animationProgress) {
        float f = animationProgress * 0.1F;
        float g = MathHelper.cos(f);
        float h = MathHelper.sin(f);
        this.head.roll += 0.06F * g;
        this.head.pitch += 0.06F * h;
        this.body.roll += 0.025F * h;
        this.body.pitch += 0.025F * g;
    }

    private void setLimbAngles(float angle, float distance) {
        float f = Math.min(0.5F, 3.0F * distance);
        float g = angle * 0.8662F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = Math.min(0.35F, f);
        this.head.roll += 0.3F * i * f;
        this.head.pitch += 1.2F * MathHelper.cos(g + (float) (Math.PI / 2)) * j;
        this.body.roll = 0.1F * i * f;
        this.body.pitch = 1.0F * h * j;
        this.leftLeg.pitch = 1.0F * h * f;
        this.rightLeg.pitch = 1.0F * MathHelper.cos(g + (float) Math.PI) * f;
        this.leftArm.pitch = -(0.8F * h * f);
        this.leftArm.roll = 0.0F;
        this.rightArm.pitch = -(0.8F * i * f);
        this.rightArm.roll = 0.0F;
        this.setPivots();
    }

    private void setPivots() {
        this.head.pivotX = 0.0F;
        this.head.pivotY = -13.0F;
        this.head.pivotZ = 0.0F;

        this.bone.pivotY = 3.0F;

        this.leftTendril.pivotX = 8.0F;
        this.leftTendril.pivotY = -12.0F;
        this.leftTendril.pivotZ = 0.0F;

        this.rightTendril.pivotX = -8.0F;
        this.rightTendril.pivotY = -12.0F;
        this.rightTendril.pivotZ = 0.0F;

        this.leftRibcage.pivotX = 7.0F;
        this.leftRibcage.pivotY = -2.0F;
        this.leftRibcage.pivotZ = -4.0F;

        this.rightRibcage.pivotX = -7.0F;
        this.rightRibcage.pivotY = -2.0F;
        this.rightRibcage.pivotZ = -4.0F;

        this.leftArm.yaw = 0.0F;
        this.leftArm.pivotZ = 1.0F;
        this.leftArm.pivotX = 13.0F;
        this.leftArm.pivotY = -13.0F;

        this.rightArm.yaw = 0.0F;
        this.rightArm.pivotZ = 1.0F;
        this.rightArm.pivotX = -13.0F;
        this.rightArm.pivotY = -13.0F;

        this.leftLeg.pivotX = 5.9F;
        this.leftLeg.pivotY = 8.0F;
        this.leftLeg.pivotZ = 0.0F;

        this.rightLeg.pivotX = -5.9F;
        this.rightLeg.pivotY = 8.0F;
        this.rightLeg.pivotZ = 0.0F;

    }

    private void setTendrilPitches(T warden, float animationProgress, float tickDelta) {
        float f = warden.getTendrilPitch(tickDelta) * (float)(Math.cos((double)animationProgress * 2.25) * Math.PI * 0.1F);
        this.leftTendril.pitch = f;
        this.rightTendril.pitch = -f;
    }

    public ModelPart getPart() {
        return this.root;
    }

    public List<ModelPart> getTendrils() {
        return this.tendrils;
    }

    public List<ModelPart> getBody() {
        return this.justBody;
    }

    public List<ModelPart> getHeadAndLimbs() {
        return this.headAndLimbs;
    }

    public List<ModelPart> getBodyHeadAndLimbs() {
        return this.bodyHeadAndLimbs;
    }
}
