package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.WildMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class WardenEntitySoulsFeatureRenderer extends EyesFeatureRenderer<WardenEntity, WardenEntityModel<WardenEntity>> {

    private static final RenderLayer SOULS = RenderLayer.getEntityTranslucent(new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_souls.png"));

    public WardenEntitySoulsFeatureRenderer(FeatureRendererContext<WardenEntity, WardenEntityModel<WardenEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WardenEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        matrices.push();
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(SOULS);

        int j = getLight(240);

        vertex(vertexConsumer, matrix4f, matrix3f, j, 0.0F, 0, 0, 1);
        vertex(vertexConsumer, matrix4f, matrix3f, j, 1.0F, 0, 1, 1);
        vertex(vertexConsumer, matrix4f, matrix3f, j, 1.0F, 1, 1, 0);
        vertex(vertexConsumer, matrix4f, matrix3f, j, 0.0F, 1, 0, 0);

        matrices.pop();
        super.render(matrices, vertexConsumers, 0, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
    }
    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int i, float f, int j, int k, int l) {
        vertexConsumer
                .vertex(matrix4f, f - 0.5F, j - 0.5F, 0.0F)
                .color(0, 0, 0, 0)
                .texture(k, l)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(i)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .next();
    }


    protected int getLight(int i) {
        return 240;
    }

    public RenderLayer getEyesTexture() {
        return SOULS;
    }
}
