package frozenblock.wild.mod.entity;


import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.WildModClient;
import frozenblock.wild.mod.liukrastapi.NewEntityRendererFactory;
import frozenblock.wild.mod.render.entity.HeldItemFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class AllayEntityRenderer extends MobEntityRenderer<AllayEntity, AllayEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/allay/allay.png");

    public AllayEntityRenderer(NewEntityRendererFactory.Context context) {
        super(context, new AllayEntityModel(context.getPart(WildModClient.MODEL_ALLAY_LAYER)), 0.4F);
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
    }

    public Identifier getTexture(AllayEntity allayEntity) {
        return TEXTURE;
    }

    protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }
}

