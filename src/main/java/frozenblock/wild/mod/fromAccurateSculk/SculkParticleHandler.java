package frozenblock.wild.mod.fromAccurateSculk;

import frozenblock.wild.mod.registry.RegisterAccurateSculk;
import frozenblock.wild.mod.registry.RegisterParticles;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

public class SculkParticleHandler {

    public static void catalystSouls(World world, BlockPos blockPos) {
        double d = (double) blockPos.getX() + 0.5;
        double e = (double) blockPos.getY() + 1;
        double f = (double) blockPos.getZ() + 0.5;
        world.addImportantParticle(RegisterParticles.SCULK_SOUL, d - 0.07, e, f - 0.07, 0.0, 0.06, 0.0);
        world.addImportantParticle(RegisterParticles.SCULK_SOUL, d + 0.07, e, f + 0.07, 0.0, 0.05, 0.0);
    }

    public static void shriekerShriek(World world, BlockPos blockPos, int direction) {
        double d = (double) blockPos.getX() + 0.5;
        double e = (double) blockPos.getY() + 0;
        double f = (double) blockPos.getZ() + 0.5;
        if (direction==1) {
            world.addImportantParticle(RegisterAccurateSculk.SCULK_SHRIEK, d, e, f, 0.0, 0.105, 0.0);
            world.addImportantParticle(RegisterAccurateSculk.SCULK_SHRIEK2, d, e, f, 0.0, 0.105, 0.0);
        } else if (direction==2) {
            world.addImportantParticle(RegisterAccurateSculk.SCULK_SHRIEKZ, d, e, f, 0.0, 0.105, 0.0);
            world.addImportantParticle(RegisterAccurateSculk.SCULK_SHRIEKZ2, d, e, f, 0.0, 0.105, 0.0);
        } else if (direction==3) {
            world.addImportantParticle(RegisterAccurateSculk.SCULK_SHRIEKNX, d, e, f, 0.0, 0.105, 0.0);
            world.addImportantParticle(RegisterAccurateSculk.SCULK_SHRIEKNX2, d, e, f, 0.0, 0.105, 0.0);
        } else if (direction==4) {
            world.addImportantParticle(RegisterAccurateSculk.SCULK_SHRIEKX, d, e, f, 0.0, 0.105, 0.0);
            world.addImportantParticle(RegisterAccurateSculk.SCULK_SHRIEKX2, d, e, f, 0.0, 0.105, 0.0);
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
