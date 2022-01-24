package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.liukrastapi.MathAddon;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.Camera;
import org.jetbrains.annotations.NotNull;

public class FireflyEntityModel extends EntityModel<FireflyEntity> {
    private final ModelPart plane;
    public FireflyEntityModel(ModelPart root) {
        this.plane = root.getChild("root");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("plane", ModelPartBuilder.create().uv(0, 0).cuboid(0F, 0F, 0F, 16F, 16F, 16F), ModelTransform.pivot(8.0F, 8.0F, 8.0F));
        return TexturedModelData.of(modelData,16,16);
    }

    @Override
    public void setAngles(@NotNull FireflyEntity entity, float limbAngle, float limbDistance, float time, float netHeadYaw, float headPitch){
        }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

    }

    public ModelPart getPlane() {
        return plane;
    }
}