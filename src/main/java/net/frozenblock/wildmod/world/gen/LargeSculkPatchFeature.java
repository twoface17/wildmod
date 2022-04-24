package net.frozenblock.wildmod.world.gen;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.block.SculkVeinBlock;
import net.frozenblock.wildmod.fromAccurateSculk.SculkTags;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.random.SimpleRandom;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.*;

public class LargeSculkPatchFeature extends Feature<DefaultFeatureConfig> {

    Random random = new Random();
    /** NOISE VARIABLES */
    public static final double multiplier = 0.20; //Lowering this makes the noise bigger, raising it makes it smaller (more like static)
    public static final double minThreshold = -0.3; //The value that outer Sculk's noise must be ABOVE in order to grow
    public static final double maxThreshold = 0.3; //The value that outer Sculk's noise must be BELOW in order to grow
    public static long seed = 1; //This gets set to the current world's seed in generate()
    public static final int thresholdTransition=40; //When this is lower, the min&max thresholds for Sculk placement will quickly fluctuate based on location. When higher, the min&max thresholds will have a longer, but smoother transition.
    public static PerlinNoiseSampler sample = new PerlinNoiseSampler(new SimpleRandom(seed));

    public LargeSculkPatchFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        if (seed!=context.getWorld().getSeed()) {
            seed=context.getWorld().getSeed();
            sample = new PerlinNoiseSampler(new SimpleRandom(seed));
        }
        if (!blockInSphere(context.getOrigin(), 8, RegisterBlocks.SCULK_CATALYST, context.getWorld())) {
            context.getWorld().setBlockState(context.getOrigin().up(), RegisterBlocks.SCULK_CATALYST.getDefaultState(), 0);
            if (context.getWorld().getBlockEntity(context.getOrigin()) == null) {
                context.getWorld().setBlockState(context.getOrigin(), RegisterBlocks.SCULK.getDefaultState(), 0);
            }
            double average = (context.getOrigin().getX() + context.getOrigin().getZ()) * 0.5;
            int radius = (int) (8 * Math.cos(((average) * Math.PI) / 24) + 16);
            double minThresh = 0.1 * Math.cos(((average) * Math.PI) / thresholdTransition);
            double maxThresh = 0.15 * Math.sin(((average) * Math.PI) / thresholdTransition);
            placePatch(context, context.getOrigin(), radius, minThreshold + minThresh, maxThreshold + maxThresh, average);
            return true;
        } return false;
    }


    public void placePatch(FeatureContext<DefaultFeatureConfig> context, BlockPos pos, int r, double min, double max, double average) {
        StructureWorldAccess world = context.getWorld();

        //Place Sculk Around Catalyst
        int timesFailed=0;
        int groupsFailed=1;
        int loop = (int) (8*Math.cos(((average)*Math.PI)/24) + 24);
        for (int l = 0; l < loop;) {
            double a = random() * 2 * PI;
            double rad = sqrt(2 + (groupsFailed - 1)) * sqrt(random());
            boolean succeed = placeSculk(pos.add((int) (rad * sin(a)), 0, (int) (rad * cos(a))), world);
            //Determine If Sculk Placement Was Successful And If Radius Should Be Increased
            if (!succeed) { ++timesFailed; } else {
                ++l;
                if (timesFailed>0) {--timesFailed; }
            }
            if (timesFailed>=10) {
                timesFailed=0;
                groupsFailed=groupsFailed+1;
            }
            if (sqrt(2 +(groupsFailed-1))>10) {
                break;
            }
        }

        //Place Sculk
        for (BlockPos blockpos : blockTagsInSphere(pos, r, SculkTags.SCULK_REPLACEABLE, world)) {
            double distance = pos.getSquaredDistance(blockpos);
            double maxA=max;
            double minA=min;
            double sampled = sample.sample(blockpos.getX()*multiplier, blockpos.getY()*multiplier,blockpos.getZ()*multiplier);
            if (r-3<=distance) { //Semi-Fade On Edges
                double equation = 0.15*(Math.sin((r-distance*PI)/6));
                maxA=maxA-equation;
                minA=minA+equation;
            }
            double dist = Math.max(r,distance*1.8);
            if (isWall(world, blockpos)) {
                double maxEq = (maxA)*(Math.sin((dist*PI)/r*2));
                double minEq = (minA)*(Math.sin((dist*PI)/r*2));
                maxA=maxA-maxEq;
                minA=minA+minEq;
            }
            if (isCeiling(world, blockpos)) {
                double maxEq = (maxA)*(Math.sin((dist*PI)/r*2));
                double minEq = (minA)*(Math.sin((dist*PI)/r*2));
                maxA=maxA-maxEq;
                minA=minA+minEq;
            }
            if (sampled>minA && sampled<maxA) {
                placeSculkOptim(blockpos, world);
            }
        }

        //Place Veins
        for (BlockPos blockpos : solidInSphere(pos, r, world)) {
            double minA=min;
            double sampled = sample.sample(blockpos.getX()*multiplier, blockpos.getY()*multiplier,blockpos.getZ()*multiplier);
            double distance = pos.getSquaredDistance(blockpos);
            if (r-3<=distance) { //Semi-Fade On Edges
                double equation = 0.15*(Math.sin((distance*PI)/6));
                minA=minA+equation;
            }
            double dist = Math.max(r,distance*1.8);
            if (isWall(world, blockpos)) {
                double minEq = (minA)*(Math.sin((dist*PI)/r*2));
                minA=minA+minEq;
            }
            if (isCeiling(world, blockpos)) {
                double minEq = (minA)*(Math.sin((dist*PI)/r*2));
                minA=minA+minEq;
            }
            if (sampled<minA && sampled>minA-0.16) {
                veins(blockpos, world);
            }
        }
        for (BlockPos blockpos : solidInSphere(pos, r, world)) {
            double distance = pos.getSquaredDistance(blockpos);
            double maxA=max;
            double sampled = sample.sample(blockpos.getX()*multiplier, blockpos.getY()*multiplier,blockpos.getZ()*multiplier);
            if (r-3<=distance) { //Semi-Fade On Edges
                double equation = 0.15*(Math.sin((distance*PI)/6));
                maxA=maxA-equation;
            }
            double dist = Math.max(r,distance*1.8);
            if (isWall(world, blockpos)) {
                double maxEq = (maxA)*(Math.sin((dist*PI)/r*2));
                maxA=maxA-maxEq;
            }
            if (isCeiling(world, blockpos)) {
                double maxEq = (maxA)*(Math.sin((dist*PI)/r*2));
                maxA=maxA-maxEq;
            }
            if (sampled>maxA && sampled<maxA+0.16) {
                veins(blockpos, world);
            }
        }
        for (BlockPos blockpos : hollowedSphere(pos, r+1, world)) {
            veins(blockpos, world);
        }

        //Place Activators
        for (BlockPos blockpos : blocksInSphere(pos, r, RegisterBlocks.SCULK, world)) {
            double sampled = sample.sample(blockpos.getX()*1.5, blockpos.getY()*1.5,blockpos.getZ()*1.5);
            if (SculkTags.blockTagContains(world.getBlockState(blockpos.up()).getBlock(), SculkTags.SCULK_VEIN_REPLACEABLE)) {
                Block activator = null;
                if (sampled<0.55 && sampled>0.41 && blockTagsInSphere(context.getOrigin(), 3, SculkTags.COMMON_ACTIVATORS, context.getWorld()).isEmpty()) {
                    activator=SculkTags.getRandomBlock(random, SculkTags.COMMON_ACTIVATORS);
                }
                if (sampled<1 && sampled>0.57 && blockTagsInSphere(context.getOrigin(), 6, SculkTags.RARE_ACTIVATORS, context.getWorld()).isEmpty()) {
                    activator=SculkTags.getRandomBlock(random, SculkTags.RARE_ACTIVATORS);
                }
                if (activator!=null) {
                    if (SculkTags.blockTagContains(activator, SculkTags.GROUND_ACTIVATORS)) {
                        world.setBlockState(blockpos.up(), activator.getDefaultState(), 0);
                    } else {
                        if ((world.getBlockState(blockpos.up()).contains(waterLogged) && world.getBlockState(blockpos.up()).get(waterLogged)) || world.getBlockState(blockpos.up()) == water) {
                            world.setBlockState(blockpos.up(), activator.getDefaultState().with(waterLogged, true), 0);
                        } else {
                            world.setBlockState(blockpos.up(), activator.getDefaultState(), 0);
                        }
                    }
                }
            }
        }
    }

    public static boolean placeSculk(BlockPos blockPos, StructureWorldAccess world) { //Call For Sculk & Call For Veins
        if (world.isChunkLoaded(blockPos)) {
            Block block = world.getBlockState(blockPos).getBlock();
            if (SculkTags.blockTagContains(block, SculkTags.SCULK_REPLACEABLE) && !SculkTags.blockTagContains(block, SculkTags.SCULK_VEIN_REPLACEABLE) && SculkTags.blockTagContains(block, SculkTags.SCULK) && airOrReplaceableUp(world, blockPos)) {
                placeSculkOptim(blockPos, world);
                fourDirVeins(blockPos, world);
                return true;
            } else {
                BlockPos NewSculk = sculkCheck(blockPos, world);
                if (NewSculk != null) {
                    block = world.getBlockState(NewSculk).getBlock();
                    if (SculkTags.blockTagContains(block, SculkTags.SCULK_REPLACEABLE)) {
                        placeSculkOptim(NewSculk, world);
                        fourDirVeins(NewSculk, world);
                        return true;
                    } else if (airveins(world, NewSculk)) {
                        Block blockUp = world.getBlockState(NewSculk.up()).getBlock();
                        if (SculkTags.blockTagContains(blockUp, SculkTags.SCULK_VEIN_REPLACEABLE) && blockUp != veinBlock) {
                            veins(NewSculk, world);
                            fourDirVeins(NewSculk, world);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void placeSculkOptim(BlockPos blockPos, StructureWorldAccess world) { //Place Sculk & Remove Veins
        if (world.isChunkLoaded(blockPos)) {
            world.setBlockState(blockPos, RegisterBlocks.SCULK.getDefaultState(), 0);
            for (Direction direction : Direction.values()) {
                BlockPos pos = blockPos.offset(direction);
                if (world.isChunkLoaded(pos)) {
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block == veinBlock) {
                        if (state.get(waterLogged)) { //If Vein Is Waterlogged
                            if (state.with(getOpposite(direction), false) == brokenWaterVein) {
                                world.setBlockState(pos, Blocks.WATER.getDefaultState(), 0);
                            } else {
                                world.setBlockState(pos, state.with(getOpposite(direction), false), 0);
                            }
                        } else { // If Vein Isn't Waterlogged
                            if (state.with(getOpposite(direction), false) == brokenVein) {
                                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
                            } else {
                                world.setBlockState(pos, state.with(getOpposite(direction), false), 0);
                            }
                        }
                    }
                    if (direction == Direction.UP) {
                        if (SculkTags.blockTagContains(block, SculkTags.SCULK_VEIN_REPLACEABLE) && block != waterBlock && !state.isAir()) {
                            if (SculkTags.blockTagContains(block, SculkTags.ALWAYS_WATER) || (state.contains(waterLogged) && state.get(waterLogged))) {
                                world.setBlockState(pos, Blocks.WATER.getDefaultState(), 0);
                            } else {
                                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
                            }
                        }
                    }
                }
            }
        }
    }
    public static void fourDirVeins(BlockPos blockpos, StructureWorldAccess world) {
        if (world.isChunkLoaded(blockpos)) {
            for (Direction direction : Direction.values()) {
                if (airveins(world, blockpos.offset(direction))) {
                    veins(blockpos.offset(direction), world);
                }
            }
        }
    }
    public static void veins(BlockPos blockpos, StructureWorldAccess world) {
        if (world.isChunkLoaded(blockpos)) {
            for (Direction direction : Direction.values()) {
                BlockPos pos1 = blockpos.offset(direction);
                if (world.isChunkLoaded(pos1)) {
                    BlockState state = world.getBlockState(pos1);
                    Block block = state.getBlock();
                    if (SculkTags.blockTagContains(block, SculkTags.ALWAYS_WATER) || state == Blocks.WATER.getDefaultState()) {
                        world.setBlockState(pos1, RegisterBlocks.SCULK_VEIN.getDefaultState().with(waterLogged, true).with(getOpposite(direction), true), 0);
                    } else if (block != waterBlock) {
                        if (block == veinBlock) {
                            world.setBlockState(pos1, state.with(getOpposite(direction), true), 0);
                        } else if (SculkTags.blockTagContains(block, SculkTags.SCULK_VEIN_REPLACEABLE) || state.isAir()) {
                            world.setBlockState(pos1, RegisterBlocks.SCULK_VEIN.getDefaultState().with(getOpposite(direction), true), 0);
                        }
                    }
                }
            }
        }
    }

    public static BlockPos sculkCheck(BlockPos blockPos, StructureWorldAccess world) { //Call For Up&Down Checks
        BlockPos check = checkPt2(blockPos, world);
        if (check!=null) { return check; }
        if (!world.isSkyVisible(blockPos)) {
            return checkPt1(blockPos, world);
        } return null;
    }
    public static BlockPos checkPt1(BlockPos blockPos, StructureWorldAccess world) { //Check For Valid Placement Above
        int upward = 8;
        int MAX = world.getHeight();
        if (blockPos.getY() + upward >= MAX) {
            upward = (MAX - blockPos.getY())-1;
        }
        for (int h = 0; h < upward; h++) {
            BlockPos pos =  blockPos.up(h);
            Block block = world.getBlockState(pos).getBlock();
            if (!SculkTags.blockTagContains(block, SculkTags.SCULK_VEIN_REPLACEABLE) && !SculkTags.blockTagContains(block, SculkTags.SCULK) && airOrReplaceableUp(world, pos)) {
                return pos;
            }
        }
        return null;
    }
    public static BlockPos checkPt2(BlockPos blockPos, StructureWorldAccess world) { //Check For Valid Placement Below
        int downward = 4;
        int MIN = world.getBottomY();
        if (blockPos.getY() - downward <= MIN) {
            downward = (blockPos.getY()-MIN)-1;
        }
        for (int h = 0; h < downward; h++) {
            BlockPos pos = blockPos.down(h);
            Block block = world.getBlockState(pos).getBlock();
            if (!SculkTags.blockTagContains(block, SculkTags.SCULK_VEIN_REPLACEABLE) && !SculkTags.blockTagContains(block, SculkTags.SCULK) && airOrReplaceableUp(world, pos)) {
                return pos;
            }
        }
        return null;
    }

    public static boolean airveins(StructureWorldAccess world, BlockPos blockPos) { //Check If Veins Are Above Invalid Block
        if (blockPos==null) { return false; }
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();
        Fluid fluid = world.getFluidState(blockPos).getFluid();
        return world.isChunkLoaded(blockPos) && !SculkTags.blockTagContains(block, SculkTags.SCULK) && !SculkTags.blockTagContains(block, SculkTags.SCULK_UNTOUCHABLE) && !SculkTags.blockTagContains(block, SculkTags.SCULK_VEIN_REPLACEABLE) && !state.isAir() && !SculkTags.fluidTagContains(fluid, FluidTags.WATER) && !SculkTags.fluidTagContains(fluid, FluidTags.LAVA);
    }

    public static boolean airOrReplaceableUp(StructureWorldAccess world, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            BlockState state = world.getBlockState(blockPos.offset(direction));
            if (!state.isFullCube(world, blockPos)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWall(StructureWorldAccess world, BlockPos pos) {
        if (world.getBlockState(pos.down()).isFullCube(world, pos.down()) && airOrReplaceableUp(world, pos.down())) {
            return true;
        }
        return world.getBlockState(pos.up()).isFullCube(world, pos.up()) && airOrReplaceableUp(world, pos.up());
    }

    public static boolean isCeiling(StructureWorldAccess world, BlockPos pos) {
        return world.getBlockState(pos.up()).isFullCube(world, pos.up()) && !world.getBlockState(pos.down()).isFullCube(world, pos.down());
    }

    public static BooleanProperty getOpposite(Direction direction) {
        if (direction==Direction.UP) { return Properties.DOWN; }
        if (direction==Direction.DOWN) { return Properties.UP; }
        if (direction==Direction.NORTH) { return Properties.SOUTH; }
        if (direction==Direction.SOUTH) { return Properties.NORTH; }
        if (direction==Direction.EAST) { return Properties.WEST; }
        if (direction==Direction.WEST) { return Properties.EAST; }
        return Properties.DOWN;
    }

    public static final BlockState brokenVein = SculkVeinBlock.SCULK_VEIN.getDefaultState().with(Properties.DOWN, false);
    public static final BlockState water = Blocks.WATER.getDefaultState();
    public static final BlockState air = Blocks.AIR.getDefaultState();
    public static final Block waterBlock = Blocks.WATER;
    public static final Block veinBlock = SculkVeinBlock.SCULK_VEIN;
    public static final BooleanProperty waterLogged = Properties.WATERLOGGED;
    public static final BlockState brokenWaterVein = SculkVeinBlock.SCULK_VEIN.getDefaultState().with(Properties.DOWN, false).with(waterLogged, true);

    public static ArrayList<BlockPos> blockTagsInSphere(BlockPos pos, int radius, TagKey<Block> tag, StructureWorldAccess world) {
        int bx = pos.getX();
        int by = pos.getY();
        int bz = pos.getZ();
        ArrayList<BlockPos> blocks = new ArrayList<>();
        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));
                    if(distance < radius * radius) {
                        BlockPos l = new BlockPos(x, y, z);
                        if (SculkTags.blockTagContains(world.getBlockState(l).getBlock(), tag)) {
                            blocks.add(l);
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public static ArrayList<BlockPos> solidInSphere(BlockPos pos, int radius, StructureWorldAccess world) {
        int bx = pos.getX();
        int by = pos.getY();
        int bz = pos.getZ();
        ArrayList<BlockPos> blocks = new ArrayList<>();
        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));
                    if(distance < radius * radius) {
                        BlockPos l = new BlockPos(x, y, z);
                        if (world.getBlockState(l).isFullCube(world, l) && !SculkTags.blockTagContains(world.getBlockState(l).getBlock(), SculkTags.SCULK) && world.isChunkLoaded(l)) {
                            blocks.add(l);
                        }
                    }
                }
            }
        }
        return blocks;
    }
    public static ArrayList<BlockPos> blocksInSphere(BlockPos pos, int radius, Block block, StructureWorldAccess world) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        int bx = pos.getX();
        int by = pos.getY();
        int bz = pos.getZ();
        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));
                    if(distance < radius * radius) {
                        BlockPos l = new BlockPos(x, y, z);
                        if (world.getBlockState(l).getBlock() == block && world.isChunkLoaded(l)) {
                            blocks.add(l);
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public static ArrayList<BlockPos> hollowedSphere(BlockPos pos, int radius, StructureWorldAccess world) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        int bx = pos.getX();
        int by = pos.getY();
        int bz = pos.getZ();
        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
                    if (distance < radius * radius && !(distance < (radius - 1) * (radius - 1))) {
                        BlockPos l = new BlockPos(x, y, z);
                        if (world.getBlockState(l).isFullCube(world, l) && !SculkTags.blockTagContains(world.getBlockState(l).getBlock(), SculkTags.SCULK) && world.isChunkLoaded(l)) {
                            blocks.add(l);
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public static boolean blockInSphere(BlockPos pos, int radius, Block block, StructureWorldAccess world) {
        int bx = pos.getX();
        int by = pos.getY();
        int bz = pos.getZ();

        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));
                    if(distance < radius * radius) {
                        BlockPos l = new BlockPos(x, y, z);
                        if (world.getBlockState(l).getBlock()==block) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }
}
