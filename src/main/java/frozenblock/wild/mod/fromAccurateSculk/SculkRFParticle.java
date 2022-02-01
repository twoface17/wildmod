package frozenblock.wild.mod.fromAccurateSculk;

import frozenblock.wild.mod.blocks.SculkBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;

import java.util.Random;

public class SculkRFParticle extends SpriteBillboardParticle {
    private final BooleanProperty ORIGINAL = NewProperties.ORIGINAL_RF;
    private final IntProperty POWER = Properties.POWER;
    private final Block SCULK = SculkBlock.SCULK_BLOCK;
    private final BlockPos pos;

    public int getBrightness(float f) {
        return 240;
    }

    SculkRFParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, h, i);
        this.scale(1.0f);
        this.setSprite(spriteProvider);
        this.setMaxAge(40);
        this.velocityX=0;
        this.velocityY=0;
        this.velocityZ=0;
        this.pos = new BlockPos(g,h,i);
    }

    @Override
    public Particle move(float speed) { return this; }
    @Override
    public void setVelocity(double velocityX, double velocityY, double velocityZ) { }
    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        BlockState state = this.world.getBlockState(pos);
        if (world.isChunkLoaded(pos)) {
            if (state.getBlock() == SCULK) {
                if (state.get(POWER) <= 0) {
                this.markDead();
                }
            } else {
                this.markDead();
            }
        } else { this.markDead(); }
    }

    @Override
    protected float getMinU() {return this.sprite.getMinU();}

    @Override
    protected float getMaxU() {return this.sprite.getMaxU();}

    @Override
    protected float getMinV() {return this.sprite.getMinV();}

    @Override
    protected float getMaxV() {return this.sprite.getMaxV();}

    public void setSprite(SpriteProvider spriteProvider) {
        this.setSprite(spriteProvider.getSprite(this.random));
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
            return new SculkRFParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
