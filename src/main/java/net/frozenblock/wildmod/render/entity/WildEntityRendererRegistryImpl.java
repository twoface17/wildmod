package net.frozenblock.wildmod.render.entity;

import net.frozenblock.wildmod.entity.render.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.HashMap;
import java.util.function.BiConsumer;

public final class WildEntityRendererRegistryImpl {

    private static HashMap<EntityType<?>, EntityRendererFactory<?>> map = new HashMap<>();
    private static BiConsumer<EntityType<?>, EntityRendererFactory<?>> handler = (type, function) -> map.put(type, function);

    public static <T extends Entity> void register(EntityType<? extends T> entityType, EntityRendererFactory<?> factory) {
        handler.accept(entityType, factory);
    }

    public static void setup(BiConsumer<EntityType<?>, EntityRendererFactory<?>> vanillaHandler) {
        map.forEach(vanillaHandler);
        handler = vanillaHandler;
    }
}
