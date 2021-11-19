package frozenblock.wild.mod.status_effects;

import net.minecraft.command.argument.StatusEffectArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class DarknessStatusEffect extends StatusEffect {
    public DarknessStatusEffect() {
        super(
                StatusEffectCategory.NEUTRAL,
                0x000000
        );
    }
}
