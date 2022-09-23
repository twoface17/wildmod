package net.frozenblock.wildmod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class SculkChargeParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    SculkChargeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.velocityMultiplier = 0.96F;
        this.spriteProvider = spriteProvider;
        this.scale(1.5F);
        this.collidesWithWorld = false;
        this.setSpriteForAge(spriteProvider);
    }

    public int getBrightness(float tint) {
        return 240;
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }

    @Environment(EnvType.CLIENT)
    public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<SculkChargeParticleEffect> {
        public Particle createParticle(
                SculkChargeParticleEffect sculkChargeParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
        ) {
            SculkChargeParticle sculkChargeParticle = new SculkChargeParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            sculkChargeParticle.setAlpha(1.0F);
            sculkChargeParticle.setVelocity(g, h, i);
            sculkChargeParticle.prevAngle = sculkChargeParticleEffect.roll();
            sculkChargeParticle.angle = sculkChargeParticleEffect.roll();
            sculkChargeParticle.setMaxAge(clientWorld.random.nextInt(12) + 8);
            return sculkChargeParticle;
        }
    }
}
