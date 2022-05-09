package net.frozenblock.wildmod.entity;

import net.minecraft.entity.damage.DamageSource;

public class WildDamageSource extends DamageSource {
    public static final DamageSource SONIC_BOOM = new WildDamageSource("sonic_boom").setBypassesArmor().setUsesMagic();

    protected WildDamageSource(String name) {
        super(name);
    }
}
