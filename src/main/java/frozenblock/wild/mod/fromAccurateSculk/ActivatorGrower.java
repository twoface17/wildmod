package frozenblock.wild.mod.fromAccurateSculk;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.blocks.SculkShriekerBlock;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.liukrastapi.Sphere;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

import static java.lang.Math.*;


public class ActivatorGrower {
    public static final BlockState sculk = RegisterBlocks.SCULK.getDefaultState();
    public static final BlockState sensor = Blocks.SCULK_SENSOR.getDefaultState();
    public static final BlockState shrieker = SculkShriekerBlock.SCULK_SHRIEKER_BLOCK.getDefaultState();
    public static final BlockState vein = SculkVeinBlock.SCULK_VEIN.getDefaultState();
    public static final BlockState water = Blocks.WATER.getDefaultState();
    public static final Block waterBlock = Blocks.WATER;
    public static final BooleanProperty waterLogged = Properties.WATERLOGGED;

    public void placeActiveOmptim(int loop, int rVal, BlockPos pos, World world) { //Call For Placement
            for (int l = 0; l < loop; ++l) {
                double a = random() * 2 * PI;
                double r = sqrt(rVal) * sqrt(random());
                int x = (int) (r * cos(a));
                int y = (int) (r * sin(a));
                placeActivator(pos.add(x, 0, y), world, loop/4);
            }
        }

    public void placeActivator(BlockPos blockPos, World world, int chance) { //Place Activators
        int chanceCheck = (chance + 4);
        int uniInt = UniformIntProvider.create(1, 20).get(world.getRandom());
        if ((UniformIntProvider.create(0, chance + 5).get(world.getRandom()) > chanceCheck)) {
            BlockPos NewSculk = solidsculkCheck(blockPos, world);
            if (NewSculk != null  && !checkForOthers(NewSculk, world)) {
                BlockState stateUp = world.getBlockState(NewSculk.up());
                if (uniInt <= 16) {
                    if (stateUp == water) {
                        world.setBlockState(NewSculk.up(), sensor.with(waterLogged, true));
                    } else if (stateUp.getBlock() !=waterBlock) {
                        if (stateUp == vein.with(waterLogged, true)) {
                            world.setBlockState(NewSculk.up(), sensor.with(waterLogged, true));
                        } else {
                            world.removeBlock(NewSculk.up(), true);
                            world.setBlockState(NewSculk.up(), sensor);
                        }
                    }
                } else {
                    if (stateUp == water) {
                        world.setBlockState(NewSculk.up(), shrieker.with(waterLogged, true));
                    } else if (stateUp.getBlock() != waterBlock) {
                        if (stateUp == vein.with(waterLogged, true)) {
                            world.setBlockState(NewSculk.up(), shrieker.with(waterLogged, true));
                        } else {
                            world.removeBlock(NewSculk.up(), true);
                            world.setBlockState(NewSculk.up(), shrieker);
                        }
                    }
                }
            }
        }
    }
    public boolean checkForOthers(BlockPos pos, World world) {
        boolean bl1 = Sphere.sphereBlock(sensor.getBlock(), world, pos, 3);
        boolean bl2 = Sphere.sphereBlock(shrieker.getBlock(), world, pos, 3);
        return bl1 || bl2;
    }

    /** CAlCULATIONS & CHECKS */
    public static BlockPos solidsculkCheck(BlockPos blockPos, World world) { //Call For Up&Down Checks
        if (checkPt2(blockPos, world)!=null) {
            return checkPt2(blockPos, world);
        } else if (checkPt1(blockPos, world)!=null) {
            return checkPt1(blockPos, world);
        } else { return null; }
    }
    public static BlockPos checkPt1(BlockPos blockPos, World world) { //Check For Valid Placement Above
        int upward = world.getGameRules().getInt(WildMod.UPWARD_SPREAD);
        int MAX = world.getHeight();
        if (blockPos.getY() + upward >= MAX) {
            upward = (MAX - blockPos.getY())-1;
        }
        for (int h = 0; h < upward; h++) {
            if (solrepsculk(world, blockPos.up(h))) {
                return blockPos.up(h);
            }
        }
        return null;
    }
    public static BlockPos checkPt2(BlockPos blockPos, World world) { //Check For Valid Placement Below
        int downward = world.getGameRules().getInt(WildMod.DOWNWARD_SPREAD);
        int MIN = world.getBottomY();
        if (blockPos.getY() - downward <= MIN) {
            downward = (blockPos.getY()-MIN)-1;
        }
        for (int h = 0; h < downward; h++) {
            if (solrepsculk(world, blockPos.down(h))) {
                return blockPos.down(h);
            }
        }
        return null;
    }

    public static boolean solrepsculk(World world, BlockPos blockPos) {
        Block block = world.getBlockState(blockPos).getBlock();
        return (block==sculk.getBlock() && SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.up()).getBlock()));
    }
}
