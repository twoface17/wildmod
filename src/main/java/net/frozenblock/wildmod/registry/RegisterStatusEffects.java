package net.frozenblock.wildmod.registry;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.misc.StatusEffect;
import net.frozenblock.wildmod.misc.WildStatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegisterStatusEffects {
    public static final StatusEffect DARKNESS = new StatusEffect(StatusEffectCategory.HARMFUL, 2696993).setFactorCalculationDataSupplier(() -> new WildStatusEffectInstance.FactorCalculationData(22));

    public static void init() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(WildMod.MOD_ID, "darkness"), DARKNESS);
    }
}
