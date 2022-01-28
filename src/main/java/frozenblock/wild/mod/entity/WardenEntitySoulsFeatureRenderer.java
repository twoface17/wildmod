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
        this.getContextModel().render(matrices, vertexConsumer, (int) (calcBeats(entity)*15728640), OverlayTexture.DEFAULT_UV, calcBeats(entity), calcBeats(entity), calcBeats(entity), 1.0f);
    }

    private static float calcBeats(WardenEntity warden) {
        long a = warden.lastClientHeartBeat;
        long b = warden.world.getTime();

        float toNow = (a-b)*-1;
        if (toNow>(5*Math.PI)) { return 0.0f; }
        if (toNow<0) { return 0.0f; }
        float d = (float) ((float) Math.cos((toNow)/(10)));
        return MathHelper.clamp(d,0,1);
    }

    public RenderLayer getEyesTexture() {
        return this.SOULS;
    }
}
