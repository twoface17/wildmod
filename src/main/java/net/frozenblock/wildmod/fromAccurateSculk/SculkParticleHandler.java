package net.frozenblock.wildmod.fromAccurateSculk;

import net.frozenblock.wildmod.registry.RegisterParticles;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

public class SculkParticleHandler {

    public static void catalystSouls(World world, BlockPos blockPos) {
        double d = (double) blockPos.getX() + 0.5;
        double e = (double) blockPos.getY() + 0.85;
        double f = (double) blockPos.getZ() + 0.5;
        int random = UniformIntProvider.create(1, 3).get(world.getRandom());
        if (random == 1) {
            world.addImportantParticle(RegisterParticles.SCULK_SOUL, d + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), e, f + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), 0.0, MathHelper.clamp(Math.random() * 0.1f, 0.02, 0.05), 0.0);
        } else if (random == 2) {
            world.addImportantParticle(RegisterParticles.SCULK_SOUL, d + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), e, f + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), 0.0, MathHelper.clamp(Math.random() * 0.1f, 0.02, 0.05), 0.0);
            world.addImportantParticle(RegisterParticles.SCULK_SOUL, d + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), e, f + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), 0.0, MathHelper.clamp(Math.random() * 0.1f, 0.02, 0.05), 0.0);
        } else if (random == 3) {
            world.addImportantParticle(RegisterParticles.SCULK_SOUL, d + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), e, f + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), 0.0, MathHelper.clamp(Math.random() * 0.1f, 0.02, 0.05), 0.0);
            world.addImportantParticle(RegisterParticles.SCULK_SOUL, d + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), e, f + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), 0.0, MathHelper.clamp(Math.random() * 0.1f, 0.02, 0.05), 0.0);
            world.addImportantParticle(RegisterParticles.SCULK_SOUL, d + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), e, f + MathHelper.clamp((Math.random() - 0.5) * 2, -0.125, 0.125), 0.0, MathHelper.clamp(Math.random() * 0.1f, 0.02, 0.05), 0.0);
        }
    }

    public static void shriekerGargle1(World world, BlockPos blockPos) {
        double ran1 = UniformIntProvider.create(-3, 3).get(world.getRandom());
        double ran2 = UniformIntProvider.create(-3, 3).get(world.getRandom());
        double d = (double) blockPos.getX() + 0.5;
        double e = (double) blockPos.getY() + 0;
        double f = (double) blockPos.getZ() + 0.5;
        double d1 = (double) blockPos.getX() + 0.5 + (ran1 / 10);
        double e1 = (double) blockPos.getY() + 0.8;
        double f1 = (double) blockPos.getZ() + 0.5 + (ran1 / 10);
        double d2 = (double) blockPos.getX() + 0.5 + (ran2 / 10);
        double f2 = (double) blockPos.getZ() + 0.5 + (ran2 / 10);
        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, d1, e1, f1, 0, 0.5, 0);
        world.addParticle(ParticleTypes.BUBBLE_POP, d2, e1, f1, 0, 0, 0);
        world.addParticle(ParticleTypes.SPLASH, d2, e1, f1, 0, 0.1, 0);
        world.addParticle(ParticleTypes.SPLASH, d1, e1, f1, 0, 0.1, 0);
    }

    public static void shriekerGargle2(World world, BlockPos blockPos) {
        double ran1 = UniformIntProvider.create(-3, 3).get(world.getRandom());
        double ran2 = UniformIntProvider.create(-3, 3).get(world.getRandom());
        double d = (double) blockPos.getX() + 0.5;
        double e = (double) blockPos.getY() + 0;
        double f = (double) blockPos.getZ() + 0.5;
        double d1 = (double) blockPos.getX() + 0.5 + (ran1 / 10);
        double e1 = (double) blockPos.getY() + 0.8;
        double f1 = (double) blockPos.getZ() + 0.5 + (ran1 / 10);
        double d2 = (double) blockPos.getX() + 0.5 + (ran2 / 10);
        double f2 = (double) blockPos.getZ() + 0.5 + (ran2 / 10);
        world.addParticle(ParticleTypes.BUBBLE, d2, e1, f2, 0, 0.2, 0);
        world.addParticle(ParticleTypes.SPLASH, d1, e1, f2, 0, 0.1, 0);
        world.addParticle(ParticleTypes.SPLASH, d2, e1, f2, 0, 0.1, 0);
    }
}
