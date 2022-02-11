package frozenblock.wild.mod.fromAccurateSculk;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.blocks.SculkBlock;
import frozenblock.wild.mod.blocks.SculkCatalystBlock;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.liukrastapi.Sphere;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.World;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static java.lang.Math.*;

public class BrokenSculkGrower {
    /** DEFAULT VARIABLES */
    public static final BlockState sculkBlock = SculkBlock.SCULK_BLOCK.getDefaultState();
    public static final BlockState water = Blocks.WATER.getDefaultState();
    /** NOISE VARIABLES */
    public static final double multiplier = 0.09; //Keep this low. Lowering it makes the noise bigger, raising it makes it smaller (more like static)
    public static final double minThreshold = 0.0; //The value that outer Sculk's noise must be ABOVE in order to grow
    public static final double maxThreshold = 0.3; //The value that outer Sculk's noise must be BELOW in order to grow
    public static long seed = 1;
    public static PerlinNoiseSampler sample = new PerlinNoiseSampler(new Xoroshiro128PlusPlusRandom(seed));

    /** MAIN CODE */
    public static void sculk(BlockPos blockPos, World world, @Nullable Entity entity, int catalysts) { //Choose Amount Of Sculk + Initial Radius
        if (entity!=null && world instanceof ServerWorld serverWorld) {
            world.playSound(null, blockPos, RegisterSounds.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.MASTER, 1F, 1F);
            world.playSound(null, blockPos, RegisterSounds.ENTITY_WARDEN_AMBIENT_UNDERGROUND, SoundCategory.MASTER, 1F, 1F);
            world.playSound(null, blockPos, RegisterSounds.BLOCK_SCULK_SENSOR_RECEIVE_RF, SoundCategory.MASTER, 1F, 1F);
            world.playSound(null, blockPos, RegisterSounds.ENTITY_WARDEN_DIG, SoundCategory.MASTER, 1F, 1F);
            world.playSound(null, blockPos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.MASTER, 1F, 1F);
            world.playSound(null, blockPos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.MASTER, 1F, 1F);
            world.playSound(null, blockPos, SoundEvents.ENTITY_BLAZE_AMBIENT, SoundCategory.MASTER, 1F, 1F);
            world.playSound(null, blockPos, SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.MASTER, 1F, 1F);
            world.playSound(null, blockPos, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.MASTER, 1F, 1F);
            BlockPos down = blockPos.down();
            long seed1 = serverWorld.getSeed();
            if (seed!=seed1) {
                seed=seed1;
                sample = new PerlinNoiseSampler(new Xoroshiro128PlusPlusRandom(seed));
            }
            sculkUnoptim(down, serverWorld);
        }
    }

    public static void sculkUnoptim(BlockPos down, ServerWorld world) {
        for (BlockPos posNew : Sphere.stinkyThiefWorldGenerator(down, 18, world)) {
            int x = posNew.getX();
            int y = posNew.getY();
            int z = posNew.getZ();
            double sampled = sample.sample(x * multiplier, y * multiplier, z * multiplier-0.3);
            double min = MathHelper.clamp(minThreshold + 0.7*(Math.sin((x*PI)/100)),-1,0);
            double max = MathHelper.clamp(maxThreshold + 0.7*(Math.sin((x*PI)/100)),0,1);
            if (sampled > min && sampled < max) {
                placeSculk(posNew, world);
            }
        }
    }

    public static boolean placeSculk(BlockPos blockPos, World world) { //Call For Sculk & Call For Veins
        if (airOrReplaceableUp(world, blockPos)) {
            placeSculkOptim(blockPos, world);
            return true;
        }
        return false;
    }

    public static void placeSculkOptim(BlockPos NewSculk, World world) { //Place Sculk & Call For Veins
        world.setBlockState(NewSculk, sculkBlock);
    }

    public static boolean airOrReplaceableUp(World world, BlockPos blockPos) {
        return world.getBlockEntity(blockPos)==null;
    }

    //double sinA = Math.sin(x/PI);
    //double cosB = Math.cos(x*z);
    //double cosA = Math.cos(x*y);
    //double sinB = Math.sin(x*z);
    //double fakeNoise = 50*((sinA*sinA) + (cosB*seed));
    //double fakeNoise2 = 50*((cosA*seed) + (sinB*sinB));
    //I'm keeping the fakeNoise stuff in case we can use it later, it DOES look really strange
}
