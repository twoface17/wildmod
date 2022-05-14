package net.frozenblock.wildmod.entity.render.warden;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.WildModClient;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WardenEntityRenderer extends MobEntityRenderer<WardenEntity, WardenEntityModel<WardenEntity>> {
    private static final Identifier TEXTURE = new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden.png");
    private static final Identifier SECRET_TEXTURE = new Identifier(WildMod.MOD_ID, "textures/entity/warden/secret_warden.png");
    private static final Identifier BIOLUMINESCENT_LAYER_TEXTURE = new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_bioluminescent_layer.png");
    private static final Identifier HEART_TEXTURE = new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_heart.png");
    private static final Identifier PULSATING_SPOTS_1_TEXTURE = new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_pulsating_spots_1.png");
    private static final Identifier PULSATING_SPOTS_2_TEXTURE = new Identifier(WildMod.MOD_ID, "textures/entity/warden/warden_pulsating_spots_2.png");


    public WardenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new WardenEntityModel<>(context.getPart(WildModClient.WARDEN)), 0.9F);
        this.addFeature(new WardenFeatureRenderer<>(this, BIOLUMINESCENT_LAYER_TEXTURE, (warden, tickDelta, animationProgress) -> {
            return 1.0F;
        }, WardenEntityModel::getHeadAndLimbs));
        this.addFeature(new WardenFeatureRenderer<>(this, PULSATING_SPOTS_1_TEXTURE, (warden, tickDelta, animationProgress) -> {
            return Math.max(0.0F, MathHelper.cos(animationProgress * 0.045F) * 0.25F);
        }, WardenEntityModel::getBodyHeadAndLimbs));
        this.addFeature(new WardenFeatureRenderer<>(this, PULSATING_SPOTS_2_TEXTURE, (warden, tickDelta, animationProgress) -> {
            return Math.max(0.0F, MathHelper.cos(animationProgress * 0.045F + 3.1415927F) * 0.25F);
        }, WardenEntityModel::getBodyHeadAndLimbs));
        this.addFeature(new WardenFeatureRenderer<>(this, TEXTURE, (warden, tickDelta, animationProgress) -> {
            return warden.getTendrilPitch(tickDelta);
        }, WardenEntityModel::getTendrils));
        this.addFeature(new WardenFeatureRenderer<>(this, HEART_TEXTURE, (warden, tickDelta, animationProgress) -> {
            return warden.getHeartPitch(tickDelta);
        }, WardenEntityModel::getBody));
    }

    @Override
    public Identifier getTexture(WardenEntity entity) {
        String string = Formatting.strip(entity.getName().getString());
        if ("Osmiooo".equals(string)) {
            return SECRET_TEXTURE;
        } else {
            return TEXTURE;
        }
    }
}
