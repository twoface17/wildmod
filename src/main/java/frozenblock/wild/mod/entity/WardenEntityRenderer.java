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
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WardenEntityRenderer extends MobEntityRenderer<WardenEntity, WardenEntityModel> {

    public WardenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new WardenEntityModel(context.getPart(WildModClient.MODEL_WARDEN_LAYER)), 0.5f);
        this.addFeature(new WardenEntityOverlayFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(WardenEntity entity) {
        return new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden.png");
    }


}
