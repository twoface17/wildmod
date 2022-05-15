package net.frozenblock.wildmod.mixins;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import net.frozenblock.wildmod.liukrastapi.MathAddon;
import net.frozenblock.wildmod.liukrastapi.StatusEffect;
import net.frozenblock.wildmod.registry.RegisterSounds;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.frozenblock.wildmod.liukrastapi.MathAddon;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static java.lang.Math.*;

@Mixin(LightmapTextureManager.class)
public abstract class LightmapTextureManagerMixin implements AutoCloseable {

    @Shadow @Final private NativeImage image;

    @Shadow @Final private NativeImageBackedTexture texture;

    @Shadow private boolean dirty;

    @Shadow @Final private MinecraftClient client;

    @Shadow private float flickerIntensity;

    @Shadow @Final private GameRenderer renderer;

    @Shadow @Final private Identifier textureIdentifier;
    @Shadow @Final public static int MAX_BLOCK_LIGHT_COORDINATE;

    @Shadow protected abstract float getBrightness(World world, int lightLevel);

    public double time;
    public double soundTime;
    public boolean shouldPlay=true;
    public int lastDark;
    public int lastDarkTime;

    @Override
    public void close() throws Exception {
        this.texture.close();
    }

    /**
     * @author FrozenBlock
     * @reason its different in 1.19
     */
    @Overwrite
    public void enable() {
        RenderSystem.setShaderTexture(2, this.textureIdentifier);
        this.client.getTextureManager().bindTexture(this.textureIdentifier);
        RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_LINEAR);
        RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private float getDarknessFactor(float delta) {
        if (this.client.player.hasStatusEffect(RegisterStatusEffects.DARKNESS)) {
            assert this.client.player.getStatusEffect(RegisterStatusEffects.DARKNESS) != null;
            StatusEffectInstance statusEffectInstance = this.client.player.getStatusEffect(RegisterStatusEffects.DARKNESS);
            if (statusEffectInstance instanceof net.frozenblock.wildmod.liukrastapi.StatusEffectInstance wildStatusEffectInstance) {
                if (wildStatusEffectInstance.getFactorCalculationData().isPresent()) {
                    return wildStatusEffectInstance.getFactorCalculationData().get().lerp(delta);
                }
            }
        }

        return 0.0F;
    }

    private float getDarkness(LivingEntity entity, float factor, float delta) {
        float f = 0.45F * factor;
        return Math.max(0.0F, MathAddon.cos(((float)entity.age - delta) * 3.1415927F * 0.025F) * f);
    }

    @Inject(at = @At("HEAD"), method = "update")
    public void update(float delta, CallbackInfo ci) {
        if (this.dirty) {
            this.dirty = false;
            this.client.getProfiler().push("lightTex");
            ClientWorld clientWorld = this.client.world;
            if (clientWorld != null) {
                float f = clientWorld.getStarBrightness(1.0F);
                float g;
                if (clientWorld.getLightningTicksLeft() > 0) {
                    g = 1.0F;
                } else {
                    g = f * 0.95F + 0.05F;
                }

                float h = 1.0F; // needs game option ((Double)this.client.options.getDarknessEffectScale().getValue()).floatValue();
                float i = this.getDarknessFactor(delta) * h;
                float j = this.getDarkness(this.client.player, i, delta) * h;
                float k = this.client.player.getUnderwaterVisibility();
                float l;
                if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                    l = GameRenderer.getNightVisionStrength(this.client.player, delta);
                } else if (k > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    l = k;
                } else {
                    l = 0.0F;
                }

                Vec3f vec3f = new Vec3f(f, f, 1.0F);
                vec3f.lerp(new Vec3f(1.0F, 1.0F, 1.0F), 0.35F);
                float m = this.flickerIntensity + 1.5F;
                Vec3f vec3f2 = new Vec3f();

                for(int n = 0; n < 16; ++n) {
                    for(int o = 0; o < 16; ++o) {
                        float p = getBrightness(clientWorld, n) * g;
                        float q = getBrightness(clientWorld, o) * m;
                        float s = q * ((q * 0.6F + 0.4F) * 0.6F + 0.4F);
                        float t = q * (q * q * 0.6F + 0.4F);
                        vec3f2.set(q, s, t);
                        boolean bl = clientWorld.getDimensionEffects().shouldBrightenLighting();
                        float u;
                        Vec3f vec3f4;
                        if (bl) {
                            vec3f2.lerp(new Vec3f(0.99F, 1.12F, 1.0F), 0.25F);
                            vec3f2.clamp(0.0F, 1.0F);
                        } else {
                            Vec3f vec3f3 = vec3f.copy();
                            vec3f3.scale(p);
                            vec3f2.add(vec3f3);
                            vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
                            if (this.renderer.getSkyDarkness(delta) > 0.0F) {
                                u = this.renderer.getSkyDarkness(delta);
                                vec3f4 = vec3f2.copy();
                                vec3f4.multiplyComponentwise(0.7F, 0.6F, 0.6F);
                                vec3f2.lerp(vec3f4, u);
                            }
                        }

                        float v;
                        if (l > 0.0F) {
                            v = Math.max(vec3f2.getX(), Math.max(vec3f2.getY(), vec3f2.getZ()));
                            if (v < 1.0F) {
                                u = 1.0F / v;
                                vec3f4 = vec3f2.copy();
                                vec3f4.scale(u);
                                vec3f2.lerp(vec3f4, l);
                            }
                        }

                        if (!bl) {
                            if (j > 0.0F) {
                                vec3f2.add(-j, -j, -j);
                            }

                            vec3f2.clamp(0.0F, 1.0F);
                        }

                        v = ((Double)this.client.options.gamma).floatValue();
                        Vec3f vec3f5 = vec3f2.copy();
                        vec3f5.modify(this::easeOutQuart);
                        vec3f2.lerp(vec3f5, Math.max(0.0F, v - i));
                        vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
                        vec3f2.clamp(0.0F, 1.0F);
                        vec3f2.scale(255.0F);
                        int w = 255;
                        int x = (int)vec3f2.getX();
                        int y = (int)vec3f2.getY();
                        int z = (int)vec3f2.getZ();
                        this.image.setColor(o, n, -16777216 | z << 16 | y << 8 | x);
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
}
