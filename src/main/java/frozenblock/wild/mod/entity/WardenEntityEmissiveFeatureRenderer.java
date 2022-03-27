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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WardenEntityEmissiveFeatureRenderer extends EyesFeatureRenderer<WardenEntity, WardenEntityModel<WardenEntity>> {

    private final RenderLayer OVERLAY;
    private final RenderLayer SECRET_OVERLAY;

    public WardenEntityEmissiveFeatureRenderer(FeatureRendererContext<WardenEntity, WardenEntityModel<WardenEntity>> featureRendererContext) {
        super(featureRendererContext);
        OVERLAY = RenderLayer.getEntityTranslucentCull(new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_bioluminescent_layer.png"));
        SECRET_OVERLAY = RenderLayer.getEntityTranslucentCull(new Identifier(WildMod.MOD_ID, "textures/entity/warden/secret_warden_overlay.png"));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WardenEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.shouldRender) {
        String string = Formatting.strip(entity.getName().getString());
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.OVERLAY);
        if ("Osmiooo".equals(string)) {vertexConsumer = vertexConsumers.getBuffer(this.SECRET_OVERLAY);}
        this.getContextModel().render(matrices, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public RenderLayer getEyesTexture() {
            return OVERLAY;
        }

}
