package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.liukrastapi.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value= EnvType.CLIENT)
public class WardenEntityModel<T extends WardenEntity>
        extends SinglePartEntityModel<T> {
    private static final float field_38324 = 13.0f;
    private static final float field_38325 = 1.0f;
    private static final Vec3f field_38326 = new Vec3f();
    private final ModelPart root;
    protected final ModelPart bone;
    protected final ModelPart body;
    protected final ModelPart head;
    protected final ModelPart rightTendril;
    protected final ModelPart leftTendril;
    protected final ModelPart leftLeg;
    protected final ModelPart leftArm;
    protected final ModelPart rightArm;
    protected final ModelPart rightLeg;

    public WardenEntityModel(ModelPart modelPart) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.root = modelPart;
        this.bone = modelPart.getChild("bone");
        this.body = this.bone.getChild("body");
        this.head = this.body.getChild("head");
        this.rightLeg = this.bone.getChild("right_leg");
        this.leftLeg = this.bone.getChild("left_leg");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightTendril = this.head.getChild("right_tendril");
        this.leftTendril = this.head.getChild("left_tendril");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData1 = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        ModelPartData modelPartData2 = modelPartData1.addChild("body", ModelPartBuilder.create().uv(0,0).cuboid(-9.0F, -10.0F, -4.0F, 18.0F, 21.0F, 11.0F), ModelTransform.pivot(-9.0F,-21.0F,-4.0F));
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(0,32).cuboid(-8.0F, -26F, -5.0F, 16.0F, 16.0F, 10.0F), ModelTransform.pivot(-8.0F,-25.0F,-5.0F));
        modelPartData3.addChild("right_tendril", ModelPartBuilder.create().uv(52,32).cuboid(-24.0F, -32.0F, 0.0F, 16.0F, 16.0F, 0.002F), ModelTransform.pivot(-24.0F,-31.5F,0.0F));
        modelPartData3.addChild("left_tendril", ModelPartBuilder.create().uv(58,0).cuboid(8.0F, -32.0F, 0.0F, 16.0F, 16.0F, 0.002F), ModelTransform.pivot(8.0F,-31.5F,0.0F));
        modelPartData2.addChild("right_arm", ModelPartBuilder.create().uv(44,50).cuboid(-4.0F, 3.0F, -5.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(-4.0F,-32.0F,-5.0F));
        modelPartData2.addChild("left_arm", ModelPartBuilder.create().uv(0,58).cuboid(-4.0F, 3F, -5.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(-25.0F,-32.0F,-5.0F));
        modelPartData1.addChild("right_leg", ModelPartBuilder.create().uv(76,48).cuboid(2.9F, 11.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(2.9F,-13.0F,6.0F));
        modelPartData1.addChild("left_leg", ModelPartBuilder.create().uv(76,76).cuboid(-8.9F, 11.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(-8.9F,-13.0F,6.0F));
        return TexturedModelData.of(modelData,128,128);
    }

    @Override
    public void setAngles(T wardenEntity, float f, float g, float h, float i, float j) {
        this.getPart().traverse().forEach(modelPart -> ((ExpandedModelPart)modelPart).resetModelTransform());
        float k = h - (float)wardenEntity.age;
        float l = Math.min(0.5F, 3.0F * g);
        float m = h * 0.1F;
        float n = f * 0.8662F;
        float o = MathHelper.cos(n);
        float p = MathHelper.sin(n);
        float q = MathHelper.cos(m);
        float r = MathHelper.sin(m);
        float s = Math.min(0.35F, l);
        this.head.pitch = j * 0.017453292F;
        this.head.yaw = i * 0.017453292F;
        ModelPart var10000 = this.head;
        var10000.roll += 0.3F * p * l;
        var10000 = this.head;
        var10000.roll += 0.06F * q;
        var10000 = this.head;
        var10000.pitch += 1.2F * MathHelper.cos(n + 1.5707964F) * s;
        var10000 = this.head;
        var10000.pitch += 0.06F * r;
        this.body.roll = 0.1F * p * l;
        var10000 = this.body;
        var10000.roll += 0.025F * r;
        this.body.pitch = 1.0F * o * s;
        var10000 = this.body;
        var10000.pitch += 0.025F * q;
        this.leftLeg.pitch = 1.0F * o * l;
        this.rightLeg.pitch = 1.0F * MathHelper.cos(n + 3.1415927F) * l;
        this.leftArm.pitch = -(0.8F * o * l);
        this.leftArm.roll = 0.0F;
        this.rightArm.pitch = -(0.8F * p * l);
        this.rightArm.roll = 0.0F;
        this.leftArm.yaw = 0.0F;
        this.leftArm.pivotZ = 1.0F;
        this.leftArm.pivotX = 13.0F;
        this.leftArm.pivotY = -13.0F;
        this.rightArm.yaw = 0.0F;
        this.rightArm.pivotZ = 1.0F;
        this.rightArm.pivotX = -13.0F;
        this.rightArm.pivotY = -13.0F;
        float t = wardenEntity.method_42223(k) * (float)(Math.cos((double)h * 2.25D) * 3.141592653589793238462643383279D * 0.10000000149011612D);
        this.leftTendril.pitch = t;
        this.rightTendril.pitch = -t;
        long u = Util.getMeasuringTimeMs();
        //this.method_42579(wardenEntity.field_38137, Animations.field_38306, u);
        this.method_42579(wardenEntity.diggingAnimation, WardenAnimation.WARDEN_DIG, u);
        this.method_42579(wardenEntity.emergingAnimation, WardenAnimation.WARDEN_EMERGE, u);
        this.method_42579(wardenEntity.roaringAnimation, WardenAnimation.WARDEN_ROAR, u);
        this.method_42579(wardenEntity.sniffingAnimation, WardenAnimation.WARDEN_SNIFF, u);
    }

    public void method_42579(AnimationState animationState2, AnimationDefinition animation, long l) {
        animationState2.ifStarted(animationState -> KeyframeAnimations.animate(this, animation, l - animationState.startTime(), 1.0f, field_38326));
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
