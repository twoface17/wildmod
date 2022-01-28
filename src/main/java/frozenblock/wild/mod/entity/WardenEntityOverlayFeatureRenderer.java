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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

@Environment(EnvType.CLIENT)
public class WardenEntityOverlayFeatureRenderer extends EyesFeatureRenderer<WardenEntity, WardenEntityModel<WardenEntity>> {

    private RenderLayer OVERLAY;
    private int lightLevel = 1048576;

    public WardenEntityOverlayFeatureRenderer(FeatureRendererContext<WardenEntity, WardenEntityModel<WardenEntity>> featureRendererContext) {
        super(featureRendererContext);
        OVERLAY = RenderLayer.getEyes(new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_overlay.png"));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, WardenEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {;
        String string = Formatting.strip(entity.getName().getString());
        if ("Osmiooo".equals(string)) {
            OVERLAY = RenderLayer.getEyes(new Identifier(WildMod.MOD_ID, "textures/entity/warden/secret_warden_overlay.png"));
        } else {
            OVERLAY = RenderLayer.getEyes(new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_overlay.png"));
        }
        int a = entity.world.getLightLevel(LightType.BLOCK, entity.getBlockPos());
        int b = entity.world.getLightLevel(LightType.SKY, entity.getBlockPos());
        int o = Math.max(a, b);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.OVERLAY);
        this.getContextModel().render(matrices, vertexConsumer, calculateLight(o), OverlayTexture.DEFAULT_UV, colors(o),colors(o),colors(o), 1.0f);
    }

    private int calculateLight(int light) {
        float d = (float) ((float) Math.cos((light*lightLevel)/(15728640/3)));
        return (int) (MathHelper.clamp(d,0,1)) * (15728640/2);
    }
    private float colors(int light) {
        float d = (float) ((float) Math.cos((light*lightLevel)/(15728640/3)));
        return MathHelper.clamp(d,0,1);
    }

        public RenderLayer getEyesTexture() {
            return OVERLAY;
        }

}
