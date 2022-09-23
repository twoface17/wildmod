package net.frozenblock.wildmod.entity.render.frog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.WildModClient;
import net.frozenblock.wildmod.entity.FrogEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FrogEntityRenderer extends MobEntityRenderer<FrogEntity, FrogEntityModel<FrogEntity>> {

    private static final Identifier FALLBACK = new Identifier(WildMod.MOD_ID, "textures/entity/frog/temperate_frog.png");

    public FrogEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new FrogEntityModel<>(context.getPart(WildModClient.MODEL_FROG_LAYER)), 0.3F);
    }

    public Identifier getTexture(FrogEntity frogEntity) {
        if (frogEntity.getVariant() != null) {
            return frogEntity.getVariant().texture();
        } else {
            return FALLBACK;
        }
    }
}
