package net.frozenblock.wildmod.entity;

import net.frozenblock.wildmod.event.EntityGameEventHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class WildHostileEntity extends HostileEntity {
    protected WildHostileEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
    }
}
