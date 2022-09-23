package net.frozenblock.wildmod.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface NewEntityRendererFactory<T extends Entity> extends EntityRendererFactory<T> {
    EntityRenderer<T> create(net.minecraft.client.render.entity.EntityRendererFactory.Context ctx);

    @Environment(EnvType.CLIENT)
    class Context extends EntityRendererFactory.Context {
        private final EntityRenderDispatcher renderDispatcher;
        private final ItemRenderer itemRenderer;
        private final BlockRenderManager blockRenderManager;
        private final HeldItemRenderer heldItemRenderer;
        private final ResourceManager resourceManager;
        private final EntityModelLoader modelLoader;
        private final TextRenderer textRenderer;

        public Context(EntityRenderDispatcher renderDispatcher, ItemRenderer itemRenderer, BlockRenderManager blockRenderManager, HeldItemRenderer heldItemRenderer, ResourceManager resourceManager, EntityModelLoader modelLoader, TextRenderer textRenderer) {
            super(renderDispatcher, itemRenderer, resourceManager, modelLoader, textRenderer);
            this.renderDispatcher = renderDispatcher;
            this.itemRenderer = itemRenderer;
            this.blockRenderManager = blockRenderManager;
            this.heldItemRenderer = heldItemRenderer;
            this.resourceManager = resourceManager;
            this.modelLoader = modelLoader;
            this.textRenderer = textRenderer;
        }

        public EntityRenderDispatcher getRenderDispatcher() {
            return this.renderDispatcher;
        }

        public ItemRenderer getItemRenderer() {
            return this.itemRenderer;
        }

        public BlockRenderManager getBlockRenderManager() {
            return this.blockRenderManager;
        }

        public HeldItemRenderer getHeldItemRenderer() {
            return this.heldItemRenderer;
        }

        public ResourceManager getResourceManager() {
            return this.resourceManager;
        }

        public EntityModelLoader getModelLoader() {
            return this.modelLoader;
        }

        public ModelPart getPart(EntityModelLayer layer) {
            return this.modelLoader.getModelPart(layer);
        }

        public TextRenderer getTextRenderer() {
            return this.textRenderer;
        }
    }
}