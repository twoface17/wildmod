package net.frozenblock.wildmod.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class CompositeEntityModel<E extends Entity> extends EntityModel<E> {
    public CompositeEntityModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public CompositeEntityModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.getParts().forEach(part -> part.render(matrices, vertices, light, overlay, red, green, blue, alpha));
    }

    public abstract Iterable<ModelPart> getParts();
}
