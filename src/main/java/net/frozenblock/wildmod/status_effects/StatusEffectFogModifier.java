package net.frozenblock.wildmod.status_effects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.liukrastapi.StatusEffectInstance;
import net.frozenblock.wildmod.registry.RegisterStatusEffects;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public
interface StatusEffectFogModifier {
    StatusEffect getStatusEffect();

    void applyStartEndModifier(FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta);

    default boolean shouldApply(LivingEntity entity, float tickDelta) {
        return entity.hasStatusEffect(this.getStatusEffect());
    }

    default float applyColorModifier(LivingEntity entity, StatusEffectInstance effect, float f, float tickDelta) {
        StatusEffectInstance statusEffectInstance = (StatusEffectInstance) entity.getStatusEffect(this.getStatusEffect());
        if (statusEffectInstance != null) {
            if (statusEffectInstance.getDuration() < 20) {
                f = 1.0F - (float)statusEffectInstance.getDuration() / 20.0F;
            } else {
                f = 0.0F;
            }
        }

        return f;
    }

    @Environment(EnvType.CLIENT)
    static class FogData {
        public final BackgroundRenderer.FogType fogType;
        public float fogStart;
        public float fogEnd;
        //public FogShape fogShape = FogShape.SPHERE;

        public FogData(BackgroundRenderer.FogType fogType) {
            this.fogType = fogType;
        }
    }

    @Environment(EnvType.CLIENT)
    public static enum FogType {
        FOG_SKY,
        FOG_TERRAIN;

        private FogType() {
        }
    }

    @Environment(EnvType.CLIENT)
    static class BlindnessFogModifier implements StatusEffectFogModifier {
        public BlindnessFogModifier() {
        }

        @Override
        public StatusEffect getStatusEffect() {
            return StatusEffects.BLINDNESS;
        }

        @Override
        public void applyStartEndModifier(
                FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta
        ) {
            float f = MathHelper.lerp(Math.min(1.0F, (float)effect.getDuration() / 20.0F), viewDistance, 5.0F);
            if (fogData.fogType == BackgroundRenderer.FogType.FOG_SKY) {
                fogData.fogStart = 0.0F;
                fogData.fogEnd = f * 0.8F;
            } else {
                fogData.fogStart = f * 0.25F;
                fogData.fogEnd = f;
            }

        }
    }

    @Environment(EnvType.CLIENT)
    static class DarknessFogModifier implements StatusEffectFogModifier {
        public DarknessFogModifier() {
        }

        public StatusEffect getStatusEffect() {
            return RegisterStatusEffects.DARKNESS;
        }

        @Override
        public void applyStartEndModifier(
                FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta
        ) {
            if (!effect.getFactorCalculationData().isEmpty()) {
                float f = MathHelper.lerp(effect.getFactorCalculationData().get().lerp(tickDelta), viewDistance, 15.0F);
                fogData.fogStart = fogData.fogType == BackgroundRenderer.FogType.FOG_SKY ? 0.0F : f * 0.75F;
                fogData.fogEnd = f;
            }
        }

        @Override
        public float applyColorModifier(LivingEntity entity, StatusEffectInstance effect, float f, float tickDelta) {
            return effect.getFactorCalculationData().isEmpty() ? 0.0F : 1.0F - effect.getFactorCalculationData().get().lerp(tickDelta);
        }
    }
}