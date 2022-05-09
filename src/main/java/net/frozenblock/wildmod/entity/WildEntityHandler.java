package net.frozenblock.wildmod.entity;

import net.minecraft.world.entity.EntityHandler;

public interface WildEntityHandler<T> extends EntityHandler<T> {

    void updateLoadStatus(T entity);
}
