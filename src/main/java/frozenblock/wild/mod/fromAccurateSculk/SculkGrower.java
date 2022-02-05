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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.*;

public class SculkGrower {
    /** DEFAULT VARIABLES */
    public static final BlockState vein = SculkVeinBlock.SCULK_VEIN.getDefaultState().with(Properties.DOWN, true);
    public static final BlockState water = Blocks.WATER.getDefaultState();
    public static final BlockState air = Blocks.AIR.getDefaultState();
    public static final Block waterBlock = Blocks.WATER;
    public static final BooleanProperty waterLogged = Properties.WATERLOGGED;

    /** MAIN CODE */
    public static void sculk(BlockPos blockPos, World world, @Nullable Entity entity, int catalysts) { //Choose Amount Of Sculk + Initial Radius
        if (entity!=null) {
            world.playSound(null, blockPos, RegisterSounds.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 1F, 1F);
            BlockPos down = blockPos.down();
            if (SculkTags.THREE.contains(entity.getType())) {
                sculkOptim(3*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            } else if (SculkTags.FIVE.contains(entity.getType())) {
                sculkOptim(5*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            } else if (SculkTags.TEN.contains(entity.getType())) {
                sculkOptim(10*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            } else if (SculkTags.TWENTY.contains(entity.getType())) {
                sculkOptim(20*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            } else if (SculkTags.FIFTY.contains(entity.getType())) {
                sculkOptim(50*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            } else if (SculkTags.ONEHUNDRED.contains(entity.getType())) {
                sculkOptim(500*catalysts, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            }
        }
    }

    public static void sculkOptim(float loop, int rVal, BlockPos down, World world) { //Call For Sculk Placement & Increase Radius If Stuck
        int rVal2 = MathHelper.clamp(rVal*world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER),1, 64);
        int timesFailed=0;
        int groupsFailed=1;
        float fLoop = loop * world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER);

        for (int l = 0; l < fLoop;) {
            double a = random() * 2 * PI;
            double r = sqrt(rVal2+(groupsFailed-1)) * sqrt(random());
            boolean succeed = placeSculk(down.add((int) (r * sin(a)), 0, (int) (r * cos(a))), world);
            if (!succeed) { ++timesFailed; } else { ++l; }
            if (timesFailed>=10) {
                timesFailed=0;
                groupsFailed=groupsFailed+2;
            }
            if (rVal2+(groupsFailed-1)>50) {
                break;
            }
        }
        setCatalysts(world, down.up(),rVal2+(groupsFailed-1));
    }

    public static boolean placeSculk(BlockPos blockPos, World world) { //Call For Sculk & Call For Veins
        BlockPos NewSculk;
        if (SculkTags.BLOCK_REPLACEABLE.contains(world.getBlockState(blockPos).getBlock()) && solrepsculk(world, blockPos)) {
            NewSculk = blockPos;
            placeSculkOptim(NewSculk, world);
            return true;
        } else {
            NewSculk = sculkCheck(blockPos, world);
            if (NewSculk != null) {
                if (SculkTags.BLOCK_REPLACEABLE.contains(world.getBlockState(NewSculk).getBlock())) {
                    placeSculkOptim(NewSculk, world);
                    return true;
                } else if (solid(world, NewSculk)) {
                    if (!SculkTags.SCULK.contains(world.getBlockState(NewSculk.up()).getBlock())) {
                        veins(NewSculk, world);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void placeSculkOptim(BlockPos NewSculk, World world) { //Place Sculk & Call For Veins
        veins(NewSculk, world);
        world.setBlockState(NewSculk, SculkBlock.SCULK_BLOCK.getDefaultState());
        BlockState upBlock = world.getBlockState(NewSculk.up());
        if (upBlock.getBlock()!=waterBlock) {
            if (upBlock.contains(waterLogged) && upBlock.get(waterLogged)) {
                world.setBlockState(NewSculk.up(), water);
            } else if (SculkTags.SCULK_REPLACEABLE.contains(upBlock.getBlock())) { world.setBlockState(NewSculk.up(), air); }
        }
    }

    public static void veins(BlockPos blockPos, World world) { //Calculate Vein Placement
        veinsPartTwo(world,blockPos.add(1, 1, 0),blockPos.add(1, 0, 0));
        veinsPartTwo(world,blockPos.add(-1, 1, 0),blockPos.add(-1, 0, 0));
        veinsPartTwo(world,blockPos.add(0, 1, 1),blockPos.add(0, 0, 1));
        veinsPartTwo(world,blockPos.add(0, 1, -1),blockPos.add(0, 0, -1));
        veinsPartTwo(world,blockPos.up(),blockPos);
    }

    public static void veinsPartTwo(World world, BlockPos one, BlockPos two) {
        if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(one).getBlock()) && solid(world, two) && airveins(world, two)) {
            veinPlaceOptim(one, world);
        } else { BlockPos check = sculkCheck(one, world);
            if (check != null && solidrep(world, check) && airveins(world, check)) {
                veinPlaceOptim(check.up(), world);
            }
        }
    }

    public static void veinPlaceOptim(BlockPos curr, World world) { //Place Veins
        if (SculkTags.ALWAYS_WATER.contains(world.getBlockState(curr).getBlock()) || world.getBlockState(curr)==Blocks.WATER.getDefaultState()) {
            world.setBlockState(curr, vein.with(Properties.WATERLOGGED, true));
            tiltVeins(curr, world);
            tiltVeinsDown(curr, world);
        } else if (world.getBlockState(curr).getBlock() != Blocks.WATER) {
            if (world.getBlockState(curr).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                world.setBlockState(curr, world.getBlockState(curr).with(Properties.DOWN, true));
                tiltVeins(curr, world);
                tiltVeinsDown(curr, world);
            } else
                world.setBlockState(curr, vein);
            tiltVeins(curr, world);
            tiltVeinsDown(curr, world);
        }
    }

    /** BLOCKSTATE TWEAKING */
    public static void tiltVeins(BlockPos blockPos, World world) { //Tilt Sculk Veins
        if (!SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.down())).getBlock())) {
            if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(1, 1, 0)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(1, 0, 0))).getBlock())) {
                world.setBlockState(blockPos, world.getBlockState(blockPos).with(Properties.EAST, true));
            }
            if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(-1, 1, 0)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(-1, 0, 0))).getBlock())) {
                world.setBlockState(blockPos, world.getBlockState(blockPos).with(Properties.WEST, true));
            }
            if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(0, 1, -1)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(0, 0, -1))).getBlock())) {
                world.setBlockState(blockPos, world.getBlockState(blockPos).with(Properties.NORTH, true));
            }
            if (SculkTags.VEIN_CONNECTABLE.contains(world.getBlockState(blockPos.add(0, 1, 1)).getBlock()) && !SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.add(0, 0, 1))).getBlock())) {
                world.setBlockState(blockPos, world.getBlockState(blockPos).with(Properties.SOUTH, true));
            }
        }
    }
    public static void tiltVeinsDown(BlockPos blockPos, World world) { //Tilt Veins Downwards
        if (!SculkTags.SCULK_UNBENDABLE.contains((world.getBlockState(blockPos.down())).getBlock())) {
            if (world.getBlockState(blockPos.add(1, -1, 0)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                world.setBlockState(blockPos.add(1, -1, 0), world.getBlockState(blockPos.add(1, -1, 0)).with(Properties.WEST, true));
            }
            if (world.getBlockState(blockPos.add(-1, -1, 0)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                world.setBlockState(blockPos.add(-1, -1, 0), world.getBlockState(blockPos.add(-1, -1, 0)).with(Properties.EAST, true));
            }
            if (world.getBlockState(blockPos.add(0, -1, -1)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                world.setBlockState(blockPos.add(0, -1, -1), world.getBlockState(blockPos.add(0, -1, -1)).with(Properties.SOUTH, true));
            }
            if (world.getBlockState(blockPos.add(0, -1, 1)).getBlock() == SculkVeinBlock.SCULK_VEIN) {
                world.setBlockState(blockPos.add(0, -1, 1), world.getBlockState(blockPos.add(0, -1, 1)).with(Properties.NORTH, true));}
        }
    }

    /** CAlCULATIONS & CHECKS */
    public static int firstRadius(World world, int i) {
        return MathHelper.clamp(i*world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER),1, 33);
    }
    public static void setCatalysts(World world, BlockPos pos, int i) {
        for (BlockPos blockPos : Sphere.checkSpherePos(SculkCatalystBlock.SCULK_CATALYST_BLOCK.getDefaultState(), world, pos, 8, false)) {
            BlockEntity catalyst = world.getBlockEntity(blockPos);
            if (catalyst instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
                if (sculkCatalystBlockEntity.lastSculkRange<i) {
                    sculkCatalystBlockEntity.lastSculkRange=i;
                }
            }
        }
    }
    public static int getHighestRadius(World world, BlockPos pos) {
        int current = 3;
        for (BlockPos blockPos : Sphere.checkSpherePos(SculkCatalystBlock.SCULK_CATALYST_BLOCK.getDefaultState(), world, pos, 8, false)) {
            BlockEntity catalyst = world.getBlockEntity(blockPos);
            if (catalyst instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
                current=Math.max(current, sculkCatalystBlockEntity.lastSculkRange);
            }
        }
        return current;
    }
    public static BlockPos sculkCheck(BlockPos blockPos, World world) { //Call For Up&Down Checks
        if (checkPt1(blockPos, world)!=null) {
            return checkPt1(blockPos, world);
        } else if (checkPt2(blockPos, world)!=null) {
            return checkPt2(blockPos, world);
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

    public static boolean airveins(World world, BlockPos blockPos) { //Check If Veins Are Above Invalid Block
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();
        if (SculkTags.SCULK.contains(block)) {
            return false;
        } else if (state.isAir()) {
            return false;
        } else if (FluidTags.WATER.contains(world.getFluidState(blockPos).getFluid())) {
            return false;
        } else if (FluidTags.LAVA.contains(world.getFluidState(blockPos).getFluid())) {
            return false;
        } else if (SculkTags.SCULK_REPLACEABLE.contains(block)) {
            return false;
        } else return !SculkTags.SCULK_UNTOUCHABLE.contains(block);
    }

    public static boolean solid(World world, BlockPos blockPos) {
        return (blockPos!=null && !world.getBlockState(blockPos).isAir() && !SculkTags.SCULK_UNTOUCHABLE.contains(world.getBlockState(blockPos).getBlock()));
    }
    public static boolean solidrep(World world, BlockPos blockPos) {
        Block block = world.getBlockState(blockPos).getBlock();
        return (!world.getBlockState(blockPos).isAir() && !SculkTags.SCULK_UNTOUCHABLE.contains(block) && SculkTags.SCULK_REPLACEABLE.contains(block) && !SculkTags.SCULK.contains(world.getBlockState(blockPos.down()).getBlock()));
    }
    public static boolean solrepsculk(World world, BlockPos blockPos) {
        Block block = world.getBlockState(blockPos).getBlock();
        return (!SculkTags.SCULK_REPLACEABLE.contains(block) && airOrReplaceableUp(world, blockPos) && !SculkTags.SCULK.contains(block));
    }
    public static boolean airOrReplaceableUp(World world, BlockPos blockPos) {
        for (Direction direction : Direction.Type.VERTICAL) {
            BlockState state = world.getBlockState(blockPos.offset(direction));
            if (state.hasSidedTransparency() || SculkTags.SCULK_REPLACEABLE.contains(state.getBlock()) || state.isAir()) {
                return true;
            }
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockState state = world.getBlockState(blockPos.offset(direction));
            if (state.hasSidedTransparency() || SculkTags.SCULK_REPLACEABLE.contains(state.getBlock()) || state.isAir()) {
                return true;
            }
        }
        return false;
    }
}
