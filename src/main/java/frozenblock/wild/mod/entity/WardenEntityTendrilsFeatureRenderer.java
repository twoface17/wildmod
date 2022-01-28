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


public class WardenEntityTendrilsFeatureRenderer extends EyesFeatureRenderer<WardenEntity, WardenEntityModel<WardenEntity>> {

    public RenderLayer funny;

    public WardenEntityTendrilsFeatureRenderer(FeatureRendererContext<WardenEntity, WardenEntityModel<WardenEntity>> featureRendererContext) {
        super(featureRendererContext);
        funny = RenderLayer.getEyes(new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_tendrils.png"));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WardenEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.funny);
        this.getContextModel().render(matrices, vertexConsumer, (int) calculateLight(entity), OverlayTexture.DEFAULT_UV, calculateColors(entity), calculateColors(entity), calculateColors(entity), 1.0f);
    }

    private static float calculateLight(WardenEntity warden) {
        float a = warden.vibrationTimer;
        float b = warden.world.getTime();

        float toNow = (a-b) * -1;
        float d = (float) ((float) Math.cos((toNow)/12));
        if (toNow>23) { return 0.0f; }
        return MathHelper.clamp(d,0,1) * 15728640;
    }

    private static float calculateColors(WardenEntity warden) {
        float a = warden.vibrationTimer;
        float b = warden.world.getTime();

        float toNow = (a-b) * -1;
        float d = (float) ((float) Math.cos((toNow)/12));
        if (toNow>24) { return 0.0f; }
        return MathHelper.clamp(d,0,1);
    }

    public RenderLayer getEyesTexture() {
        return this.funny;
    }
}
