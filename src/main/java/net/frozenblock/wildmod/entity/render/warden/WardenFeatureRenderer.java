package net.frozenblock.wildmod.entity.render.warden;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.misc.ExpandedModelPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.List;
import java.util.function.BiFunction;

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

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float tickDelta, float animationProgress, float yaw, float pitch) {
        if (!wardenEntity.isInvisible()) {
            this.updateModelPartVisibility();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(ENTITY_TRANSLUCENT_EMISSIVE.apply(this.texture, true));
            this.getContextModel().render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(wardenEntity, 0.0F), 1.0F, 1.0F, 1.0F, this.animationAngleAdjuster.apply(wardenEntity, tickDelta, animationProgress));
            this.unhideAllModelParts();
        }
    }

    private void updateModelPartVisibility() {
        List<ModelPart> list = this.modelPartVisibility.getPartsToDraw(this.getContextModel());
        //this.getContextModel().getPart().traverse().forEach(part -> part.visible = false);
        this.getContextModel().getPart().traverse().forEach(part -> ((ExpandedModelPart) part).setHidden(true));
        //list.forEach(part -> part.visible = true);
        list.forEach(part -> ((ExpandedModelPart) part).setHidden(false));
    }

    private void unhideAllModelParts() {
        //this.getContextModel().getPart().traverse().forEach(part -> part.visible = true);
        this.getContextModel().getPart().traverse().forEach(part -> ((ExpandedModelPart) part).setHidden(false));
    }

    @Environment(EnvType.CLIENT)
    public interface AnimationAngleAdjuster<T extends WardenEntity> {
        float apply(T warden, float tickDelta, float animationProgress);
    }

    @Environment(EnvType.CLIENT)
    public interface ModelPartVisibility<T extends WardenEntity, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M model);
    }

    private static final BiFunction<Identifier, Boolean, RenderLayer> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(
            ((texture, affectsOutline) -> {
                RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                        .shader(RenderPhase.EYES_SHADER)
                        .texture(new RenderPhase.Texture(texture, false, false))
                        .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                        .cull(RenderPhase.DISABLE_CULLING)
                        .writeMaskState(RenderPhase.COLOR_MASK)
                        .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                        .build(affectsOutline);
                return of(
                        "entity_translucent_emissive",
                        VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                        VertexFormat.DrawMode.QUADS,
                        256,
                        true,
                        true,
                        multiPhaseParameters
                );
            })
    );

    private static RenderLayer.MultiPhase of(
            String name,
            VertexFormat vertexFormat,
            VertexFormat.DrawMode drawMode,
            int expectedBufferSize,
            boolean hasCrumbling,
            boolean translucent,
            RenderLayer.MultiPhaseParameters phases
    ) {
        return new RenderLayer.MultiPhase(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
    }
}
