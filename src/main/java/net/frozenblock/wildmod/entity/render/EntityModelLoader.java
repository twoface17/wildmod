package net.frozenblock.wildmod.entity.render;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.WildModClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class EntityModelLoader extends net.minecraft.client.render.entity.model.EntityModelLoader {
    private Map<EntityModelLayer, TexturedModelData> modelParts = ImmutableMap.of();

    public EntityModelLoader() {
    }

    public ModelPart getModelPart(EntityModelLayer layer) {
        TexturedModelData texturedModelData = (TexturedModelData)this.modelParts.get(layer);
        if (texturedModelData == null) {
            throw new IllegalArgumentException("No model for layer " + layer);
        } else {
            return texturedModelData.createModel();
        }
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return super.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
    }

    public void reload(ResourceManager manager) {
        this.modelParts = ImmutableMap.copyOf(EntityModels.getModels());
    }
}
