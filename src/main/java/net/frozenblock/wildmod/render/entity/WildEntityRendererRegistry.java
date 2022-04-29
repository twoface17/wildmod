package net.frozenblock.wildmod.render.entity;

import net.frozenblock.wildmod.entity.AllayEntity;
import net.frozenblock.wildmod.entity.render.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public final class WildEntityRendererRegistry {

    public static <E extends Entity> void register(EntityType<?> entityType, EntityRendererFactory<E> entityRendererFactory) {
        WildEntityRendererRegistryImpl.register(entityType, entityRendererFactory);
    }

    private WildEntityRendererRegistry() {
    }
}
