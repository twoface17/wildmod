package frozenblock.wild.mod.fromAccurateSculk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class FireFlyParticle extends AbstractSlowingParticle {
    private final SpriteProvider spriteProvider;

    public int getBrightness(float f) {
        return 240;
    }

    FireFlyParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, h, i);
        this.spriteProvider = spriteProvider;
        this.scale(1.0f);
        this.setSpriteForAge(spriteProvider);
        this.setMaxAge(1);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }

    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternion quaternion;
        if (this.angle == 0.0F) {
            quaternion = camera.getRotation();
        } else {
            quaternion = new Quaternion(camera.getRotation());
            float i = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Z.getRadialQuaternion(i));
        }

        Vec3f i = new Vec3f(-1.0F, -1.0F, 0.0F);
        i.rotate(quaternion);
        Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
        float j = this.getSize(tickDelta);

        for(int k = 0; k < 4; ++k) {
            Vec3f vec3f = vec3fs[k];
            vec3f.rotate(quaternion);
            vec3f.scale(0.1F);
            vec3f.add(f, g, h);
        }

        float k = this.getMinU();
        float vec3f = this.getMaxU();
        float l = this.getMinV();
        float m = this.getMaxV();
        int n = this.getBrightness(tickDelta);
        vertexConsumer.vertex((double)vec3fs[0].getX(), (double)vec3fs[0].getY(), (double)vec3fs[0].getZ()).texture(vec3f, m).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(n).next();
        vertexConsumer.vertex((double)vec3fs[1].getX(), (double)vec3fs[1].getY(), (double)vec3fs[1].getZ()).texture(vec3f, l).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(n).next();
        vertexConsumer.vertex((double)vec3fs[2].getX(), (double)vec3fs[2].getY(), (double)vec3fs[2].getZ()).texture(k, l).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(n).next();
        vertexConsumer.vertex((double)vec3fs[3].getX(), (double)vec3fs[3].getY(), (double)vec3fs[3].getZ()).texture(k, m).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(n).next();
    }



    @Environment(value= EnvType.CLIENT)
    public static class Factory
            implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            FireFlyParticle fireFlyParticle = new FireFlyParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            return fireFlyParticle;
        }
    }
}
