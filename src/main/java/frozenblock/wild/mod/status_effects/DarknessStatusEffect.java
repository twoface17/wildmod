package frozenblock.wild.mod.status_effects;

import frozenblock.wild.mod.liukrastapi.MathAddon;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DarknessStatusEffect extends StatusEffect {
    public DarknessStatusEffect() {
        super(
                StatusEffectCategory.NEUTRAL,
                0x000000
        );
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        float pulse;
        pulse = (float) MathAddon.cutCos(MathAddon.time, 0, false);
        if (pulse < 0.5f && pulse > 0.4f) {
            World world = entity.getEntityWorld();
            BlockPos pos = entity.getBlockPos();
            if (!world.isClient) {
                world.playSound(
                        null,
                        pos,
                        RegisterSounds.ENTITY_WARDEN_CLOSE,
                        SoundCategory.AMBIENT,
                        5f,
                        1f
                );
            }
        }
    }
}
