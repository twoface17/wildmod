package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.WildModClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class FrogEntityRenderer extends MobEntityRenderer<FrogEntity, FrogEntityModel> {
    private static final Identifier SWAMP_TEXTURE = new Identifier(WildMod.MOD_ID, "textures/entity/frog/frog_swamp.png");
    private static final Identifier COLD_TEXTURE = new Identifier(WildMod.MOD_ID, "textures/entity/frog/frog_cold.png");
    private static final Identifier TROPICAL_TEXTURE = new Identifier(WildMod.MOD_ID, "textures/entity/frog/frog_tropical.png");

    public FrogEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new FrogEntityModel(context.getPart(WildModClient.MODEL_FROG_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(FrogEntity entity) {
        if(entity.getVariant() == FrogEntity.Variant.TROPICAL) {
            return TROPICAL_TEXTURE;
        } else if(entity.getVariant() == FrogEntity.Variant.COLD) {
            return COLD_TEXTURE;
        } else {
            return SWAMP_TEXTURE;
        }
    }



}