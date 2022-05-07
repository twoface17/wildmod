package net.frozenblock.wildmod.registry;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.liukrastapi.StatusEffectInstance;
import net.frozenblock.wildmod.status_effects.DarknessStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegisterStatusEffects {
    public static final StatusEffect DARKNESS = new net.frozenblock.wildmod.liukrastapi.StatusEffect(StatusEffectCategory.HARMFUL, 2696993).setFactorCalculationDataSupplier(() -> new StatusEffectInstance.FactorCalculationData(22));

    public static void RegisterStatusEffects() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(WildMod.MOD_ID, "darkness"), DARKNESS);
    }
}
