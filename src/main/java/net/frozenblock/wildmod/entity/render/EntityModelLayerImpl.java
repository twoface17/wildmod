package net.frozenblock.wildmod.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class EntityModelLayerImpl {
    public static final Map<EntityModelLayer, EntityModelLayerRegistry.TexturedModelDataProvider> PROVIDERS = new HashMap<>();

    private EntityModelLayerImpl() {
    }
}
