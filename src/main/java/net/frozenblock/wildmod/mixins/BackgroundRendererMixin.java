package net.frozenblock.wildmod.mixins;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.frozenblock.wildmod.liukrastapi.AdvancedMath;
import net.frozenblock.wildmod.liukrastapi.StatusEffectInstance;
import net.frozenblock.wildmod.registry.RegisterStatusEffects;
import net.frozenblock.wildmod.render.WildGameRenderer;
import net.frozenblock.wildmod.status_effects.StatusEffectFogModifier;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
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
import org.spongepowered.asm.mixin.Mixin;
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

    private static StatusEffectFogModifier getFogModifier(Entity entity, float f) {
        if (entity instanceof LivingEntity livingEntity) {
            return FOG_MODIFIERS.stream().filter((statusEffectFogModifier) -> {
                return statusEffectFogModifier.shouldApply(livingEntity, f);
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
            r = 1.0F - (float) Math.pow(r, 0.25);
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
            s = WildGameRenderer.getNightVisionStrength((LivingEntity) entity, tickDelta);
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
        float f = 0;
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        StatusEffectFogModifier.FogData fogData = new StatusEffectFogModifier.FogData(fogType);
        StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, f);
        float y;
        if (cameraSubmersionType == CameraSubmersionType.WATER) {
            y = 192.0F;
            if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
                y *= Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
                RegistryEntry<Biome> biome = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
                Biome.Category category = Biome.getCategory(biome);
                if (category == Biome.Category.SWAMP) {
                    y *= 0.85F;
                }
            }

            RenderSystem.setShaderFogStart(-8.0F);
            RenderSystem.setShaderFogEnd(y * 0.5F);
        } else {
            float ab;
            if (cameraSubmersionType == CameraSubmersionType.LAVA) {
                if (entity.isSpectator()) {
                    y = -8.0F;
                    ab = viewDistance * 0.5F;
                } else if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                    y = 0.0F;
                    ab = 3.0F;
                } else {
                    y = 0.25F;
                    ab = 1.0F;
                }
            } else if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
                int m = Objects.requireNonNull(((LivingEntity) entity).getStatusEffect(StatusEffects.BLINDNESS)).getDuration();
                float n = MathHelper.lerp(Math.min(1.0F, (float) m / 20.0F), viewDistance, 5.0F);
                if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
                    y = 0.0F;
                    ab = n * 0.8F;
                } else {
                    y = n * 0.25F;
                    ab = n;
                }
            } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
                if (entity.isSpectator()) {
                    y = -8.0F;
                    ab = viewDistance * 0.5F;
                } else {
                    y = 0.0F;
                    ab = 2.0F;
                }
            } else if (thickFog) {
                y = viewDistance * 0.05F;
                ab = Math.min(viewDistance, 192.0F) * 0.5F;
            } else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
                y = 0.0F;
                ab = viewDistance;
            } else {
                y = (viewDistance * 0.75f);
                ab = viewDistance;
            }
            float math;

            if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(RegisterStatusEffects.DARKNESS)) {
                float offset = 0.5f;
                float multiplier = viewDistance * 1.4f;
                float equation = (float) AdvancedMath.cutCos(AdvancedMath.time, offset, true);
                math = (equation * multiplier) - offset * (multiplier);
            } else {
                math = 0;
            }

            y = y - math;
            ab = ab - math;

            RenderSystem.setShaderFogStart(y);
            RenderSystem.setShaderFogEnd(ab);
            RenderSystem.setShaderFogColor(BackgroundRendererMixin.red, BackgroundRendererMixin.green, BackgroundRendererMixin.blue);
        }
    }
}
