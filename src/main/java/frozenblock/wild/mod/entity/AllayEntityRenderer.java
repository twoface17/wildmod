package frozenblock.wild.mod.entity;


import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.WildModClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class AllayEntityRenderer extends MobEntityRenderer<AllayEntity, AllayEntityModel<AllayEntity>> {

    public AllayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AllayEntityModel<>(context.getPart(WildModClient.MODEL_ALLAY_LAYER)), 0.5f);
        this.addFeature(new AllayHeldItemFeatureRenderer(this));
    }

    protected int getBlockLight(AllayEntity entity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public Identifier getTexture(AllayEntity entity) {
        return new Identifier(WildMod.MOD_ID, "textures/entity/allay/allay.png");
    }
}