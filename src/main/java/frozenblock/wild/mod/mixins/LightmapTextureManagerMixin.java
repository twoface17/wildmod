package frozenblock.wild.mod.mixins;

import frozenblock.wild.mod.liukrastapi.MathAddon;
import frozenblock.wild.mod.registry.RegisterSounds;
import frozenblock.wild.mod.registry.RegisterStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static java.lang.Math.*;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

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
            double soundTimer = -Math.tan((time*PI + 128)/160); //Can Someone Please Find A Way To Get This To Sync With The Fog Pulsing?
            if (soundTimer == 0 && shouldPlay) {
                if (angerLevel == 0) {
                    shouldPlay=false;
                    double a = random() * 2 * PI;
                    double r = sqrt(16) * sqrt(random());
                    int x = (int) (r * cos(a));
                    int z = (int) (r * sin(a));
                    BlockPos play = this.client.player.getBlockPos().add(x, 0, z);
                    assert this.client.world != null;
                    this.client.world.playSound(this.client.player, play, RegisterSounds.ENTITY_WARDEN_CLOSE, SoundCategory.AMBIENT, 0.4F, 1F);
                } else if (angerLevel == 1) {
                    shouldPlay=false;
                    double a = random() * 2 * PI;
                    double r = sqrt(12) * sqrt(random());
                    int x = (int) (r * cos(a));
                    int z = (int) (r * sin(a));
                    BlockPos play = this.client.player.getBlockPos().add(x, 0, z);
                    assert this.client.world != null;
                    this.client.world.playSound(this.client.player, play, RegisterSounds.ENTITY_WARDEN_CLOSER, SoundCategory.AMBIENT, 0.6F, 1F);
                } else if (angerLevel == 2) {
                    shouldPlay=false;
                    double a = random() * 2 * PI;
                    double r = sqrt(8) * sqrt(random());
                    int x = (int) (r * cos(a));
                    int z = (int) (r * sin(a));
                    BlockPos play = this.client.player.getBlockPos().add(x, 0, z);
                    assert this.client.world != null;
                    this.client.world.playSound(this.client.player, play, RegisterSounds.ENTITY_WARDEN_CLOSEST, SoundCategory.AMBIENT, 0.8F, 1F);
                } else if (angerLevel == 3) { //WARDEN DARKNESS
                    shouldPlay=false;
                    double a = random() * 2 * PI;
                    double r = sqrt(16) * sqrt(random());
                    int x = (int) (r * cos(a));
                    int z = (int) (r * sin(a));
                    BlockPos play = this.client.player.getBlockPos().add(x, 0, z);
                    assert this.client.world != null;
                    this.client.world.playSound(this.client.player, play, RegisterSounds.ENTITY_WARDEN_CLOSER, SoundCategory.AMBIENT, 0.05F, 1F);
                }
            }
            lastDarkTime=darkTime;
        } else {
            time = 0;
            soundTime=0;
            shouldPlay=true;
        }
    }

    @Inject(at = @At("HEAD"), method = "update")
    public void update(float delta, CallbackInfo ci) {
        int lightvalue;

        double dark;

        assert this.client.player != null;
        if(this.client.player.hasStatusEffect(RegisterStatusEffects.DARKNESS)) {
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

                for(int n = 0; n < 16; ++n) {
                    for(int o = 0; o < 16; ++o) {
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
                        float v;
                        if (l > 0.0F) {
                            v = Math.max(vec3f2.getX(), Math.max(vec3f2.getY(), vec3f2.getZ()));
                            if (v < 1.0F) {
                                w = 1.0F / v;
                                vec3f5 = vec3f2.copy();
                                vec3f5.scale(w);
                                vec3f2.lerp(vec3f5, l);
                            }
                        }

                        v = (float)this.client.options.gamma;
                        Vec3f vec3f6 = vec3f2.copy();
                        vec3f6.modify(this::easeOutQuart);
                        vec3f2.lerp(vec3f6, v - (float)dark);
                        vec3f2.lerp(new Vec3f(0.75F, 0.75F, 0.75F), 0.04F);
                        vec3f2.clamp(0.0F, 1.0F);
                        vec3f2.scale(255.0F);
                        int z = (int)vec3f2.getX();
                        int aa = (int)vec3f2.getY();
                        int ab = (int)vec3f2.getZ();
                        lightvalue = -16777216 | ab << 16 | aa << 8 | z;
                        this.image.setColor(o, n, lightvalue);
                    }
                }
            }
        }
        this.texture.upload();
    }

    private float getBrightness(World world, int lightLevel) {
        return world.getDimension().getBrightness(lightLevel);
    }

    private float easeOutQuart(float x) {
        float f = 1.0F - x;
        return 1.0F - f * f * f * f;
    }
}
