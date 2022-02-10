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
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.World;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.*;

public class SculkGrower {
    /** DEFAULT VARIABLES */
    public static final BlockState vein = SculkVeinBlock.SCULK_VEIN.getDefaultState();
    public static final BlockState brokenVein = SculkVeinBlock.SCULK_VEIN.getDefaultState().with(Properties.DOWN, false);
    public static final BlockState sculkBlock = SculkBlock.SCULK_BLOCK.getDefaultState();
    public static final Block sculkBlockBlock = SculkBlock.SCULK_BLOCK;
    public static final BlockState water = Blocks.WATER.getDefaultState();
    public static final BlockState air = Blocks.AIR.getDefaultState();
    public static final Block waterBlock = Blocks.WATER;
    public static final Block veinBlock = SculkVeinBlock.SCULK_VEIN;
    public static final BooleanProperty waterLogged = Properties.WATERLOGGED;
    public static final BlockState brokenWaterVein = SculkVeinBlock.SCULK_VEIN.getDefaultState().with(Properties.DOWN, false).with(waterLogged, true);
    /** NOISE VARIABLES */
    public static final double multiplier = 0.15; //Keep this low. Lowering it makes the noise bigger, raising it makes it smaller (more like static)
    public static final double minThreshold = -0.1; //The value that outer Sculk's noise must be ABOVE in order to grow
    public static final double maxThreshold = 0.9; //The value that outer Sculk's noise must be BELOW in order to grow
    public static long seed = 1;
    public static PerlinNoiseSampler sample = new PerlinNoiseSampler(new Xoroshiro128PlusPlusRandom(seed));

    /** MAIN CODE */
    public static void sculk(BlockPos blockPos, World world, @Nullable Entity entity, int catalysts) { //Choose Amount Of Sculk + Initial Radius
        if (entity!=null && world instanceof ServerWorld serverWorld) {
            world.playSound(null, blockPos, RegisterSounds.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 1F, 1F);
            BlockPos down = blockPos.down();
            long seed1 = serverWorld.getSeed();
            if (seed!=seed1) {
                seed=seed1;
                sample = new PerlinNoiseSampler(new Xoroshiro128PlusPlusRandom(seed));
            }
            if (SculkTags.THREE.contains(entity.getType())) {
                sculkOptim(3*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, serverWorld);
            } else if (SculkTags.FIVE.contains(entity.getType())) {
                sculkOptim(5*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, serverWorld);
            } else if (SculkTags.TEN.contains(entity.getType())) {
                sculkOptim(10*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, serverWorld);
            } else if (SculkTags.TWENTY.contains(entity.getType())) {
                sculkOptim(20*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, serverWorld);
            } else if (SculkTags.FIFTY.contains(entity.getType())) {
                sculkOptim(50*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, serverWorld);
            } else if (SculkTags.ONEHUNDRED.contains(entity.getType())) {
                sculkOptim(500*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, serverWorld);
            }
        }
    }

    public static void sculkOptim(float loop, int rVal, BlockPos down, ServerWorld world) { //Call For Sculk Placement & Increase Radius If Stuck
        int timesFailed=0;
        int groupsFailed=1;
        float fLoop = loop * world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER);

        for (int l = 0; l < fLoop;) {
            boolean succeed=false;
            if (sqrt(rVal +(groupsFailed-1))<24) { //Generate Sculk In Radius
                double a = random() * 2 * PI;
                double r = sqrt(rVal + (groupsFailed - 1)) * sqrt(random());
                succeed = placeSculk(down.add((int) (r * sin(a)), 0, (int) (r * cos(a))), world);
            } else { //Use Noise For Outer Sculk "Veins"
                double a = random() * 2 * PI;
                double r = sqrt(rVal + (groupsFailed - 1)) * sqrt(random());
                BlockPos posNew = down.add((int) (r * sin(a)), 0, (int) (r * cos(a)));
                int x = posNew.getX();
                int y = posNew.getY();
                int z = posNew.getZ();
                double sampled = sample.sample(x*multiplier,y*multiplier,z*multiplier);
                if (sampled>minThreshold && sampled<maxThreshold) {
                    succeed = placeSculk(posNew, world);
                }
            }
            //Determine If Sculk Placement Was Successful And If Radius Should Be Increased
            if (!succeed) { ++timesFailed; } else {
                ++l;
                if (timesFailed>0) {--timesFailed; }
            }
            if (timesFailed>=10) {
                timesFailed=0;
                groupsFailed=groupsFailed+2;
            }
            if (sqrt(rVal +(groupsFailed-1))>50) {
                break;
            }
        }
        setCatalysts(world, down.up(), rVal +(groupsFailed-1));
    }

    public static boolean placeSculk(BlockPos blockPos, World world) { //Call For Sculk & Call For Veins
        Block block = world.getBlockState(blockPos).getBlock();
        if (SculkTags.BLOCK_REPLACEABLE.contains(block) && !SculkTags.SCULK_VEIN_REPLACEABLE.contains(block) && !SculkTags.SCULK.contains(block) && airOrReplaceableUp(world, blockPos)) {
            placeSculkOptim(blockPos, world);
            return true;
        } else {
            BlockPos NewSculk = sculkCheck(blockPos, world);
            if (NewSculk != null) {
                block = world.getBlockState(NewSculk).getBlock();
                if (SculkTags.BLOCK_REPLACEABLE.contains(block)) {
                    placeSculkOptim(NewSculk, world);
                    return true;
                } else if (airveins(world, NewSculk)) {
                    Block blockUp = world.getBlockState(NewSculk.up()).getBlock();
                    if (SculkTags.SCULK_VEIN_REPLACEABLE.contains(blockUp) && blockUp!=veinBlock) {
                        veins(NewSculk, world);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void placeSculkOptim(BlockPos NewSculk, World world) { //Place Sculk & Call For Veins
        world.setBlockState(NewSculk, sculkBlock);
        for (Direction direction : Direction.values()) {
            BlockPos pos = NewSculk.offset(direction);
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block==veinBlock) {
                if (state.get(waterLogged)) { //If Vein Is Waterlogged
                    if (state.with(getOpposite(direction), false)==brokenWaterVein) {
                        world.setBlockState(pos, water);
                    } else {
                        world.setBlockState(pos, state.with(getOpposite(direction), false));
                    }
                } else { // If Vein Isn't Waterlogged
                    if (state.with(getOpposite(direction), false)==brokenVein) {
                        world.setBlockState(pos, air);
                    } else {
                        world.setBlockState(pos, state.with(getOpposite(direction), false));
                    }
                }
            }
            if (direction==Direction.UP) {
                if (SculkTags.SCULK_VEIN_REPLACEABLE.contains(block) && block!=waterBlock && !state.isAir()) {
                    if (SculkTags.ALWAYS_WATER.contains(block) || (state.contains(waterLogged) && state.get(waterLogged))) {
                        world.setBlockState(pos, water);
                    } else {
                        world.setBlockState(pos, air);
                    }
                }
            }
        }
        veins(NewSculk, world);
    }

    public static void veins(BlockPos blockPos, World world) { //Calculate Vein Placement
        for (Direction direction : Direction.values()) {
            BlockPos pos = blockPos.offset(direction);
            if (airveins(world, pos)) {
                veinPlaceOptim(pos, world);
            } else { BlockPos check = sculkCheck(pos, world);
                if (airveins(world, check)) {
                    veinPlaceOptim(check, world);
                }
            }
        }
        if (airveins(world, blockPos)) {
            veinPlaceOptim(blockPos, world);
        } else { BlockPos check = sculkCheck(blockPos, world);
            if (airveins(world, check)) {
                veinPlaceOptim(check, world);
            }
        }
    }

    public static void veinPlaceOptim(BlockPos blockPos, World world) { //Place Veins
        for (Direction direction : Direction.values()) {
            BlockPos pos = blockPos.offset(direction);
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (SculkTags.ALWAYS_WATER.contains(block) || state == Blocks.WATER.getDefaultState()) {
                world.setBlockState(pos, vein.with(waterLogged, true).with(getOpposite(direction), true));
            } else if (block != waterBlock) {
                if (block == veinBlock) {
                    world.setBlockState(pos, state.with(getOpposite(direction), true));
                } else if (SculkTags.SCULK_VEIN_REPLACEABLE.contains(block) || state.isAir()) {
                    world.setBlockState(pos, vein.with(getOpposite(direction), true));
                }
            }
        }
    }

    /** BLOCKSTATE TWEAKING */
    public static BooleanProperty getOpposite(Direction direction) {
        if (direction==Direction.UP) { return Properties.DOWN; }
        if (direction==Direction.DOWN) { return Properties.UP; }
        if (direction==Direction.NORTH) { return Properties.SOUTH; }
        if (direction==Direction.SOUTH) { return Properties.NORTH; }
        if (direction==Direction.EAST) { return Properties.WEST; }
        if (direction==Direction.WEST) { return Properties.EAST; }
        return Properties.DOWN;
    }

    /** CALCULATIONS & CHECKS */
    public static int firstRadius(World world, int i) {
        return MathHelper.clamp(i*world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER),1, 48);
    }
    public static void setCatalysts(World world, BlockPos pos, int i) {
        for (BlockPos blockPos : Sphere.blockPosSphere(pos, 9, SculkCatalystBlock.SCULK_CATALYST_BLOCK, world)) {
            if (world.getBlockEntity(blockPos) instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
                if (sculkCatalystBlockEntity.lastSculkRange!=i) {
                    sculkCatalystBlockEntity.lastSculkRange=i;
                }
            }
        }
    }
    public static int getHighestRadius(World world, BlockPos pos) {
        int current = 3;
        for (BlockPos blockPos : Sphere.blockPosSphere(pos, 9, SculkCatalystBlock.SCULK_CATALYST_BLOCK, world)) {
            if (world.getBlockEntity(blockPos) instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
                current=(int) Math.max(current, (sculkCatalystBlockEntity.lastSculkRange)/(2*Math.cos((sculkCatalystBlockEntity.lastSculkRange*Math.PI)/175)));
            }
        }
        return current;
    }
    public static BlockPos sculkCheck(BlockPos blockPos, World world) { //Call For Up&Down Checks
        BlockPos check = checkPt2(blockPos, world, world.getGameRules().getBoolean(WildMod.SCULK_STOPS_SCULKCHECK));
        if (check!=null) { return check; }
        if (!world.isSkyVisible(blockPos)) {
            return checkPt1(blockPos, world, world.getGameRules().getBoolean(WildMod.SCULK_STOPS_SCULKCHECK));
        } return null;
    }
    public static BlockPos checkPt1(BlockPos blockPos, World world, boolean sculkStops) { //Check For Valid Placement Above
        int upward = world.getGameRules().getInt(WildMod.UPWARD_SPREAD);
        int MAX = world.getHeight();
        if (blockPos.getY() + upward >= MAX) {
            upward = (MAX - blockPos.getY())-1;
        }
        for (int h = 0; h < upward; h++) {
            BlockPos pos =  blockPos.up(h);
            Block block = world.getBlockState(pos).getBlock();
            if (!SculkTags.SCULK_VEIN_REPLACEABLE.contains(block) && !SculkTags.SCULK.contains(block) && airOrReplaceableUp(world, pos)) {
                return pos;
            } else if (sculkStops && block==sculkBlockBlock) {return null;}
        }
        return null;
    }
    public static BlockPos checkPt2(BlockPos blockPos, World world, boolean sculkStops) { //Check For Valid Placement Below
        int downward = world.getGameRules().getInt(WildMod.DOWNWARD_SPREAD);
        int MIN = world.getBottomY();
        if (blockPos.getY() - downward <= MIN) {
            downward = (blockPos.getY()-MIN)-1;
        }
        for (int h = 0; h < downward; h++) {
            BlockPos pos = blockPos.down(h);
            Block block = world.getBlockState(pos).getBlock();
            if (!SculkTags.SCULK_VEIN_REPLACEABLE.contains(block) && !SculkTags.SCULK.contains(block) && airOrReplaceableUp(world, pos)) {
                return pos;
            } else if (sculkStops && block==sculkBlockBlock) {return null;}
        }
        return null;
    }

    public static boolean airveins(World world, BlockPos blockPos) { //Check If Veins Are Above Invalid Block
        if (blockPos==null) { return false; }
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();
        Fluid fluid = world.getFluidState(blockPos).getFluid();
        return !SculkTags.SCULK.contains(block) && !SculkTags.SCULK_UNTOUCHABLE.contains(block) && !SculkTags.SCULK_VEIN_REPLACEABLE.contains(block) && !state.isAir() && !FluidTags.WATER.contains(fluid) && !FluidTags.LAVA.contains(fluid);
    }

    public static boolean airOrReplaceableUp(World world, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            BlockState state = world.getBlockState(blockPos.offset(direction));
            if (!state.isFullCube(world, blockPos)) {
                return true;
            }
        }
        return false;
    }

    //double sinA = Math.sin(x/PI);
    //double cosB = Math.cos(x*z);
    //double cosA = Math.cos(x*y);
    //double sinB = Math.sin(x*z);
    //double fakeNoise = 50*((sinA*sinA) + (cosB*seed));
    //double fakeNoise2 = 50*((cosA*seed) + (sinB*sinB));
    //I'm keeping the fakeNoise stuff in case we can use it later, it DOES look really strange
}
