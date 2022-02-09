package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.liukrastapi.AnimationAPI;
import frozenblock.wild.mod.liukrastapi.MathAddon;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.PI;

public class FrogEntityModel extends EntityModel<FrogEntity> {
    private final ModelPart root;
    private final ModelPart main;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart croaking_body;
    private final ModelPart tongue;
    private final ModelPart tongue_eat;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart left_leg;
    private final ModelPart right_leg;
    
    private double Animationtime;
    private FrogEntity c;
    private boolean croak = true;
    private double croakstartTime;
    private float togueBegin;

    public FrogEntityModel(ModelPart root) {
        this.root = root.getChild("root");
        this.right_leg = this.root.getChild("right_leg");
        this.left_leg = this.root.getChild("left_leg");
        this.main = this.root.getChild("main");
        this.body = this.main.getChild("body");
        this.right_arm = this.body.getChild("right_arm");
        this.left_arm = this.body.getChild("left_arm");
        this.tongue = this.body.getChild("tongue");
        this.tongue_eat = this.body.getChild("tongue_eat");
        this.croaking_body = this.body.getChild("croaking_body");
        this.head = this.body.getChild("head");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();


        ModelPartData modelPartData1 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F,24.0F,0.0F));
        ModelPartData modelPartData2 = modelPartData1.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F,-2.0F,4.0F));
        ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(3,1).cuboid(-6.5F, -2.0F, -8.0F, 7.0F, 3.0F, 9.0F).uv(23,22).cuboid(-6.5F, -1.0F, -8.0F, 7.0F, 0.0F, 9.0F), ModelTransform.pivot(6.0F,0.0F,0.0F));
        ModelPartData modelPartData4 = modelPartData3.addChild("head", ModelPartBuilder.create().uv(23,13).cuboid(-3.5F, -1.0F, -7.0F, 7.0F, 0.0F, 9.0F).uv(0,13).cuboid(-3.5F, -2.0F, -7.0F, 7.0F, 3.0F, 9.0F), ModelTransform.pivot(-3.0F,-2.0F,-1.0F));
        ModelPartData modelPartData5 = modelPartData4.addChild("eyes", ModelPartBuilder.create(), ModelTransform.pivot(-0.5F,0.0F,2.0F));
        modelPartData5.addChild("right_eye", ModelPartBuilder.create().uv(0,0).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(-1.5F,-3.0F,-6.5F));
        modelPartData5.addChild("left_eye", ModelPartBuilder.create().uv(0,5).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F), ModelTransform.pivot(2.5F,-3.0F,-6.5F));
        modelPartData3.addChild("croaking_body", ModelPartBuilder.create().uv(26, 5).cuboid(3.5f, 1.1f, 3.9f,7, 2, 3, new Dilation(-0.1f)), ModelTransform.pivot(-10F,-3F,-8F));
        modelPartData3.addChild("tongue", ModelPartBuilder.create().uv(17,13).cuboid(-2.0F, 0.0F, -7.1F, 4.0F, 0.0F, 7.0F), ModelTransform.pivot(-3.0F,-1.1F,1.0F));
        modelPartData3.addChild("tongue_eat", ModelPartBuilder.create().uv(17,13).cuboid(-2.0F, 0.0F, -7.1F, 4.0F, 0.0F, 10.0F), ModelTransform.pivot(-3.0F,-1.1F,1.0F)); //EDIT THIS
        modelPartData3.addChild("left_arm", ModelPartBuilder.create().uv(0,32).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F).uv(18,40).cuboid(-4.0F, 3.01F, -5.0F, 8.0F, 0.0F, 8.0F), ModelTransform.pivot(1.0F,-1.0F,-6.5F));
        modelPartData3.addChild("right_arm", ModelPartBuilder.create().uv(0,38).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F).uv(2,40).cuboid(-4.0F, 3.01F, -5.0F, 8.0F, 0.0F, 8.0F), ModelTransform.pivot(-7.0F,-1.0F,-6.5F));
        modelPartData1.addChild("left_leg", ModelPartBuilder.create().uv(14,25).cuboid(-1.0F, 0.0F, -2.0F, 3.0F, 3.0F, 4.0F).uv(2,32).cuboid(-2.0F, 3.01F, -4.0F, 8.0F, 0.0F, 8.0F), ModelTransform.pivot(3.5F,-3.0F,4.0F));
        modelPartData1.addChild("right_leg", ModelPartBuilder.create().uv(0,25).cuboid(-2.0F, 0.0F, -2.0F, 3.0F, 3.0F, 4.0F).uv(18,32).cuboid(-6.0F, 3.01F, -4.0F, 8.0F, 0.0F, 8.0F), ModelTransform.pivot(-3.5F,-3.0F,4.0F));
        return TexturedModelData.of(modelData,48,48);

    }
    @Override
    public void setAngles(@NotNull FrogEntity entity, float limbAngle, float limbDistance, float time, float netHeadYaw, float headPitch){
        //this.main.pivotY = - 2 + AnimationAPI.easeInOutSine(100, 160, 10, time) + AnimationAPI.easeInOutSine(160, 220, 10, time);

        float t = 2f; //Multiplier for animation length
        float j = (float) (180 / PI); //Converts degrees to radians

        /* STARTING ANIMATIONS */
        if (entity.canEatAnim) {
            entity.eatAnimStartTime = time;
            entity.canEatAnim=false;
        }

        float eatTime = AnimationAPI.animationTimer(time, entity.eatAnimStartTime, entity.eatAnimStartTime + 10) / 10;

        boolean canEat = eatTime != 0;

        if(canEat) { //Eat animation
            this.tongue.visible=false;
            this.tongue_eat.visible=true;
            this.head.pitch = (AnimationAPI.easeInOutSine(t * 0f, t * 0.0833f, -60f / j, eatTime) +
                    AnimationAPI.easeInOutSine(t * 0.0833f, t * 0.4167f, 0f / j, eatTime) +
                    AnimationAPI.easeInOutSine(t * 0.4167f, t * 0.5f, 60f / j, eatTime)
            );
            this.tongue_eat.pitch = (AnimationAPI.easeInOutSine(t * 0f, t * 0.0833f, 0f / j, eatTime) +
                    AnimationAPI.easeInOutSine(t * 0.0833f, t * 0.4167f, -18f / j, eatTime) +
                    AnimationAPI.easeInOutSine(t * 0.4167f, t * 0.5f, 18f / j, eatTime)
            ); //EDIT THIS
        } else {
        this.head.pitch = 0;
        this.tongue.pitch = 0;
        this.tongue.visible=true;
        this.tongue_eat.visible=false;
        c = entity;
        this.Animationtime = time;
        float animationspeed = 2F;
        float defaultmultiplier = MathHelper.clamp(0.7F * limbDistance,-7.5f/j,7.5f/j);

        if(!entity.isSubmergedInWater()) { //Walk Animation
            if(entity.isOnGround()) {
                float rightanimation = (float) MathHelper.clamp(MathAddon.cutCos(limbAngle * animationspeed, 0, false) * defaultmultiplier,-7.5f,7.5f);
                float leftanimation = (float) MathHelper.clamp(MathAddon.cutCos(limbAngle * animationspeed, 0, true) * defaultmultiplier,-7.5f,7.5f);

                this.main.roll = MathHelper.clamp(-2 * rightanimation,-7.5f/j,7.5f/j);
                this.main.pitch = MathHelper.clamp(-rightanimation,-7.5f/j,0);

                this.right_arm.roll = 2 * rightanimation;

                this.body.roll = MathHelper.clamp(leftanimation,-7.5f/j,7.5f/j);
                this.body.pitch = MathHelper.clamp(-leftanimation,-7.5f/j,7.5f/j);
                this.left_arm.roll = -leftanimation;

                this.right_arm.pitch = -9 * leftanimation;
                this.left_arm.pitch = -9 * rightanimation;

                this.right_leg.pitch = 9 * rightanimation;
                this.left_leg.pitch = 9 * leftanimation;

                float translationanimation1 = (float) MathAddon.cutSin(limbAngle * animationspeed, 0, true) * defaultmultiplier;
                float translationanimation2 = (float) MathAddon.cutSin(limbAngle * animationspeed, 0, false) * defaultmultiplier;

                if (limbDistance < 0.13) {
                    this.right_arm.pivotZ = -6.5f - 35 * translationanimation1;
                    this.left_arm.pivotZ = -6.5f - 35 * translationanimation2;

                    this.right_leg.pivotZ = 4 + 35 * translationanimation2;
                    this.left_leg.pivotZ = 4 + 35 * translationanimation1;
                } else {
                    this.right_arm.pivotZ = -6.5f;
                    this.left_arm.pivotZ = -6.5f;

                    this.right_leg.pivotZ = 4;
                    this.left_leg.pivotZ = 4;
                }
            } else {
                this.main.roll = 0;
                this.main.pitch = 0;
                this.right_arm.roll = 0;

                this.body.roll = 0;
                this.body.pitch = 0;
                this.left_arm.roll = 0;

                this.right_arm.pitch = -(float)Math.PI/2;
                this.left_arm.pitch = -(float)Math.PI/2;

                this.right_leg.pitch = (float)Math.PI/2;
                this.left_leg.pitch = (float)Math.PI/2;

                this.right_arm.pivotZ = -6.5f;
                this.left_arm.pivotZ = -6.5f;

                this.right_leg.pivotZ = 4;
                this.left_leg.pivotZ = 4;
            }
        } else {
            this.main.roll = 0;
            this.main.pitch = 0;
            this.body.roll = 0;
            this.body.pitch = 0;

            this.left_arm.roll = MathHelper.cos(limbAngle * animationspeed) * defaultmultiplier;
            this.right_arm.roll = -MathHelper.cos(limbAngle * animationspeed) * defaultmultiplier;

            float cosAnim = (float) MathAddon.cutCos(limbAngle * animationspeed, 0, true) * defaultmultiplier;
            float regAnim = (float) MathAddon.cutCos(limbAngle * animationspeed, 0, true) * (float)(Math.PI/2);

            this.right_arm.pitch = regAnim;
            this.left_arm.pitch = regAnim;

            this.left_leg.pitch = regAnim;
            this.right_leg.pitch = regAnim;

            if (limbDistance < 0.13) {
                this.right_arm.pivotZ = -6.5f - 30 * cosAnim;
                this.left_arm.pivotZ = -6.5f - 30 * cosAnim;

                this.right_leg.pivotZ = 4 + 30 * cosAnim;
                this.left_leg.pivotZ = 4 + 30 * cosAnim;
            } else {
                this.right_arm.pivotZ = -6.5f;
                this.left_arm.pivotZ = -6.5f;

                this.right_leg.pivotZ = 4;
                this.left_leg.pivotZ = 4;
            }

        }
    }
}
    @Override
    public void render(MatrixStack matrixStack, VertexConsumer	buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        root.render(matrixStack, buffer, packedLight, packedOverlay);
        float animation;

        if(this.croak) {
            if(Math.random() < 0.005) {
                this.croak = false;
                this.croakstartTime = this.Animationtime;
            }
        } else {
            double time = this.Animationtime - this.croakstartTime;
            animation = (float) MathAddon.cutSin(time/10, 0, false);
            matrixStack.translate(animation/4, 1.33+animation/6, animation/4-0.05);
            if(this.c.isOnGround()) {
                matrixStack.scale(1.3f*animation, 2*animation, 2*animation);
                this.croaking_body.render(matrixStack, buffer, packedLight, packedOverlay);
            } else {
                matrixStack.scale(0, 0, 0);
            }
            if(time > 0.1) {
                if(animation == 0) {
                    this.croak = true;
                }
            }
        }
    }
}
