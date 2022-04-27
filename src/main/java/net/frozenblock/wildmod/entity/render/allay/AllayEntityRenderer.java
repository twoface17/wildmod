package net.frozenblock.wildmod.entity.render.allay;


import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.WildModClient;
import net.frozenblock.wildmod.entity.AllayEntity;
import net.frozenblock.wildmod.entity.AllayHeldItemFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class AllayEntityRenderer extends MobEntityRenderer<AllayEntity, AllayEntityModel> {
    private static final Identifier TEXTURE = new Identifier(WildMod.MOD_ID,"textures/entity/allay/allay.png");

    public AllayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AllayEntityModel(context.getPart(WildModClient.MODEL_ALLAY_LAYER)), 0.4F);
        //this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
        this.addFeature(new AllayHeldItemFeatureRenderer(this));
    }

    public Identifier getTexture(AllayEntity allayEntity) {
        return TEXTURE;
    }

    protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }
}

