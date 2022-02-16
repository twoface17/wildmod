package frozenblock.wild.mod.worldgen;

import com.mojang.serialization.Codec;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.fromAccurateSculk.SculkTags;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.random.SimpleRandom;

import java.util.ArrayList;

import static java.lang.Math.PI;

public class CrawlingVeinsFeature extends Feature<DefaultFeatureConfig> {

    /** NOISE VARIABLES */
    public static final double multiplier = 0.17; //Lowering this makes the noise bigger, raising it makes it smaller (more like static)
    public static final double minThreshold = -0.1; //The value that Vein's noise must be ABOVE in order to grow
    public static final double maxThreshold = 0.2; //The value that Vein's noise must be BELOW in order to grow
    public static long seed = 1; //This gets set to the current world's seed in generate()
    public static final int thresholdTransition=27; //When this is lower, the min&max thresholds for Sculk placement will quickly fluctuate based on location. When higher, the min&max thresholds will have a longer, but smoother transition.
    public static PerlinNoiseSampler sample = new PerlinNoiseSampler(new SimpleRandom(seed));

    public CrawlingVeinsFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        if (seed!=context.getWorld().getSeed()) {
            seed=context.getWorld().getSeed();
            sample = new PerlinNoiseSampler(new SimpleRandom(seed));
        }
        double average = (context.getOrigin().getX() + context.getOrigin().getZ()) * 0.5;
        int radius = (int) (8 * Math.cos(((average) * Math.PI) / 36) + 16);
        double minThresh = 0.1 * Math.cos(((average) * Math.PI) / thresholdTransition);
        double maxThresh = 0.15 * Math.sin(((average) * Math.PI) / thresholdTransition);
        placeVeins(context, context.getOrigin(), radius, minThreshold + minThresh, maxThreshold + maxThresh, average);
        return true;
    }


    public void placeVeins(FeatureContext<DefaultFeatureConfig> context, BlockPos pos, int r, double min, double max, double average) {
        StructureWorldAccess world = context.getWorld();

        //Place Veins
        for (BlockPos blockpos : solidInSphere(pos, r, world)) {
            double minA=min;
            double sampled = sample.sample(blockpos.getX()*multiplier, blockpos.getY()*multiplier,blockpos.getZ()*multiplier);
            double distance = pos.getSquaredDistance(blockpos,false);
            if (r-3<=distance) { //Semi-Fade On Edges
                double equation = 0.15*(Math.sin((distance*PI)/6));
                minA=minA+equation;
            }
            double dist = Math.max(r,distance*1.8);
            if (isWall(world, blockpos)) {
                double minEq = (minA/3)*(Math.sin((dist*PI)/r*2));
                minA=minA+minEq;
            }
            if (isCeiling(world, blockpos)) {
                double minEq = (minA/3)*(Math.sin((dist*PI)/r*2));
                minA=minA+minEq;
            }
            if (sampled<minA && sampled>minA-0.16) {
                veins(blockpos, world);
            }
        }
        for (BlockPos blockpos : solidInSphere(pos, r, world)) {
            double distance = pos.getSquaredDistance(blockpos,false);
            double maxA=max;
            double sampled = sample.sample(blockpos.getX()*multiplier, blockpos.getY()*multiplier,blockpos.getZ()*multiplier);
            if (r-3<=distance) { //Semi-Fade On Edges
                double equation = 0.15*(Math.sin((distance*PI)/6));
                maxA=maxA-equation;
            }
            double dist = Math.max(r,distance*1.8);
            if (isWall(world, blockpos)) {
                double maxEq = (maxA/3)*(Math.sin((dist*PI)/r*2));
                maxA=maxA-maxEq;
            }
            if (isCeiling(world, blockpos)) {
                double maxEq = (maxA/3)*(Math.sin((dist*PI)/r*2));
                maxA=maxA-maxEq;
            }
            if (sampled>maxA && sampled<maxA+0.16) {
                veins(blockpos, world);
            }
        }
        for (BlockPos blockpos : hollowedSphere(pos, r+1, world)) {
            veins(blockpos, world);
        }

    }

    public static void veins(BlockPos blockpos, StructureWorldAccess world) {
        if (world.isChunkLoaded(blockpos)) {
            for (Direction direction : Direction.values()) {
                BlockPos pos1 = blockpos.offset(direction);
                if (world.isChunkLoaded(pos1)) {
                    BlockState state = world.getBlockState(pos1);
                    Block block = state.getBlock();
                    if (SculkTags.ALWAYS_WATER.contains(block) || state == Blocks.WATER.getDefaultState()) {
                        world.setBlockState(pos1, RegisterBlocks.SCULK_VEIN.getDefaultState().with(waterLogged, true).with(getOpposite(direction), true), 0);
                    } else if (block != waterBlock) {
                        if (block == veinBlock) {
                            world.setBlockState(pos1, state.with(getOpposite(direction), true), 0);
                        } else if (SculkTags.SCULK_VEIN_REPLACEABLE.contains(block) || state.isAir()) {
                            world.setBlockState(pos1, RegisterBlocks.SCULK_VEIN.getDefaultState().with(getOpposite(direction), true), 0);
                        }
                    }
                }
            }
        }
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

    public static final BlockState water = Blocks.WATER.getDefaultState();
    public static final Block waterBlock = Blocks.WATER;
    public static final Block veinBlock = SculkVeinBlock.SCULK_VEIN;
    public static final BooleanProperty waterLogged = Properties.WATERLOGGED;

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
                        if (world.getBlockState(l).isFullCube(world, l) && !SculkTags.SCULK.contains(world.getBlockState(l).getBlock()) && world.isChunkLoaded(l)) {
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
                        if (world.getBlockState(l).isFullCube(world, l) && !SculkTags.SCULK.contains(world.getBlockState(l).getBlock()) && world.isChunkLoaded(l)) {
                            blocks.add(l);
                        }
                    }
                }
            }
        }
        return blocks;
    }
}