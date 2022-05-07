package net.frozenblock.wildmod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class class_7452 extends ExplosionLargeParticle {
    protected class_7452(ClientWorld clientWorld, double d, double e, double f, double g, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, spriteProvider);
        this.maxAge = 16;
        this.scale = 1.5F;
        this.setSpriteForAge(spriteProvider);
    }

    @Environment(EnvType.CLIENT)
    public static class class_7453 implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider field_39196;

        public class_7453(SpriteProvider spriteProvider) {
            this.field_39196 = spriteProvider;
        }

        public Particle createParticle(
                DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
        ) {
            return new class_7452(clientWorld, d, e, f, g, this.field_39196);
        }
    }
}
