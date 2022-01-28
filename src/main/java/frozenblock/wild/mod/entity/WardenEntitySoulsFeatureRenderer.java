package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.WildMod;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;


public class WardenEntitySoulsFeatureRenderer extends EyesFeatureRenderer<WardenEntity, WardenEntityModel<WardenEntity>> {

    public RenderLayer SOULS;

    public WardenEntitySoulsFeatureRenderer(FeatureRendererContext<WardenEntity, WardenEntityModel<WardenEntity>> featureRendererContext) {
        super(featureRendererContext);
        SOULS = RenderLayer.getEntityTranslucentCull(new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_souls.png"));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WardenEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.SOULS);
        this.getContextModel().render(matrices, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, calculateBeats(entity));
    }

    private static float calculateBeats(WardenEntity warden) {
        float a = warden.lastHeartBeat;
        float b = warden.world.getTime();
        float c = warden.heartbeatTime;

        float toNow = (a-b) * -1;
        float d = (float) ((float) Math.cos((toNow)/c));

        return MathHelper.clamp(d,0,1);
    }

    public RenderLayer getEyesTexture() {
        return this.SOULS;
    }
}
