package net.frozenblock.wildmod.entity.render.warden;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.liukrastapi.ExpandedModelPart;
import net.frozenblock.wildmod.render.WildRenderLayer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class WardenFeatureRenderer<T extends WardenEntity, M extends WardenEntityModel<T>> extends FeatureRenderer<T, M> {
    private final Identifier texture;
    private final WardenFeatureRenderer.AnimationAngleAdjuster<T> animationAngleAdjuster;
    private final WardenFeatureRenderer.ModelPartVisibility<T, M> modelPartVisibility;

    public WardenFeatureRenderer(
            FeatureRendererContext<T, M> context,
            Identifier texture,
            WardenFeatureRenderer.AnimationAngleAdjuster<T> animationAngleAdjuster,
            WardenFeatureRenderer.ModelPartVisibility<T, M> modelPartVisibility
    ) {
        super(context);
        this.texture = texture;
        this.animationAngleAdjuster = animationAngleAdjuster;
        this.modelPartVisibility = modelPartVisibility;
    }

    public void render(
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l
    ) {
        if (!wardenEntity.isInvisible()) {
            this.updateModelPartVisibility();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(WildRenderLayer.getEntityTranslucentEmissive(this.texture));
            this.getContextModel().render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(wardenEntity, 0.0F), 1.0F, 1.0F, 1.0F, this.animationAngleAdjuster.apply(wardenEntity, h, j));
            this.unhideAllModelParts();
        }
    }

    private void updateModelPartVisibility() {
        List<ModelPart> list = this.modelPartVisibility.getPartsToDraw(this.getContextModel());
        this.getContextModel().getPart().traverse().forEach(part -> ((ExpandedModelPart)part).setHidden(true));
        list.forEach(part -> ((ExpandedModelPart)part).setHidden(false));
    }

    private void unhideAllModelParts() {
        this.getContextModel().getPart().traverse().forEach(part -> ((ExpandedModelPart)part).setHidden(false));
    }

    @Environment(EnvType.CLIENT)
    public interface AnimationAngleAdjuster<T extends WardenEntity> {
        float apply(T warden, float tickDelta, float animationProgress);
    }

    @Environment(EnvType.CLIENT)
    public interface ModelPartVisibility<T extends WardenEntity, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M model);
    }
}
