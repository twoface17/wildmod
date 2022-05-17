package net.frozenblock.wildmod.mixins;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.frozenblock.wildmod.liukrastapi.StatusEffectInstance;
import net.frozenblock.wildmod.registry.RegisterStatusEffects;
import net.frozenblock.wildmod.render.GameRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Shadow
    private static float red;
    @Shadow
    private static float green;
    @Shadow
    private static float blue;
    @Shadow
    private static int waterFogColor = -1;
    @Shadow
    private static int nextWaterFogColor = -1;
    @Shadow
    private static long lastWaterFogColorUpdateTime = -1L;

    @Inject(at = @At("TAIL"), method = "render")
    private static void render(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        float f;
        float g;
        float h;
        float s;

        float t = ((float)camera.getPos().y - (float)world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).hasStatusEffect(StatusEffects.BLINDNESS)) {
            int y = ((LivingEntity)camera.getFocusedEntity()).getStatusEffect(StatusEffects.BLINDNESS).getDuration();
            if (y < 20) {
                t = 1.0F - (float)y / 20.0F;
            } else {
                t = 0.0F;
            }
        }

        if (t < 1.0F && cameraSubmersionType != CameraSubmersionType.LAVA && cameraSubmersionType != CameraSubmersionType.POWDER_SNOW) {
            if (t < 0.0F) {
                t = 0.0F;
            }

            t *= t;
            red *= t;
            green *= t;
            blue *= t;
        }

        if (skyDarkness > 0.0F) {
            red = red * (1.0F - skyDarkness) + red * 0.7F * skyDarkness;
            green = green * (1.0F - skyDarkness) + green * 0.6F * skyDarkness;
            blue = blue * (1.0F - skyDarkness) + blue * 0.6F * skyDarkness;
        }

        if (cameraSubmersionType == CameraSubmersionType.WATER) {
            if (entity instanceof ClientPlayerEntity) {
                s = ((ClientPlayerEntity) entity).getUnderwaterVisibility();
            } else {
                s = 1.0F;
            }
        } else if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            s = GameRenderer.getNightVisionStrength((LivingEntity) entity, tickDelta);
        } else {
            s = 0.0F;
        }

        if (red != 0.0F && green != 0.0F && blue != 0.0F) {
            t = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
            red = red * (1.0F - s) + red * t * s;
            green = green * (1.0F - s) + green * t * s;
            blue = blue * (1.0F - s) + blue * t * s;
        }

        RenderSystem.clearColor(red, green, blue, 0.0F);
    }

    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
        float tickDelta = 1F;
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        FogShape fogShape = FogShape.SPHERE;
        float fogStart = 1.0F;
        float fogEnd = 1.0F;
        if (cameraSubmersionType == CameraSubmersionType.LAVA) {
            if (entity.isSpectator()) {
                fogStart = -8.0F;
                fogEnd = viewDistance * 0.5F;
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                fogStart = 0.0F;
                fogEnd = 3.0F;
            } else {
                fogStart = 0.25F;
                fogEnd = 1.0F;
            }
        } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
            if (entity.isSpectator()) {
                fogStart = -8.0F;
                fogEnd = viewDistance * 0.5F;
            } else {
                fogStart = 0.0F;
                fogEnd = 2.0F;
            }
        } else if (entity instanceof LivingEntity livingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
            int i = Objects.requireNonNull(livingEntity.getStatusEffect(StatusEffects.BLINDNESS)).getDuration();
            float h = MathHelper.lerp(Math.min(1.0F, (float) i / 20.0F), viewDistance, 5.0F);
            if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
                fogStart = 0.0F;
                fogEnd = h * 0.8F;
            } else {
                fogStart = cameraSubmersionType == CameraSubmersionType.WATER ? -4.0F : h * 0.25F;
                fogEnd = h;
            }
        } else if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(RegisterStatusEffects.DARKNESS)) {
            net.minecraft.entity.effect.StatusEffectInstance statusEffectInstance = livingEntity.getStatusEffect(RegisterStatusEffects.DARKNESS);
            if (statusEffectInstance instanceof StatusEffectInstance wildStatusEffectInstance) {
                if (!wildStatusEffectInstance.getFactorCalculationData().isEmpty()) {
                    int i = Objects.requireNonNull(livingEntity.getStatusEffect(RegisterStatusEffects.DARKNESS)).getDuration();
                    float f = MathHelper.lerp(Math.min(1.0F, (float) i / 20.0F), viewDistance, 5.0F);
                    if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
                        fogStart = 0.0F;
                        fogEnd = f * 0.8F;
                    } else {
                        fogStart = f * 0.25F;
                        fogEnd = f;
                    }
                }
            }
        } else if (cameraSubmersionType == CameraSubmersionType.WATER) {
            fogStart = -8.0F;
            fogEnd = 96.0F;
            if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
                fogEnd *= Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
                RegistryEntry<Biome> registryEntry = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
                if (Biome.getCategory(registryEntry) == Biome.Category.SWAMP) {
                    fogEnd *= 0.85F;
                }
            }

            if (fogEnd > viewDistance) {
                fogEnd = viewDistance;
                fogShape = FogShape.CYLINDER;
            }
        } else if (thickFog) {
            fogStart = viewDistance * 0.05F;
            fogEnd = Math.min(viewDistance, 192.0F) * 0.5F;
        } else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
            fogStart = 0.0F;
            fogEnd = viewDistance;
            fogShape = FogShape.CYLINDER;
        } else {
            float j = MathHelper.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
            fogStart = viewDistance - j;
            fogEnd = viewDistance;
            fogShape = FogShape.CYLINDER;
        }
        if (entity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(RegisterStatusEffects.DARKNESS)) {

        }

        RenderSystem.setShaderFogStart(fogStart);
        RenderSystem.setShaderFogEnd(fogEnd);
        RenderSystem.setShaderFogShape(fogShape);
    }
}
