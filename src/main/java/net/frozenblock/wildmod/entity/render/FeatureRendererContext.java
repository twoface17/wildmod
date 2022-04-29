package net.frozenblock.wildmod.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface FeatureRendererContext<T extends Entity, M extends EntityModel<T>> {
    M getModel();

    Identifier getTexture(T entity);
}
