package net.frozenblock.wildmod.entity;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.entity.render.*;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MangroveBoatEntityModel extends CompositeEntityModel<MangroveBoatEntity> {
    private static final String LEFT_PADDLE = "left_paddle";
    private static final String RIGHT_PADDLE = "right_paddle";
    private static final String WATER_PATCH = "water_patch";
    private static final String BOTTOM = "bottom";
    private static final String BACK = "back";
    private static final String FRONT = "front";
    private static final String RIGHT = "right";
    private static final String LEFT = "left";
    private static final String CHEST_BOTTOM = "chest_bottom";
    private static final String CHEST_LID = "chest_lid";
    private static final String CHEST_LOCK = "chest_lock";
    private final net.frozenblock.wildmod.entity.render.ModelPart leftPaddle;
    private final net.frozenblock.wildmod.entity.render.ModelPart rightPaddle;
    private final net.frozenblock.wildmod.entity.render.ModelPart waterPatch;
    private final ImmutableList<net.frozenblock.wildmod.entity.render.ModelPart> parts;

    public MangroveBoatEntityModel(net.frozenblock.wildmod.entity.render.ModelPart root, boolean chest) {
        this.leftPaddle = root.getChild("left_paddle");
        this.rightPaddle = root.getChild("right_paddle");
        this.waterPatch = root.getChild("water_patch");
        ImmutableList.Builder<net.frozenblock.wildmod.entity.render.ModelPart> builder = new ImmutableList.Builder<>();
        builder.add(
            root.getChild("bottom"),
            root.getChild("back"),
            root.getChild("front"),
            root.getChild("right"),
            root.getChild("left"),
            this.leftPaddle,
            this.rightPaddle
        );
        if (chest) {
            builder.add(root.getChild("chest_bottom"));
            builder.add(root.getChild("chest_lid"));
            builder.add(root.getChild("chest_lock"));
        }

        this.parts = builder.build();
    }

    public static TexturedModelData getTexturedModelData(boolean chest) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 32;
        int j = 6;
        int k = 20;
        int l = 4;
        int m = 28;
        modelPartData.addChild(
                "bottom",
                ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
                ModelTransform.of(0.0F, 3.0F, 1.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );
        modelPartData.addChild(
                "back",
                ModelPartBuilder.create().uv(0, 19).cuboid(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F),
                ModelTransform.of(-15.0F, 4.0F, 4.0F, 0.0F, (float) (Math.PI * 3.0 / 2.0), 0.0F)
        );
        modelPartData.addChild(
                "front",
                ModelPartBuilder.create().uv(0, 27).cuboid(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F),
                ModelTransform.of(15.0F, 4.0F, 0.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
        );
        modelPartData.addChild(
                "right",
                ModelPartBuilder.create().uv(0, 35).cuboid(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F),
                ModelTransform.of(0.0F, 4.0F, -9.0F, 0.0F, (float) Math.PI, 0.0F)
        );
        modelPartData.addChild(
                "left", ModelPartBuilder.create().uv(0, 43).cuboid(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), ModelTransform.pivot(0.0F, 4.0F, 9.0F)
        );
        if (chest) {
            modelPartData.addChild(
                    "chest_bottom",
                    ModelPartBuilder.create().uv(0, 76).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F),
                    ModelTransform.of(-2.0F, -5.0F, -6.0F, 0.0F, (float) (-Math.PI / 2), 0.0F)
            );
            modelPartData.addChild(
                    "chest_lid",
                    ModelPartBuilder.create().uv(0, 59).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 4.0F, 12.0F),
                    ModelTransform.of(-2.0F, -9.0F, -6.0F, 0.0F, (float) (-Math.PI / 2), 0.0F)
            );
            modelPartData.addChild(
                    "chest_lock",
                    ModelPartBuilder.create().uv(0, 59).cuboid(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F),
                    ModelTransform.of(-1.0F, -6.0F, -1.0F, 0.0F, (float) (-Math.PI / 2), 0.0F)
            );
        }

        int n = 20;
        int o = 7;
        int p = 6;
        float f = -5.0F;
        modelPartData.addChild(
                "left_paddle",
                ModelPartBuilder.create().uv(62, 0).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
                ModelTransform.of(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, (float) (Math.PI / 16))
        );
        modelPartData.addChild(
                "right_paddle",
                ModelPartBuilder.create().uv(62, 20).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F),
                ModelTransform.of(3.0F, -5.0F, -9.0F, 0.0F, (float) Math.PI, (float) (Math.PI / 16))
        );
        modelPartData.addChild(
                "water_patch",
                ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F),
                ModelTransform.of(0.0F, -3.0F, 1.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );
        return TexturedModelData.of(modelData, 128, chest ? 128 : 64);
    }

    public void setAngles(MangroveBoatEntity boatEntity, float f, float g, float h, float i, float j) {
        setPaddleAngle(boatEntity, 0, this.leftPaddle, f);
        setPaddleAngle(boatEntity, 1, this.rightPaddle, f);
    }

    public ImmutableList<net.frozenblock.wildmod.entity.render.ModelPart> getParts() {
        return this.parts;
    }

    public net.frozenblock.wildmod.entity.render.ModelPart getWaterPatch() {
        return this.waterPatch;
    }

    private static void setPaddleAngle(MangroveBoatEntity entity, int sigma, ModelPart part, float angle) {
        float f = entity.interpolatePaddlePhase(sigma, angle);
        part.pitch = MathHelper.clampedLerp((float) (-Math.PI / 3), (float) (-Math.PI / 12), (MathHelper.sin(-f) + 1.0F) / 2.0F);
        part.yaw = MathHelper.clampedLerp((float) (-Math.PI / 4), (float) (Math.PI / 4), (MathHelper.sin(-f + 1.0F) + 1.0F) / 2.0F);
        if (sigma == 1) {
            part.yaw = (float) Math.PI - part.yaw;
        }

    }
}
