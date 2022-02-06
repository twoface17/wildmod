package frozenblock.wild.mod.status_effects;

import frozenblock.wild.mod.liukrastapi.MathAddon;
import frozenblock.wild.mod.registry.RegisterSounds;
import frozenblock.wild.mod.registry.RegisterStatusEffects;
import net.minecraft.client.MinecraftClient;
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
    public double time;

    public void tick(MinecraftClient client) {
        if(client.player.hasStatusEffect(RegisterStatusEffects.DARKNESS)) {
            time = time + 0.075/2;
            MathAddon.time = time;
        } else {
            time = 0;
        }
    }
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        float pulse;
        pulse = (float) MathAddon.cutCos(time, 0, false) * 1.5f;
        if(pulse < 1.51f && pulse > 1.45f) {
            World world = entity.getEntityWorld();
            BlockPos pos = entity.getBlockPos();
            if(!world.isClient) {
                world.playSound(
                        null,
                        pos,
                        RegisterSounds.ENTITY_WARDEN_CLOSE,
                        SoundCategory.AMBIENT,
                        1f,
                        1f
                );
            }
        }
    }
}
