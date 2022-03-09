package frozenblock.wild.mod.worldgen;

import com.mojang.serialization.Codec;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.fromAccurateSculk.SculkTags;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

import static java.lang.Math.*;

public class RandomVeinsFeature extends Feature<DefaultFeatureConfig> {
    Random random = new Random();
    public RandomVeinsFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        double average = (context.getOrigin().getX() + context.getOrigin().getZ()) * 0.5;
        placeVeins(context, context.getOrigin(), average);
        return true;
    }


    public void placeVeins(FeatureContext<DefaultFeatureConfig> context, BlockPos pos, double average) {
        StructureWorldAccess world = context.getWorld();
        int loop = random.nextInt(2,14);
        for (int l = 0; l < loop; l++) {
            double a = random() * 2 * PI;
            double rad = sqrt(3) * sqrt(random());
            fourDirVeins(pos.add((int) (rad * sin(a)), 0, (int) (rad * cos(a))), world);
        }
    }

    public static void fourDirVeins(BlockPos blockpos, StructureWorldAccess world) {
        if (world.isChunkLoaded(blockpos)) {
        for (Direction direction : Direction.values()) {
            if (airveins(world, blockpos.offset(direction))) {
                veins(blockpos.offset(direction), world);
            } else { BlockPos check = sculkCheck(blockpos, world);
                if (airveins(world, check)) {
                    veins(check, world);
                }
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
        return checkPt1(blockPos, world);
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
}