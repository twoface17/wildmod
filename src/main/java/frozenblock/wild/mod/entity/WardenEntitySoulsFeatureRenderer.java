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
        SOULS = RenderLayer.getEyes(new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_souls.png"));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WardenEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.SOULS);
        this.getContextModel().render(matrices, vertexConsumer, (int) (calculateBeats(entity)*15728640), OverlayTexture.DEFAULT_UV, calculateBeats(entity), calculateBeats(entity), calculateBeats(entity), 1.0f);
    }

    private static float calculateBeats(WardenEntity warden) {
        //TODO: FIX #63 SO THIS WORKS PROPERLY (BUT HOW DO WE DO IT?)
        float a = warden.lastHeartBeat;
        float b = warden.world.getTime();
        float c = warden.heartbeatTime;

        float toNow = (a-b) * -1;
        float d = (float) ((float) Math.cos((toNow)/(c/1.5)));

        return MathHelper.clamp(d,0,1);
    }

    public RenderLayer getEyesTexture() {
        return this.SOULS;
    }
}
