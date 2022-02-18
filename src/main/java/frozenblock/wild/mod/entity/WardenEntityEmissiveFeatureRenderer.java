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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

@Environment(EnvType.CLIENT)
public class WardenEntityEmissiveFeatureRenderer extends EyesFeatureRenderer<WardenEntity, WardenEntityModel<WardenEntity>> {

    private RenderLayer OVERLAY;
    private RenderLayer SECRET_OVERLAY;

    public WardenEntityEmissiveFeatureRenderer(FeatureRendererContext<WardenEntity, WardenEntityModel<WardenEntity>> featureRendererContext) {
        super(featureRendererContext);
        OVERLAY = RenderLayer.getEyes(new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_bioluminescent_layer.png"));
        SECRET_OVERLAY = RenderLayer.getEyes(new Identifier(WildMod.MOD_ID, "textures/entity/warden/secret_warden_overlay.png"));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WardenEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {;
        if (entity.shouldRender) {
        String string = Formatting.strip(entity.getName().getString());
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.OVERLAY);
        if ("Osmiooo".equals(string)) {vertexConsumer = vertexConsumers.getBuffer(this.SECRET_OVERLAY);}
        int a = calculateLight(getBlockLight(entity, entity.getBlockPos()));
        float b = colors(getBlockLight(entity, entity.getBlockPos()));
        this.getContextModel().render(matrices, vertexConsumer, a, OverlayTexture.DEFAULT_UV, b, b, b, 1.0f);
        }
    }
    private float getBlockLight(WardenEntity wardenEntity, BlockPos blockPos) {
        int i = (int)MathHelper.clampedLerp(0.0F, 15.0F, 1.0F - wardenEntity.lightTransitionTicks / 10.0F);
        return i == 15 ? 15 : Math.max(i, wardenEntity.world.getLightLevel(LightType.BLOCK, blockPos));
    }

    private int calculateLight(float light) {
        float d = (float) ((float) Math.cos((light*Math.PI)/30));
        int ret = (int) ((MathHelper.clamp(d,0,1)) * 15728640);
        return ret;
    }
    private float colors(float light) {
        float d = (float) ((float) Math.cos((light*Math.PI)/30));
        float a = MathHelper.clamp(d,0,1);
        return a;
    }

        public RenderLayer getEyesTexture() {
            return OVERLAY;
        }

}
