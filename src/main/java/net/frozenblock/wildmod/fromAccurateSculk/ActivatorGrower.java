package net.frozenblock.wildmod.fromAccurateSculk;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.misc.Sphere;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.frozenblock.wildmod.registry.RegisterTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;

import static java.lang.Math.*;


public class ActivatorGrower {
    public static final BlockState sculk = RegisterBlocks.SCULK.getDefaultState();
    public static final BlockState vein = RegisterBlocks.SCULK_VEIN.getDefaultState();
    public static final BlockState water = Blocks.WATER.getDefaultState();
    public static final Block waterBlock = Blocks.WATER;
    public static final Block sculkBlock = RegisterBlocks.SCULK;
    public static final BooleanProperty waterLogged = Properties.WATERLOGGED;
    public static final Random random = new Random();

    public static void startGrowing(int loop, int rVal, BlockPos pos, World world) { //Call For Placement
        for (int l = 0; l < loop; ++l) {
            double a = random() * 2 * PI;
            double r = sqrt(rVal) * sqrt(random());
            int x = (int) (r * cos(a));
            int y = (int) (r * sin(a));
            selectActivator(pos.add(x, 0, y), world, loop / 5);
        }
    }

    public static void selectActivator(BlockPos blockPos, World world, int chance) { //Get Activator To Place
        int uniInt = UniformIntProvider.create(1, 20).get(world.getRandom());
        if ((UniformIntProvider.create(0, chance + 5).get(world.getRandom()) > (chance + 4))) {
            BlockPos NewSculk = solidsculkCheck(blockPos, world);
            if (NewSculk != null && !Sphere.blockTagInSphere(NewSculk, 4, RegisterTags.ACTIVATORS, world)) {
                BlockState activator = null;
                if (uniInt <= 3) {
                    activator = Objects.requireNonNull(RegisterTags.getRandomBlock(random, RegisterTags.RARE_ACTIVATORS)).getDefaultState();
                } else if (uniInt <= 16) {
                    activator = Objects.requireNonNull(RegisterTags.getRandomBlock(random, RegisterTags.COMMON_ACTIVATORS)).getDefaultState();
                }
                if (activator != null) {
                    placeActivator(NewSculk, world, activator);
                }
            }
        }
    }

    public static void placeActivator(BlockPos pos, World world, BlockState state) { //Place The Activator
        if (RegisterTags.blockTagContains(state.getBlock(), RegisterTags.GROUND_ACTIVATORS)) {
            world.setBlockState(pos, state);
        } else {
            BlockState stateUp = world.getBlockState(pos.up());
            if (stateUp == water && state.contains(waterLogged)) {
                world.setBlockState(pos.up(), state.with(waterLogged, true));
            } else if (stateUp.getBlock() != waterBlock) {
                if (stateUp == vein.with(waterLogged, true) && state.contains(waterLogged)) {
                    world.setBlockState(pos.up(), state.with(waterLogged, true));
                } else {
                    world.setBlockState(pos.up(), state);
                }
            }
        }
    }

    /**
     * CAlCULATIONS & CHECKS
     */
    public static BlockPos solidsculkCheck(BlockPos blockPos, World world) { //Call For Up&Down Checks
        BlockPos check = checkPt2(blockPos, world);
        if (check != null) {
            return check;
        }
        if (!world.isSkyVisible(blockPos)) {
            return checkPt1(blockPos, world);
        }
        return null;
    }

    public static BlockPos checkPt1(BlockPos blockPos, World world) { //Check For Valid Placement Above
        int upward = world.getGameRules().getInt(WildMod.UPWARD_SPREAD);
        int MAX = world.getHeight();
        if (blockPos.getY() + upward >= MAX) {
            upward = (MAX - blockPos.getY()) - 1;
        }
        for (int h = 0; h < upward; h++) {
            BlockPos pos = blockPos.up(h);
            if (world.getBlockState(pos) == sculk && RegisterTags.blockTagContains(world.getBlockState(pos.up()).getBlock(), RegisterTags.SCULK_VEIN_REPLACEABLE)) {
                return pos;
            }
        }
        return null;
    }

    public static BlockPos checkPt2(BlockPos blockPos, World world) { //Check For Valid Placement Below
        int downward = world.getGameRules().getInt(WildMod.DOWNWARD_SPREAD);
        int MIN = world.getBottomY();
        if (blockPos.getY() - downward <= MIN) {
            downward = (blockPos.getY() - MIN) - 1;
        }
        for (int h = 0; h < downward; h++) {
            BlockPos pos = blockPos.down(h);
            if (world.getBlockState(pos) == sculk && RegisterTags.blockTagContains(world.getBlockState(pos.up()).getBlock(), RegisterTags.SCULK_VEIN_REPLACEABLE)) {
                return pos;
            }
        }
        return null;
    }
}
