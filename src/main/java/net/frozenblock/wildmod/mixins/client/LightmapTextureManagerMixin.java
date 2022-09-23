package net.frozenblock.wildmod.mixins.client;

import net.frozenblock.wildmod.misc.WildStatusEffectInstance;
import net.frozenblock.wildmod.registry.RegisterStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @Shadow
    @Final
    private NativeImage image;

    @Shadow
    @Final
    private NativeImageBackedTexture texture;

    @Shadow
    private boolean dirty;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private float flickerIntensity;

    @Shadow
    @Final
    private GameRenderer renderer;

    public double time;

    private float getDarknessFactor(float tickDelta) {
        if (this.client.player.hasStatusEffect(RegisterStatusEffects.DARKNESS)) {
            assert this.client.player.getStatusEffect(RegisterStatusEffects.DARKNESS) != null;
            StatusEffectInstance statusEffectInstance = this.client.player.getStatusEffect(RegisterStatusEffects.DARKNESS);
            if (statusEffectInstance instanceof WildStatusEffectInstance wildStatusEffectInstance) {
                if (wildStatusEffectInstance.getFactorCalculationData().isPresent()) {
                    return wildStatusEffectInstance.getFactorCalculationData().get().lerp(this.client.player, tickDelta);
                }
            }
        }

        return 0.0F;
    }

    private float getDarkness(LivingEntity livingEntity, float factor, float delta) {
        return Math.max(0.0F, MathHelper.cos(((float) livingEntity.age - delta) * (float) Math.PI * 0.025F) * 0.45F * factor);
    }

    @Inject(at = @At("HEAD"), method = "update")
    public void update(float delta, CallbackInfo ci) {
        if (this.dirty) {
            this.dirty = false;
            this.client.getProfiler().push("lightTex");
            ClientWorld clientWorld = this.client.world;
            if (clientWorld != null) {
                float skyDarken = clientWorld.getStarBrightness(1.0F);
                float skyFlashTime = clientWorld.getLightningTicksLeft() > 0 ? 1.0F : skyDarken * 0.95F + 0.05F;
                float h = 1.0F; // needs game option ((Double)this.client.options.getDarknessEffectScale().getValue()).floatValue();
                float darkness = this.getDarknessFactor(delta) * h;
                assert this.client.player != null;
                float j = this.getDarkness(this.client.player, darkness, delta) * h;
                float k = this.client.player.getUnderwaterVisibility();
                float vision;
                if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                    vision = GameRenderer.getNightVisionStrength(this.client.player, delta);
                } else if (k > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    vision = k;
                } else {
                    vision = 0.0F;
                }

                Vec3f vec3f = new Vec3f(skyDarken, skyDarken, 1.0F);
                vec3f.lerp(new Vec3f(1.0F, 1.0F, 1.0F), 0.35F);
                float m = this.flickerIntensity + 1.5F;
                Vec3f vec3f2 = new Vec3f();

                for (int sky = 0; sky < 16; ++sky) {
                    for (int block = 0; block < 16; ++block) {
                        float skyGamma = getBrightness(clientWorld, sky) * skyFlashTime;
                        float blockGamma = getBrightness(clientWorld, block) * m;
                        float yGamma = blockGamma * ((blockGamma * 0.6F + 0.4F) * 0.6F + 0.4F);
                        float xzGamma = blockGamma * (blockGamma * blockGamma * 0.6F + 0.4F);
                        vec3f2.set(blockGamma, yGamma, xzGamma);
                        boolean bl = clientWorld.getDimensionEffects().shouldBrightenLighting();
                        float scale;
                        Vec3f vec3f4;
                        if (bl) {
                            vec3f2.lerp(new Vec3f(0.99F, 1.12F, 1.0F), 0.25F);
                            vec3f2.clamp(0.0F, 1.0F);
                        } else {
                            Vec3f vec3f3 = vec3f.copy();
                            vec3f3.scale(skyGamma);
                            vec3f2.add(vec3f3);
                            vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
                            if (this.renderer.getSkyDarkness(delta) > 0.0F) {
                                scale = this.renderer.getSkyDarkness(delta);
                                vec3f4 = vec3f2.copy();
                                vec3f4.multiplyComponentwise(0.7F, 0.6F, 0.6F);
                                vec3f2.lerp(vec3f4, scale);
                            }
                        }

                        if (vision > 0.0F) {
                            float gammaModifier = Math.max(vec3f2.getX(), Math.max(vec3f2.getY(), vec3f2.getZ()));
                            if (gammaModifier < 1.0F) {
                                scale = 1.0F / gammaModifier;
                                vec3f4 = vec3f2.copy();
                                vec3f4.scale(scale);
                                vec3f2.lerp(vec3f4, vision);
                            }
                        }

                        if (!bl) {
                            if (j > 0.0F) {
                                vec3f2.add(-j, -j, -j);
                            }

                            vec3f2.clamp(0.0F, 1.0F);
                        }

                        float gammaModifier = (float) this.client.options.gamma;
                        Vec3f vec3f5 = vec3f2.copy();
                        vec3f5.modify(this::easeOutQuart);
                        vec3f2.lerp(vec3f5, Math.max(0.0F, gammaModifier - darkness));
                        vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
                        vec3f2.clamp(0.0F, 1.0F);
                        vec3f2.scale(255.0F);
                        int x = (int) vec3f2.getX();
                        int y = (int) vec3f2.getY();
                        int z = (int) vec3f2.getZ();
                        this.image.setColor(block, sky, 0xFF000000 | z << 16 | y << 8 | x);
                    }
                }

                this.texture.upload();
                this.client.getProfiler().pop();
            }
        }
    }

    private float easeOutQuart(float x) {
        float f = 1.0F - x;
        return 1.0F - f * f * f * f;
    }

    private float getBrightness(ClientWorld world, int lightLevel) {
        float f = (float) lightLevel / 15.0F;
        float g = f / (4.0F - 3.0F * f);
        return MathHelper.lerp(world.getDimension().getBrightness(lightLevel), g, 1.0F);
    }
}
