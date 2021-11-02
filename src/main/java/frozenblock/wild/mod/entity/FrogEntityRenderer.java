package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.WildModClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class FrogEntityRenderer extends MobEntityRenderer<FrogEntity, FrogEntityModel> {

    public FrogEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new FrogEntityModel(context.getPart(WildModClient.MODEL_FROG_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(FrogEntity entity) {
        return new Identifier(WildMod.MOD_ID, "textures/entity/frog/frog.png");
    }


}