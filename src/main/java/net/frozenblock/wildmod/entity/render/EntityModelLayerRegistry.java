package net.frozenblock.wildmod.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.mixins.EntityModelLayersAccessor;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class EntityModelLayerRegistry {
    /**
     * Registers an entity model layer and registers a provider for a {@linkplain TexturedModelData}.
     *
     * @param modelLayer the entity model layer
     * @param provider the provider for the textured model data
     */
    public static void registerModelLayer(EntityModelLayer modelLayer, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        Objects.requireNonNull(modelLayer, "EntityModelLayer cannot be null");
        Objects.requireNonNull(provider, "TexturedModelDataProvider cannot be null");

        if (EntityModelLayerImpl.PROVIDERS.putIfAbsent(modelLayer, provider) != null) {
            throw new IllegalArgumentException(String.format("Cannot replace registration for entity model layer \"%s\"", modelLayer));
        }

        EntityModelLayersAccessor.getLayers().add(modelLayer);
    }

    private EntityModelLayerRegistry() {
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    public interface TexturedModelDataProvider {
        /**
         * Creates the textured model data for use in a {@link EntityModelLayer}.
         *
         * @return the textured model data for the entity model layer.
         */
        TexturedModelData createModelData();
    }
}
