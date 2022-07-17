package net.frozenblock.wildmod.render.entity;

import net.frozenblock.wildmod.liukrastapi.NewEntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.HashMap;
import java.util.function.BiConsumer;

public final class WildEntityRendererRegistryImpl {

    private static final HashMap<EntityType<?>, NewEntityRendererFactory<?>> map = new HashMap<>();
    private static BiConsumer<EntityType<?>, NewEntityRendererFactory<?>> handler = (type, function) -> map.put(type, function);

    public static <T extends Entity> void register(EntityType<? extends T> entityType, NewEntityRendererFactory<?> factory) {
        handler.accept(entityType, factory);
    }

    public static void setup(BiConsumer<EntityType<?>, NewEntityRendererFactory<?>> vanillaHandler) {
        map.forEach(vanillaHandler);
        handler = vanillaHandler;
    }
}
