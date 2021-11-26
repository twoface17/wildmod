package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.WildModClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PufferfishEntity;
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

    public void render(FrogEntity frogEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(frogEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }



}