package net.frozenblock.wildmod.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;

public class WildDamageSource extends EntityDamageSource {
    public static DamageSource SONIC_BOOM(Entity entity) {
        return new WildDamageSource("sonic_boom", entity).setBypassesArmor().setUsesMagic();
    }

    protected WildDamageSource(String name, Entity entity) {
        super(name, entity);
    }
}
