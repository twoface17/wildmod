package frozenblock.wild.mod.worldgen;

import com.mojang.serialization.Codec;
import frozenblock.wild.mod.blocks.SculkShriekerBlock;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.fromAccurateSculk.SculkTags;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.Tag;
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

public class SculkPatchFeature extends Feature<DefaultFeatureConfig> {
    Random random = new Random();
    public SculkPatchFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    //alex im sorry that i made your code a mess but it's the only way i know how to do this

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        if (seed!=context.getWorld().getSeed()) {
            seed=context.getWorld().getSeed();
            sample = new PerlinNoiseSampler(new SimpleRandom(seed));
        }
        context.getWorld().setBlockState(context.getOrigin().up(), RegisterBlocks.SCULK_CATALYST.getDefaultState(), 0);
        if (context.getWorld().getBlockEntity(context.getOrigin())==null) {
            context.getWorld().setBlockState(context.getOrigin(), RegisterBlocks.SCULK.getDefaultState(), 0);
        }
        double average = (context.getOrigin().getX() + context.getOrigin().getZ())*0.5;
        int radius = (int) (8*Math.cos((average*Math.PI)/24) + 16);
        placePatch(context, context.getOrigin(), radius);
        return true;
    }
    /** NOISE VARIABLES */
    public static final double multiplier = 0.20; //Lowering this makes the noise bigger, raising it makes it smaller (more like static)
    public static final double minThreshold = -0.3; //The value that outer Sculk's noise must be ABOVE in order to grow
    public static final double maxThreshold = 0.3; //The value that outer Sculk's noise must be BELOW in order to grow
    public static long seed = 1;
    public static PerlinNoiseSampler sample = new PerlinNoiseSampler(new SimpleRandom(seed));

    public void placePatch(FeatureContext<DefaultFeatureConfig> context, BlockPos pos, int r) {
        StructureWorldAccess world = context.getWorld();
        //Place Sculk
        for (BlockPos blockpos : blockTagsInSphere(pos, r, SculkTags.BLOCK_REPLACEABLE, world)) {
            double sampled = sample.sample(blockpos.getX()*multiplier, blockpos.getY()*multiplier,blockpos.getZ()*multiplier);
            if (sampled>minThreshold && sampled<maxThreshold && blockpos.getY()<maxSculk) {
                world.setBlockState(blockpos, RegisterBlocks.SCULK.getDefaultState(), 0);
            }
        }

        //Place Veins
        for (BlockPos blockpos : solidInSphere(pos, r, world)) {
            double sampled = sample.sample(blockpos.getX()*multiplier, blockpos.getY()*multiplier,blockpos.getZ()*multiplier);
            if (sampled<minThreshold && sampled>minThreshold-0.16 && blockpos.getY()<maxSculk+2) {
                veins(blockpos, world);
            }
        }
        for (BlockPos blockpos : solidInSphere(pos, r, world)) {
            double sampled = sample.sample(blockpos.getX()*multiplier, blockpos.getY()*multiplier,blockpos.getZ()*multiplier);
            if (sampled>maxThreshold && sampled<maxThreshold+0.16 && blockpos.getY()<maxSculk+2) {
                veins(blockpos, world);
            }
        }
        for (BlockPos blockpos : hollowedSphere(pos, r+1, world)) {
            veins(blockpos, world);
        }

        //Place Activators
        for (BlockPos blockpos : blocksInSphere(pos, r, RegisterBlocks.SCULK, world)) {
            double sampled = sample.sample(blockpos.getX(), blockpos.getY(),blockpos.getZ());
            if (SculkTags.SCULK_VEIN_REPLACEABLE.contains(world.getBlockState(blockpos.up()).getBlock())) {
                //SENSOR
                if (sampled<0.55 && sampled>0.41 && blockpos.getY()<maxSculk) {
                    if ((world.getBlockState(blockpos.up()).contains(waterLogged) && world.getBlockState(blockpos.up()).get(waterLogged)) || world.getBlockState(blockpos.up()) == water) {
                        world.setBlockState(blockpos.up(), Blocks.SCULK_SENSOR.getDefaultState().with(waterLogged, true), 0);
                    } else {
                        world.setBlockState(blockpos.up(), Blocks.SCULK_SENSOR.getDefaultState(), 0);
                    }
                }
                //SHRIEKER
                if (sampled<1 && sampled>0.60 && blockpos.getY()<maxSculk) {
                    if ((world.getBlockState(blockpos.up()).contains(waterLogged) && world.getBlockState(blockpos.up()).get(waterLogged)) || world.getBlockState(blockpos.up())==water) {
                        world.setBlockState(blockpos.up(), SculkShriekerBlock.SCULK_SHRIEKER_BLOCK.getDefaultState().with(waterLogged, true), 0);
                } else {
                        world.setBlockState(blockpos.up(), SculkShriekerBlock.SCULK_SHRIEKER_BLOCK.getDefaultState(), 0); }
                }
            }
        }
    }



    public static void veins(BlockPos blockpos, StructureWorldAccess world) {
        for (Direction direction : Direction.values()) {
            BlockPos pos1 = blockpos.offset(direction);
            BlockState state = world.getBlockState(pos1);
            Block block = state.getBlock();
            if (SculkTags.ALWAYS_WATER.contains(block) || state == Blocks.WATER.getDefaultState()) {
                world.setBlockState(pos1, RegisterBlocks.SCULK_VEIN.getDefaultState().with(waterLogged, true).with(getOpposite(direction), true),0);
            } else if (block != waterBlock) {
                if (block == veinBlock) {
                    world.setBlockState(pos1, state.with(getOpposite(direction), true),0);
                } else if (SculkTags.SCULK_VEIN_REPLACEABLE.contains(block) || state.isAir()) {
                    world.setBlockState(pos1, RegisterBlocks.SCULK_VEIN.getDefaultState().with(getOpposite(direction), true),0);
                }
            }
        }
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
    public static final int maxSculk = 56;

    public static ArrayList<BlockPos> blockTagsInSphere(BlockPos pos, int radius, Tag<Block> tag, StructureWorldAccess world) {
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
                        if (tag.contains(world.getBlockState(l).getBlock())) {
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
                        if (world.getBlockState(l).isFullCube(world, l) && !SculkTags.SCULK.contains(world.getBlockState(l).getBlock())) {
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
                        if (world.getBlockState(l).getBlock() == block) {
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
                        if (world.getBlockState(l).isFullCube(world, l) && !SculkTags.SCULK.contains(world.getBlockState(l).getBlock())) {
                            blocks.add(l);
                        }
                    }
                }
            }
        }
        return blocks;
    }
}