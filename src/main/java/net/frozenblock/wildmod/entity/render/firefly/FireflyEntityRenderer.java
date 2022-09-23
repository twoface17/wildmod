package net.frozenblock.wildmod.entity.render.firefly;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.FireflyEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class FireflyEntityRenderer extends EntityRenderer<FireflyEntity> {

    //CREDIT TO magistermaks ON GITHUB!!

    public FireflyEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    private final Identifier TEXTURE = new Identifier(WildMod.MOD_ID, "textures/entity/firefly/firefly.png");
    private final RenderLayer LAYER = RenderLayer.getEntityCutout(TEXTURE);

    @Override
    public void render(FireflyEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(1, 1, 1); // you may need to adjust this
        matrixStack.translate(0, 0.3, 0);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));

        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);

        int j = getLight(i);

        vertex(vertexConsumer, matrix4f, matrix3f, j, 0.0F, 0, 0, 1);
        vertex(vertexConsumer, matrix4f, matrix3f, j, 1.0F, 0, 1, 1);
        vertex(vertexConsumer, matrix4f, matrix3f, j, 1.0F, 1, 1, 0);
        vertex(vertexConsumer, matrix4f, matrix3f, j, 0.0F, 1, 0, 0);

        matrixStack.pop();
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(FireflyEntity entity) {
        return TEXTURE;
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int i, float f, int j, int k, int l) {
        vertexConsumer
                .vertex(matrix4f, f - 0.5F, j - 0.5F, 0.0F)
                .color(255, 255, 255, 255)
                .texture(k, l)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(i)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .next();
    }

    protected int getLight(int i) {
        return 240;
    }

}
