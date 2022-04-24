package frozenblock.wild.mod.render.entity;

import frozenblock.wild.mod.entity.AllayEntity;
import frozenblock.wild.mod.liukrastapi.NewEntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public final class WildEntityRendererRegistry {

    public static <E extends Entity> void register(EntityType<AllayEntity> entityType, NewEntityRendererFactory<E> entityRendererFactory) {
        WildEntityRendererRegistryImpl.register(entityType, entityRendererFactory);
    }

    private WildEntityRendererRegistry() {
    }
}
