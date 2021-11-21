package frozenblock.wild.mod.status_effects;

import frozenblock.wild.mod.liukrastapi.UpdaterNum;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.command.argument.StatusEffectArgumentType;
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
        float math;
        math = (float) UpdaterNum.cuttedCos(UpdaterNum.time, 1, 0, false);
        if(math == 0.5f) {
            World world = entity.getEntityWorld();
            BlockPos pos = entity.getBlockPos();
            if(!world.isClient) {
                world.playSound(
                        null,
                        pos,
                        RegisterSounds.NEARBY_CLOSE_EVENT,
                        SoundCategory.AMBIENT,
                        1f,
                        1f
                );
            }
        }
    }
}
