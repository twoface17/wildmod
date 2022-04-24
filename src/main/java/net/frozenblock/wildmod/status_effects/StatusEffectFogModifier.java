package net.frozenblock.wildmod.status_effects;

import net.frozenblock.wildmod.registry.RegisterStatusEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public
interface StatusEffectFogModifier {
    StatusEffect getStatusEffect();

    public default void applyStartEndModifier(FogData fogData, LivingEntity livingEntity, net.frozenblock.wildmod.liukrastapi.StatusEffectInstance statusEffectInstance, float f, float g) {
        if (statusEffectInstance.getFactorCalculationData().isPresent()) {
            float h = MathHelper.lerp(statusEffectInstance.getFactorCalculationData().get().lerp(g), f, 15.0F);
            fogData.fogStart = fogData.fogType == BackgroundRenderer.FogType.FOG_SKY ? 0.0F : h * 0.75F;
            fogData.fogEnd = h;
        }
    }

    default boolean shouldApply(LivingEntity livingEntity, float f) {
        return livingEntity.hasStatusEffect(this.getStatusEffect());
    }

    default float applyColorModifier(LivingEntity livingEntity, StatusEffectInstance statusEffectInstance, float f, float g) {
        StatusEffectInstance statusEffectInstance2 = livingEntity.getStatusEffect(this.getStatusEffect());
        if (statusEffectInstance2 != null) {
            if (statusEffectInstance2.getDuration() < 20) {
                f = 1.0F - (float)statusEffectInstance2.getDuration() / 20.0F;
            } else {
                f = 0.0F;
            }
        }

        return f;
    }

    default void applyStartEndModifier(FogData fogData, LivingEntity livingEntity, StatusEffectInstance statusEffectInstance, float viewDistance, float f) {
        float h = MathHelper.lerp(Math.min(1.0F, (float)statusEffectInstance.getDuration() / 20.0F), f, 5.0F);
        if (fogData.fogType == BackgroundRenderer.FogType.FOG_SKY) {
            fogData.fogStart = 0.0F;
            fogData.fogEnd = h * 0.8F;
        } else {
            fogData.fogStart = h * 0.25F;
            fogData.fogEnd = h;
        }
    };

    @Environment(EnvType.CLIENT)
    static class FogData {
        public final BackgroundRenderer.FogType fogType;
        public float fogStart;
        public float fogEnd;
        public FogShape field_38342;

        public FogData(BackgroundRenderer.FogType fogType) {
            this.field_38342 = FogShape.SPHERE;
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

        public StatusEffect getStatusEffect() {
            return StatusEffects.BLINDNESS;
        }

        public void applyStartEndModifier(FogData fogData, LivingEntity livingEntity, net.frozenblock.wildmod.liukrastapi.StatusEffectInstance statusEffectInstance, float f, float g) {
            float h = MathHelper.lerp(Math.min(1.0F, (float)statusEffectInstance.getDuration() / 20.0F), f, 5.0F);
            if (fogData.fogType == BackgroundRenderer.FogType.FOG_SKY) {
                fogData.fogStart = 0.0F;
                fogData.fogEnd = h * 0.8F;
            } else {
                fogData.fogStart = h * 0.25F;
                fogData.fogEnd = h;
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

        public void applyStartEndModifier(FogData fogData, LivingEntity livingEntity, net.frozenblock.wildmod.liukrastapi.StatusEffectInstance statusEffectInstance, float f, float g) {
            if (statusEffectInstance.getFactorCalculationData().isPresent()) {
                float h = MathHelper.lerp(statusEffectInstance.getFactorCalculationData().get().lerp(g), f, 15.0F);
                fogData.fogStart = fogData.fogType == BackgroundRenderer.FogType.FOG_SKY ? 0.0F : h * 0.75F;
                fogData.fogEnd = h;
            }
        }

        public float applyColorModifier(LivingEntity livingEntity, net.frozenblock.wildmod.liukrastapi.StatusEffectInstance statusEffectInstance, float f, float g) {
            return statusEffectInstance.getFactorCalculationData().isEmpty() ? 0.0F : 1.0F - statusEffectInstance.getFactorCalculationData().get().lerp(g);
        }
    }
}