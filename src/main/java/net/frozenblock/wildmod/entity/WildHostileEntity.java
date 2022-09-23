package net.frozenblock.wildmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.world.World;

public class WildHostileEntity extends HostileEntity {
    protected WildHostileEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean disablesShield() {
        return this.getMainHandStack().getItem() instanceof AxeItem;
    }

}
