package net.frozenblock.wildmod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class SculkChargePopParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    SculkChargePopParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.velocityMultiplier = 0.96F;
        this.spriteProvider = spriteProvider;
        this.scale(1.0F);
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
    public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {
        public Particle createParticle(
                DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
        ) {
            SculkChargePopParticle sculkChargePopParticle = new SculkChargePopParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            sculkChargePopParticle.setAlpha(1.0F);
            sculkChargePopParticle.setVelocity(g, h, i);
            sculkChargePopParticle.setMaxAge(clientWorld.random.nextInt(4) + 6);
            return sculkChargePopParticle;
        }
    }
}
