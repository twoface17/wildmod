package net.frozenblock.wildmod.mixins;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.frozenblock.wildmod.liukrastapi.MathAddon;
import net.frozenblock.wildmod.liukrastapi.StatusEffectInstance;
import net.frozenblock.wildmod.registry.RegisterStatusEffects;
import net.frozenblock.wildmod.render.GameRenderer;
import net.frozenblock.wildmod.status_effects.StatusEffectFogModifier;
import net.frozenblock.wildmod.tags.BiomeTags;
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
    private static final List<StatusEffectFogModifier> FOG_MODIFIERS = Lists.newArrayList
            (new StatusEffectFogModifier.BlindnessFogModifier(), new StatusEffectFogModifier.DarknessFogModifier());
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

    @Nullable
    private static StatusEffectFogModifier getFogModifier(Entity entity, float tickDelta) {
        if (entity instanceof LivingEntity livingEntity) {
            return FOG_MODIFIERS.stream().filter((modifier) -> {
                return modifier.shouldApply(livingEntity, tickDelta);
            }).findFirst().orElse(null);
        } else {
            return null;
        }
    }

    @Inject(at = @At("TAIL"), method = "render")
    private static void render(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        float f;
        float g;
        float h;
        float r;
        float s;
        float t;
        if (cameraSubmersionType == CameraSubmersionType.WATER) {
            long l = Util.getMeasuringTimeMs();
            int i = world.getBiome(new BlockPos(camera.getPos())).value().getWaterFogColor();
            if (lastWaterFogColorUpdateTime < 0L) {
                waterFogColor = i;
                nextWaterFogColor = i;
                lastWaterFogColorUpdateTime = l;
            }

            int j = waterFogColor >> 16 & 255;
            int k = waterFogColor >> 8 & 255;
            int m = waterFogColor & 255;
            int n = nextWaterFogColor >> 16 & 255;
            int o = nextWaterFogColor >> 8 & 255;
            int p = nextWaterFogColor & 255;
            f = MathHelper.clamp((float) (l - lastWaterFogColorUpdateTime) / 5000.0F, 0.0F, 1.0F);
            g = MathHelper.lerp(f, (float) n, (float) j);
            h = MathHelper.lerp(f, (float) o, (float) k);
            float q = MathHelper.lerp(f, (float) p, (float) m);
            red = g / 255.0F;
            green = h / 255.0F;
            blue = q / 255.0F;
            if (waterFogColor != i) {
                waterFogColor = i;
                nextWaterFogColor = MathHelper.floor(g) << 16 | MathHelper.floor(h) << 8 | MathHelper.floor(q);
                lastWaterFogColorUpdateTime = l;
            }
        } else if (cameraSubmersionType == CameraSubmersionType.LAVA) {
            red = 0.6F;
            green = 0.1F;
            blue = 0.0F;
            lastWaterFogColorUpdateTime = -1L;
        } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
            red = 0.623F;
            green = 0.734F;
            blue = 0.785F;
            lastWaterFogColorUpdateTime = -1L;
            RenderSystem.clearColor(red, green, blue, 0.0F);
        } else {
            r = 0.25F + 0.75F * (float) viewDistance / 32.0F;
            r = 1.0F - (float) Math.pow((double) r, 0.25);
            Vec3d vec3d = world.getSkyColor(camera.getPos(), tickDelta);
            s = (float) vec3d.x;
            t = (float) vec3d.y;
            float u = (float) vec3d.z;
            float v = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(tickDelta) * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
            BiomeAccess biomeAccess = world.getBiomeAccess();
            Vec3d vec3d2 = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
            Vec3d vec3d3 = CubicSampler.sampleColor(
                    vec3d2,
                    (x, y, z) -> world.getDimensionEffects()
                            .adjustFogColor(Vec3d.unpackRgb(biomeAccess.getBiomeForNoiseGen(x, y, z).value().getFogColor()), v)
            );
            red = (float) vec3d3.getX();
            green = (float) vec3d3.getY();
            blue = (float) vec3d3.getZ();
            if (viewDistance >= 4) {
                f = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0F ? -1.0F : 1.0F;
                Vec3f vec3f = new Vec3f(f, 0.0F, 0.0F);
                h = camera.getHorizontalPlane().dot(vec3f);
                if (h < 0.0F) {
                    h = 0.0F;
                }

                if (h > 0.0F) {
                    float[] fs = world.getDimensionEffects().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta);
                    if (fs != null) {
                        h *= fs[3];
                        red = red * (1.0F - h) + fs[0] * h;
                        green = green * (1.0F - h) + fs[1] * h;
                        blue = blue * (1.0F - h) + fs[2] * h;
                    }
                }
            }

            red += (s - red) * r;
            green += (t - green) * r;
            blue += (u - blue) * r;
            f = world.getRainGradient(tickDelta);
            if (f > 0.0F) {
                g = 1.0F - f * 0.5F;
                h = 1.0F - f * 0.4F;
                red *= g;
                green *= g;
                blue *= h;
            }

            g = world.getThunderGradient(tickDelta);
            if (g > 0.0F) {
                h = 1.0F - g * 0.5F;
                red *= h;
                green *= h;
                blue *= h;
            }

            lastWaterFogColorUpdateTime = -1L;
        }

        r = ((float) camera.getPos().y - (float) world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
        StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);
        if (statusEffectFogModifier != null) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect()) instanceof StatusEffectInstance statusEffectInstance) {
                statusEffectInstance = (StatusEffectInstance) livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect());
                r = statusEffectFogModifier.applyColorModifier(livingEntity, statusEffectInstance, r, tickDelta);
            }
        }

        if (r < 1.0F && cameraSubmersionType != CameraSubmersionType.LAVA && cameraSubmersionType != CameraSubmersionType.POWDER_SNOW) {
            if (r < 0.0F) {
                r = 0.0F;
            }

            r *= r;
            red *= r;
            green *= r;
            blue *= r;
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

    /**
     * @author FrozenBlock
     * @reason darkness
     */
    @Overwrite
    public static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
        float tickDelta = 1F;
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        StatusEffectFogModifier.FogData fogData = new StatusEffectFogModifier.FogData(fogType);
        StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);
        if (cameraSubmersionType == CameraSubmersionType.LAVA) {
            if (entity.isSpectator()) {
                fogData.fogStart = -8.0F;
                fogData.fogEnd = viewDistance * 0.5F;
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                fogData.fogStart = 0.0F;
                fogData.fogEnd = 3.0F;
            } else {
                fogData.fogStart = 0.25F;
                fogData.fogEnd = 1.0F;
            }
        } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
            if (entity.isSpectator()) {
                fogData.fogStart = -8.0F;
                fogData.fogEnd = viewDistance * 0.5F;
            } else {
                fogData.fogStart = 0.0F;
                fogData.fogEnd = 2.0F;
            }
        } else if (statusEffectFogModifier != null) {
            LivingEntity livingEntity = (LivingEntity)entity;
            net.minecraft.entity.effect.StatusEffectInstance statusEffectInstance = livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect());
            if (statusEffectInstance instanceof StatusEffectInstance wildStatusEffectInstance) {
                statusEffectFogModifier.applyStartEndModifier(fogData, livingEntity, wildStatusEffectInstance, viewDistance, tickDelta);
            }
        } else if (cameraSubmersionType == CameraSubmersionType.WATER) {
            fogData.fogStart = -8.0F;
            fogData.fogEnd = 96.0F;
            if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
                fogData.fogEnd *= Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
                RegistryEntry<Biome> registryEntry = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
                if (registryEntry.isIn(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                    fogData.fogEnd *= 0.85F;
                }
            }

            if (fogData.fogEnd > viewDistance) {
                fogData.fogEnd = viewDistance;
                fogData.fogShape = FogShape.CYLINDER;
            }
        } else if (thickFog) {
            fogData.fogStart = viewDistance * 0.05F;
            fogData.fogEnd = Math.min(viewDistance, 192.0F) * 0.5F;
        } else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
            fogData.fogStart = 0.0F;
            fogData.fogEnd = viewDistance;
            fogData.fogShape = FogShape.CYLINDER;
        } else {
            float f = MathHelper.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
            fogData.fogStart = viewDistance - f;
            fogData.fogEnd = viewDistance;
            fogData.fogShape = FogShape.CYLINDER;
        }

        RenderSystem.setShaderFogStart(fogData.fogStart);
        RenderSystem.setShaderFogEnd(fogData.fogEnd);
        RenderSystem.setShaderFogShape(fogData.fogShape);
    }
}
