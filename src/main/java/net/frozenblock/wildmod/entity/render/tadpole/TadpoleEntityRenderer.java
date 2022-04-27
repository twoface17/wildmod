package net.frozenblock.wildmod.entity.render.tadpole;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.WildModClient;
import net.frozenblock.wildmod.entity.TadpoleEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class TadpoleEntityRenderer extends MobEntityRenderer<TadpoleEntity, TadpoleEntityModel> {

    public TadpoleEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new TadpoleEntityModel(context.getPart(WildModClient.MODEL_TADPOLE_LAYER)), 0.5f);
        this.shadowRadius = 0.1F;
    }

    @Override
    public Identifier getTexture(TadpoleEntity entity) {
        return new Identifier(WildMod.MOD_ID, "textures/entity/tadpole.png");
    }


}
