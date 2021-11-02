package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.WildMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WardenEntityOverlayFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EyesFeatureRenderer<T, EntityModel<T>> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_overlay.png"));

    public WardenEntityOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super((FeatureRendererContext<T, EntityModel<T>>) featureRendererContext);
    }

    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}
