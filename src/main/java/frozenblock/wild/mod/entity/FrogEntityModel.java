package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.liukrastapi.MathAddon;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;

public class FrogEntityModel extends EntityModel<FrogEntity> {
    private double Animationtime;
    private FrogEntity c;
    private boolean croak = true;
    private double croakstartTime;
    private float togueBegin;

    private final ModelPart main;
    private final ModelPart body;
    private final ModelPart front_left_leg;
    private final ModelPart front_right_leg;
    private final ModelPart front_right_leg_sub_0;
    private final ModelPart back_left_leg;
    private final ModelPart back_right_leg;
    private final ModelPart back_right_leg_sub_0;
    private final ModelPart mouth;
    private final ModelPart tongue;
    private final ModelPart sac;
    public FrogEntityModel(ModelPart root) {
        this.Animationtime = 0;
        this.main = root.getChild("main");
        this.body = this.main.getChild("body");
        this.sac = this.body.getChild("sac");
        this.tongue = this.body.getChild("tongue");
        this.mouth = this.body.getChild("mouth");
        this.back_right_leg = this.body.getChild("back_right_leg");
        this.back_right_leg_sub_0 = this.back_right_leg.getChild("back_right_leg_sub_0");
        this.back_left_leg = this.body.getChild("back_left_leg");
        this.front_right_leg = this.body.getChild("front_right_leg");
        this.front_right_leg_sub_0 = this.front_right_leg.getChild("front_right_leg_sub_0");
        this.front_left_leg = this.body.getChild("front_left_leg");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData modelPartData1 = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F,23.0F,0.0F));
        ModelPartData modelPartData2 = modelPartData1.addChild("body", ModelPartBuilder.create().uv(0,12).cuboid(-6.5F, -3.0F, -4.5F, 7.0F, 3.0F, 9.0F).uv(23,18).cuboid(-6.5F, -2.01F, -4.5F, 7.0F, 0.0F, 9.0F), ModelTransform.pivot(6.0F,0.0F,0.0F));
        modelPartData2.addChild("front_left_leg", ModelPartBuilder.create().uv(32,7).cuboid(0.0F, -2.0F, -2.0F, 2.0F, 3.0F, 3.0F).uv(-4,0).cuboid(-2.0F, 1.0F, -4.0F, 4.0F, 0.0F, 4.0F), ModelTransform.pivot(0.0F,0.0F,-2.0F));
        ModelPartData modelPartData3 = modelPartData2.addChild("front_right_leg", ModelPartBuilder.create(), ModelTransform.pivot(-6.0F,0.0F,-2.0F));
        modelPartData3.addChild("front_right_leg_sub_0", ModelPartBuilder.create().uv(42,7).cuboid(-5.0F, -3.0F, -4.0F, 2.0F, 3.0F, 3.0F, true).uv(-4,4).cuboid(-5.0F, 0.0F, -6.0F, 4.0F, 0.0F, 4.0F, true), ModelTransform.pivot(3.0F,1.0F,2.0F));
        modelPartData2.addChild("back_left_leg", ModelPartBuilder.create().uv(23,0).cuboid(-0.5F, -2.0F, -1.5F, 3.0F, 3.0F, 4.0F).uv(-4,12).cuboid(1.5F, 1.0F, -2.5F, 3.0F, 0.0F, 4.0F), ModelTransform.pivot(0.0F,0.0F,3.0F));
        ModelPartData modelPartData4 = modelPartData2.addChild("back_right_leg", ModelPartBuilder.create(), ModelTransform.pivot(-6.0F,0.0F,3.0F));
        modelPartData4.addChild("back_right_leg_sub_0", ModelPartBuilder.create().uv(37,0).cuboid(-5.5F, -3.0F, 1.5F, 3.0F, 3.0F, 4.0F, true).uv(-4,16).cuboid(-7.5F, 0.0F, 0.5F, 3.0F, 0.0F, 4.0F, true), ModelTransform.pivot(3.0F,1.0F,-3.0F));
        modelPartData2.addChild("mouth", ModelPartBuilder.create().uv(0,0).cuboid(-3.5F, -2.0F, -7.0F, 7.0F, 3.0F, 9.0F).uv(23,18).cuboid(-3.5F, -1.0F, -7.0F, 7.0F, 0.0F, 9.0F).uv(23,13).cuboid(0.5F, -4.0F, -6.0F, 3.0F, 2.0F, 3.0F).uv(35,13).cuboid(-3.5F, -4.0F, -6.0F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(-3.0F,-3.0F,2.5F));
        modelPartData2.addChild("tongue", ModelPartBuilder.create().uv(-8,24).cuboid(-2.0F, -3.25F, -3.5F, 4.0F, 0.0F, 8.0F), ModelTransform.pivot(-3.0F,1.0F,0.0F));
        modelPartData2.addChild("sac", ModelPartBuilder.create().uv(8,24).cuboid(-3.5F, -3.25F, -4.5F, 7.0F, 2.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.pivot(-3.0F,1.0F,0.0F));
        return TexturedModelData.of(modelData,64,32);


    }
    @Override
    public void setAngles(FrogEntity entity, float limbAngle, float limbDistance, float time, float netHeadYaw, float headPitch){

        if(entity.getTogue() == 10) {
            this.togueBegin = 100;
        }
        if(this.togueBegin > 0) {
            this.togueBegin = this.togueBegin - 0.6f;
        }
        this.tongue.pivotZ = -this.togueBegin/10;

        c = entity;
        this.Animationtime = time;
        float animationspeed = 2F;
        float defaultmultiplier = 0.7F * limbDistance;
        if(!entity.isSubmergedInWater()) {
            if (entity.isOnGround()) {
                this.front_left_leg.pivotX = 0;
                this.front_right_leg.pivotX = -6;
                this.back_left_leg.pivotX = 0;
                this.back_right_leg.pivotX = -6;

                this.front_left_leg.yaw = -0F;
                this.front_right_leg.yaw = 0F;
                this.back_left_leg.yaw = 0F;
                this.back_right_leg.yaw = 0F;


                float rightanimation = (float) MathAddon.cutCos(limbAngle * animationspeed, 0, false) * defaultmultiplier;
                float leftanimation = (float) MathAddon.cutCos(limbAngle * animationspeed, 0, true) * defaultmultiplier;
                this.main.roll = -2 * rightanimation;
                this.main.pitch = -rightanimation;
                this.front_right_leg.roll = 2 * rightanimation;
                this.back_right_leg.roll = rightanimation;

                this.body.roll = leftanimation;
                this.body.pitch = -leftanimation;
                this.front_left_leg.roll = -leftanimation;
                this.back_left_leg.roll = -leftanimation;

                this.front_right_leg.pitch = -9 * leftanimation;
                this.front_left_leg.pitch = -9 * rightanimation;

                this.back_right_leg.pitch = 9 * rightanimation;
                this.back_left_leg.pitch = 9 * leftanimation;

                float translationanimation1 = (float) MathAddon.cutSin(limbAngle * animationspeed,0, true) * defaultmultiplier;
                float translationanimation2 = (float) MathAddon.cutSin(limbAngle * animationspeed, 0, false) * defaultmultiplier;
                if(limbDistance < (float)1/4) {
                    this.front_right_leg.pivotZ = -2 - 30 * translationanimation1;
                    this.front_left_leg.pivotZ = -2 - 30 * translationanimation2;

                    this.back_right_leg.pivotZ = 3 + 30 * translationanimation2;
                    this.back_left_leg.pivotZ = 3 + 30 * translationanimation1;
                } else {
                    this.front_right_leg.pivotZ = -2;
                    this.front_left_leg.pivotZ = -2;

                    this.back_right_leg.pivotZ = 3;
                    this.back_left_leg.pivotZ = 3;
                }
            } else {
                this.main.roll = 0;
                this.main.pitch = 0;
                this.body.roll = 0;
                this.front_right_leg.pivotZ = -2;
                this.front_left_leg.pivotZ = -2;

                this.back_right_leg.pivotZ = 3;
                this.back_left_leg.pivotZ = 3;

                this.body.pitch = -0.2182F;
                this.front_left_leg.pitch = -0.4461F;
                this.front_left_leg.yaw = -0.4802F;
                this.front_left_leg.roll = -0.088F;
                this.front_right_leg.pitch = -0.8262F;
                this.front_right_leg.yaw = 0.5956F;
                this.front_right_leg.roll = -0.1541F;
                this.back_left_leg.pitch = 0.6471F;
                this.back_left_leg.yaw = 0.1059F;
                this.back_right_leg.yaw = -0.1059F;
                this.back_left_leg.roll = -0.139F;
                this.back_right_leg.pitch = 0.4363F;
                this.back_right_leg.roll = 0.3927F;

                this.front_left_leg.pivotX = 1;
                this.front_right_leg.pivotX = -7;
                this.back_left_leg.pivotX = 1;
                this.back_right_leg.pivotX = -7;
            }
        } else {
            float swimspeed = animationspeed/10;
            this.front_left_leg.pivotX = 0;
            this.front_right_leg.pivotX = -6;
            this.back_left_leg.pivotX = 0;
            this.back_right_leg.pivotX = -6;

            this.front_left_leg.yaw = -0F;
            this.front_right_leg.yaw = 0F;
            this.back_left_leg.yaw = 0F;
            this.back_right_leg.yaw = 0F;



            float rightanimation = (float) MathAddon.cutCos(time * swimspeed, 0, false) * 0.7f;
            float leftanimation = (float) MathAddon.cutCos(time * swimspeed, 0, true) * 0.7f;
            this.main.roll = -2 * rightanimation/5;
            this.main.pitch = -rightanimation;
            this.front_right_leg.roll = 2 * rightanimation;
            this.back_right_leg.roll = rightanimation;

            this.body.roll = leftanimation/5;
            this.body.pitch = -leftanimation;
            this.front_left_leg.roll = -leftanimation;
            this.back_left_leg.roll = -leftanimation;

            this.front_right_leg.pitch = 90 -2 * leftanimation;
            this.front_left_leg.pitch = 90 -2 * rightanimation;

            this.back_right_leg.pitch = 90 -2 * leftanimation;
            this.back_left_leg.pitch = 90 -2 * rightanimation;

            this.front_right_leg.pivotZ = -2 - 30 * (float) MathAddon.cutSin(time * swimspeed, 0, true)/10;
            this.front_left_leg.pivotZ = -2 - 30 * (float) MathAddon.cutSin(time * swimspeed, 0, false)/10;

            this.back_right_leg.pivotZ = 3 + 30 * (float) MathAddon.cutSin(time * swimspeed, 0, false)/10;
            this.back_left_leg.pivotZ = 3 + 30 * (float) MathAddon.cutSin(time * swimspeed, 0, true)/10;

        }

    }
    @Override
    public void render(MatrixStack matrixStack, VertexConsumer	buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        main.render(matrixStack, buffer, packedLight, packedOverlay);
        MatrixStack matrixStack1 = matrixStack;
        float animation;



        if(this.croak) {
            if(Math.random() < 0.005) {
                this.croak = false;
                this.croakstartTime = this.Animationtime;
                this.c.getEntityWorld().playSound(this.c.getX(), this.c.getY(), this.c.getZ(), RegisterSounds.ENTITY_FROG_AMBIENT, SoundCategory.NEUTRAL, 1, 1, true);
            }
        } else {
            double time = this.Animationtime - this.croakstartTime;
            animation = (float) MathAddon.cutSin(time/10, 0, false);
            matrixStack1.translate(animation/4, 1.33+animation/6, animation/4-0.05);
            if(this.c.isOnGround()) {
                matrixStack1.scale(1.3f*animation, 2*animation, 2*animation);
                this.sac.render(matrixStack1, buffer, packedLight, packedOverlay);
            } else {
                matrixStack1.scale(0, 0, 0);
            }
            if(time > 0.1) {
                if(animation == 0) {
                    this.croak = true;
                }
            }
        }
    }
}