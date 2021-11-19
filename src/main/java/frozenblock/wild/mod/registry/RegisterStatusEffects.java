package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.status_effects.DarknessStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegisterStatusEffects {
    public static final StatusEffect DARKNESS = new DarknessStatusEffect();

    public static void RegisterStatusEffects() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(WildMod.MOD_ID, "darkness"), DARKNESS);
    }
}
