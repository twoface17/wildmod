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

//shriek more like SHREK am i right?


public class ShriekParticleNX2 extends AbstractSlowingParticle {
    private final SpriteProvider spriteProvider;

    public int getBrightness(float f) {
        return 240;
    }

    ShriekParticleNX2(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, h, i);
        this.collidesWithWorld=false;
        this.spriteProvider = spriteProvider;
        this.scale(1.5f);
        this.setSpriteForAge(spriteProvider);
        this.setMaxAge(40);
    }
    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float f) {
        Vec3d vec3d = camera.getPos();
        float g = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - vec3d.getX());
        float h = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - vec3d.getY());
        float i = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternion quaternion;
        quaternion = new Quaternion(-0.2F,  -0.8F,-0.8F,  -0.2F);

        Vec3f j = new Vec3f(-1.0F, -1.0F, 0.0F);
        j.rotate(quaternion);
        Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
        float k = this.getSize(f);

        for(int l = 0; l < 4; ++l) {
            Vec3f vec3f = vec3fs[l];
            vec3f.rotate(quaternion);
            vec3f.scale((float) (((this.age)/(9.8))/7));
            vec3f.add(g, h, i);
            this.setColorAlpha((float)(this.age-this.maxAge)*6);
        }

        float l = this.getMinU();
        float vec3f = this.getMaxU();
        float m = this.getMinV();
        float n = this.getMaxV();
        int o = this.getBrightness(f);
        vertexConsumer.vertex((double)vec3fs[0].getX(), (double)vec3fs[0].getY(), (double)vec3fs[0].getZ()).texture(vec3f, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(o).next();
        vertexConsumer.vertex((double)vec3fs[1].getX(), (double)vec3fs[1].getY(), (double)vec3fs[1].getZ()).texture(vec3f, m).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(o).next();
        vertexConsumer.vertex((double)vec3fs[2].getX(), (double)vec3fs[2].getY(), (double)vec3fs[2].getZ()).texture(l, m).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(o).next();
        vertexConsumer.vertex((double)vec3fs[3].getX(), (double)vec3fs[3].getY(), (double)vec3fs[3].getZ()).texture(l, n).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(o).next();
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

    @Environment(value= EnvType.CLIENT)
    public static class Factory
            implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ShriekParticleNX2 shriekParticle2 = new ShriekParticleNX2(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            shriekParticle2.setColorAlpha(1.0f);
            return shriekParticle2;
        }
    }
}
