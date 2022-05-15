package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.block.MultifaceGrowthBlock;
import net.frozenblock.wildmod.block.SculkShriekerBlock;
import net.frozenblock.wildmod.particle.SculkChargeParticleEffect;
import net.frozenblock.wildmod.particle.ShriekParticleEffect;
import net.frozenblock.wildmod.registry.RegisterParticles;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow @Nullable private ClientWorld world;

    @Inject(method = "processWorldEvent", at = @At("TAIL"))
    public void processWorldEvent(PlayerEntity source, int eventId, BlockPos pos, int data, CallbackInfo ci) {
        Random random = this.world.random;
        int i;
        int j;
        float w;
        float ac;
        double g;
        float v;
        int k;
        double aj;
        double s;
        double d;
        double ak;
        int t;
        double e;
        double al;
        double f;
        double af;
        switch (eventId) {
            case 3006:
                i = data >> 6;
                float ae;
                if (i > 0) {
                    if (random.nextFloat() < 0.3F + (float)i * 0.1F) {
                        v = 0.15F + 0.02F * (float)i * (float)i * random.nextFloat();
                        w = 0.4F + 0.3F * (float)i * random.nextFloat();
                        this.world.playSound(pos, RegisterSounds.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, v, w, false);
                    }

                    byte b = (byte)(data & 63);
                    IntProvider intProvider = UniformIntProvider.create(0, i);
                    ac = 0.005F;
                    Supplier<Vec3d> supplier = () -> {
                        return new Vec3d(MathHelper.nextDouble(random, -0.004999999888241291, 0.004999999888241291), MathHelper.nextDouble(random, -0.004999999888241291, 0.004999999888241291), MathHelper.nextDouble(random, -0.004999999888241291, 0.004999999888241291));
                    };
                    if (b == 0) {
                        Direction[] var11 = Direction.values();
                        int var12 = var11.length;

                        for(int var13 = 0; var13 < var12; ++var13) {
                            Direction direction2 = var11[var13];
                            float ad = direction2 == Direction.DOWN ? 3.1415927F : 0.0F;
                            g = direction2.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                            spawnParticles(this.world, pos, new SculkChargeParticleEffect(ad), intProvider, direction2, supplier, g);
                        }

                        return;
                    } else {
                        Iterator var47 = MultifaceGrowthBlock.flagToDirections(b).iterator();

                        while(var47.hasNext()) {
                            Direction direction3 = (Direction)var47.next();
                            ae = direction3 == Direction.UP ? 3.1415927F : 0.0F;
                            af = 0.35;
                            spawnParticles(this.world, pos, new SculkChargeParticleEffect(ae), intProvider, direction3, supplier, 0.35);
                        }

                        return;
                    }
                } else {
                    this.world.playSound(pos, RegisterSounds.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                    boolean bl = this.world.getBlockState(pos).isFullCube(this.world, pos);
                    k = bl ? 40 : 20;
                    ac = bl ? 0.45F : 0.25F;
                    float ag = 0.07F;

                    for(t = 0; t < k; ++t) {
                        float ah = 2.0F * random.nextFloat() - 1.0F;
                        ae = 2.0F * random.nextFloat() - 1.0F;
                        float ai = 2.0F * random.nextFloat() - 1.0F;
                        this.world.addParticle(RegisterParticles.SCULK_CHARGE_POP, (double)pos.getX() + 0.5 + (double)(ah * ac), (double)pos.getY() + 0.5 + (double)(ae * ac), (double)pos.getZ() + 0.5 + (double)(ai * ac), (double)(ah * 0.07F), (double)(ae * 0.07F), (double)(ai * 0.07F));
                    }

                    return;
                }
            case 3007:
                for(j = 0; j < 10; ++j) {
                    this.world.addParticle(new ShriekParticleEffect(j * 5), false, (double)pos.getX() + 0.5, (double)pos.getY() + SculkShriekerBlock.TOP, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                }

                this.world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + SculkShriekerBlock.TOP, (double)pos.getZ() + 0.5, RegisterSounds.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.BLOCKS, 2.0F, 0.6F + this.world.random.nextFloat() * 0.4F, false);
        }
    }

    private static void spawnParticles(World world, BlockPos pos, ParticleEffect effect, IntProvider count, Direction direction, Supplier<Vec3d> velocity, double offsetMultiplier) {
        int i = count.get(world.random);

        for(int j = 0; j < i; ++j) {
            spawnParticle(world, pos, direction, effect, velocity.get(), offsetMultiplier);
        }

    }

    private static void spawnParticle(World world, BlockPos pos, Direction direction, ParticleEffect effect, Vec3d velocity, double offsetMultiplier) {
        Vec3d vec3d = Vec3d.ofCenter(pos);
        int i = direction.getOffsetX();
        int j = direction.getOffsetY();
        int k = direction.getOffsetZ();
        double d = vec3d.x + (i == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)i * offsetMultiplier);
        double e = vec3d.y + (j == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)j * offsetMultiplier);
        double f = vec3d.z + (k == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)k * offsetMultiplier);
        double g = i == 0 ? velocity.getX() : 0.0;
        double h = j == 0 ? velocity.getY() : 0.0;
        double l = k == 0 ? velocity.getZ() : 0.0;
        world.addParticle(effect, d, e, f, g, h, l);
    }
}
