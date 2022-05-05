package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.liukrastapi.MathAddon;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static java.lang.Math.*;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin implements AutoCloseable {

    @Shadow @Final private NativeImage image;

    @Shadow @Final private NativeImageBackedTexture texture;

    @Shadow private boolean dirty;

    @Shadow @Final private MinecraftClient client;

    @Shadow private float flickerIntensity;

    @Shadow @Final private GameRenderer renderer;

    public double time;
    public double soundTime;
    public boolean shouldPlay=true;
    public int lastDark;
    public int lastDarkTime;

    @Override
    public void close() throws Exception {
        this.texture.close();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        assert this.client.player != null;
        if(this.client.player.hasStatusEffect(RegisterStatusEffects.DARKNESS)) {
            time = time + 0.075/2;
            ++soundTime;
            MathAddon.time = time;
            int darkTime = Objects.requireNonNull(this.client.player.getStatusEffect(RegisterStatusEffects.DARKNESS)).getDuration();
            int angerLevel = Objects.requireNonNull(this.client.player.getStatusEffect(RegisterStatusEffects.DARKNESS)).getAmplifier();
            if (angerLevel!=lastDark) {
                lastDark=angerLevel;
                shouldPlay=true;
            }
            if (lastDarkTime<darkTime || angerLevel==3) {
                shouldPlay=true;
            }
            double soundTimer = Math.cos(((soundTime+40)*PI)/80);
            if (soundTimer == -1 && shouldPlay) {
                if (angerLevel == 0) {
                    shouldPlay=false;
                    double a = random() * 2 * PI;
                    double r = sqrt(16) * sqrt(random());
                    int x = (int) (r * cos(a));
                    int z = (int) (r * sin(a));
                    BlockPos play = this.client.player.getBlockPos().add(x, 0, z);
                    assert this.client.world != null;
                    this.client.world.playSound(this.client.player, play, RegisterSounds.ENTITY_WARDEN_NEARBY_CLOSE, SoundCategory.AMBIENT, 0.2F, 1F);
                } else if (angerLevel == 1) {
                    shouldPlay=false;
                    double a = random() * 2 * PI;
                    double r = sqrt(12) * sqrt(random());
                    int x = (int) (r * cos(a));
                    int z = (int) (r * sin(a));
                    BlockPos play = this.client.player.getBlockPos().add(x, 0, z);
                    assert this.client.world != null;
                    this.client.world.playSound(this.client.player, play, RegisterSounds.ENTITY_WARDEN_NEARBY_CLOSER, SoundCategory.AMBIENT, 0.4F, 1F);
                } else if (angerLevel == 2) {
                    shouldPlay=false;
                    double a = random() * 2 * PI;
                    double r = sqrt(8) * sqrt(random());
                    int x = (int) (r * cos(a));
                    int z = (int) (r * sin(a));
                    BlockPos play = this.client.player.getBlockPos().add(x, 0, z);
                    assert this.client.world != null;
                    this.client.world.playSound(this.client.player, play, RegisterSounds.ENTITY_WARDEN_NEARBY_CLOSEST, SoundCategory.AMBIENT, 0.6F, 1F);
                } else if (angerLevel == 3) { //WARDEN DARKNESS
                    shouldPlay=false;
                    /*double a = random() * 2 * PI;
                    double r = sqrt(16) * sqrt(random());
                    int x = (int) (r * cos(a));
                    int z = (int) (r * sin(a));
                    BlockPos play = this.client.player.getBlockPos().add(x, 0, z);
                    assert this.client.world != null;
                    this.client.world.playSound(this.client.player, play, RegisterSounds.ENTITY_WARDEN_CLOSER, SoundCategory.AMBIENT, 0.1F, 1F);*/
                }
            }
            lastDarkTime=darkTime;
        } else {
            time = 0;
            soundTime=0;
            shouldPlay=true;
        }
    }

    private float getDarknessFactor(float f) {
        assert this.client.player != null;
        if (this.client.player.hasStatusEffect(RegisterStatusEffects.DARKNESS)) {
            StatusEffectInstance statusEffectInstance = this.client.player.getStatusEffect(RegisterStatusEffects.DARKNESS);
        }

        return 0.0F;
    }

    private float getDarkness(LivingEntity livingEntity, float f, float g) {
        float h = 0.45F * f;
        return Math.max(0.0F, MathHelper.cos(((float)livingEntity.age - g) * 3.1415927F * 0.025F) * h);
    }

    @Inject(at = @At("HEAD"), method = "update")
    public void update(float delta, CallbackInfo ci) {
        if (this.dirty) {
            this.dirty = false;
            this.client.getProfiler().push("lightTex");
            ClientWorld clientWorld = this.client.world;
            if (clientWorld!=null) {
                float f = clientWorld.getStarBrightness(1.0F);
                float g;
                if (clientWorld.getLightningTicksLeft()>0) {
                    g = 1.0F;
                } else {
                    g = f * 0.95F + 0.05F;
                }

                float h = 1.0F; // needs game option ((Double)this.client.options.getDarknessEffectScale().getValue()).floatValue();
                float i = this.getDarknessFactor(delta) * h;
                assert this.client.player != null;
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
                        boolean w = true;
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


        int lightvalue;

        double dark;

        assert this.client.player != null;
        if (this.client.player.hasStatusEffect(RegisterStatusEffects.DARKNESS)) {
            dark = MathAddon.cutCos(time, 0, true) * 1.6;
        } else {
            dark = 0;
        }

        if (this.dirty) {
            this.dirty = false;
            this.client.getProfiler().push("lightTex");
            ClientWorld clientWorld = this.client.world;
            if (clientWorld != null) {
                float f = clientWorld.getStarBrightness(1.0F);
                float h;
                if (clientWorld.getLightningTicksLeft() > 0) {
                    h = 1.0F;
                } else {
                    h = f * 0.95F + 0.05F;
                }

                assert this.client.player != null;
                float i = this.client.player.getUnderwaterVisibility();
                float l;
                if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                    l = GameRenderer.getNightVisionStrength(this.client.player, delta);
                } else if (i > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    l = i;
                } else {
                    l = 0.0F;
                }

                Vec3f vec3f = new Vec3f(f, f, 1.0F);
                vec3f.lerp(new Vec3f(1.0F, 1.0F, 1.0F), 0.35F);
                float m = this.flickerIntensity + 1.5F;
                Vec3f vec3f2 = new Vec3f();

                for (int n = 0; n < 16; ++n) {
                    for (int o = 0; o < 16; ++o) {
                        float p = this.getBrightness(clientWorld, n) * h;
                        float q = this.getBrightness(clientWorld, o) * m;
                        float s = q * ((q * 0.6F + 0.4F) * 0.6F + 0.4F);
                        float t = q * (q * q * 0.6F + 0.4F);
                        vec3f2.set(q, s, t);
                        float w;
                        Vec3f vec3f5;
                        if (clientWorld.getDimensionEffects().shouldBrightenLighting()) {
                            vec3f2.lerp(new Vec3f(0.99F, 1.12F, 1.0F), 0.25F);
                        } else {
                            Vec3f vec3f3 = vec3f.copy();
                            vec3f3.scale(p);
                            vec3f2.add(vec3f3);
                            vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
                            if (this.renderer.getSkyDarkness(delta) > 0.0F) {
                                w = this.renderer.getSkyDarkness(delta);
                                vec3f5 = vec3f2.copy();
                                vec3f5.multiplyComponentwise(0.7F, 0.6F, 0.6F);
                                vec3f2.lerp(vec3f5, w);
                            }
                        }

                        vec3f2.clamp(0.0F, 1.0F);
                        float y;
                        if (l > 0.0F) {
                            y = Math.max(vec3f2.getX(), Math.max(vec3f2.getY(), vec3f2.getZ()));
                            if (y < 1.0F) {
                                w = 1.0F / y;
                                vec3f5 = vec3f2.copy();
                                vec3f5.scale(w);
                                vec3f2.lerp(vec3f5, l);
                            }
                        }

                        y = (float) this.client.options.gamma;
                        Vec3f vec3f6 = vec3f2.copy();
                        vec3f6.modify(this::easeOutQuart);
                        vec3f2.lerp(vec3f6, Math.max(0.0F, y - (float) dark));
                        vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
                        vec3f2.clamp(0.0F, 1.0F);
                        vec3f2.scale(255.0F);
                        int x = (int) vec3f2.getX();
                        int y1 = (int) vec3f2.getY();
                        int z = (int) vec3f2.getZ();
                        lightvalue = -16777216 | z << 16 | y1 << 8 | x;
                        this.image.setColor(o, n, lightvalue);
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
        float f = (float)lightLevel / 15.0F;
        float g = f / (4.0F - 3.0F * f);
        return MathHelper.lerp(world.getDimension().getBrightness(lightLevel), g, 1.0F);
    }
}
