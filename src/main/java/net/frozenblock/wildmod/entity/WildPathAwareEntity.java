package net.frozenblock.wildmod.entity;

import net.frozenblock.wildmod.event.EntityGameEventHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class WildPathAwareEntity extends PathAwareEntity {
    protected WildPathAwareEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
    }
}
