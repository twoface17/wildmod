package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.WildModClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class WardenEntityRenderer extends MobEntityRenderer<WardenEntity, WardenEntityModel> {

    public WardenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new WardenEntityModel(context.getPart(WildModClient.MODEL_WARDEN_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(WardenEntity entity) {
        return new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden.png");
    }

    @Environment(EnvType.CLIENT)
    private static class GlowingLayer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
        public GlowingLayer(FeatureRendererContext<T, M> context) {
            super(context);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance,
                           float tickDelta, float animationProgress, float headYaw, float headPitch) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(new Identifier("textures/entity/warden/warden_overlay.png")));
            this.getContextModel().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        }
    }


}
