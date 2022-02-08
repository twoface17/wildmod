package frozenblock.wild.mod.fromAccurateSculk;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.blocks.SculkBlock;
import frozenblock.wild.mod.blocks.SculkCatalystBlock;
import frozenblock.wild.mod.blocks.SculkShriekerBlock;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.liukrastapi.Sphere;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.ServerTask;
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

import java.util.Objects;

import static java.lang.Math.*;

public class CatalystThreader {
    public static int running;

    public static void main(World world, Entity entity, BlockPos blockPos, int div) throws InterruptedException {
        if (div > 0) {
            world.playSound(null, blockPos, RegisterSounds.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 1F, 1F);
            SculkThread T1 = new SculkThread("sculkThread");
            T1.blockPos = blockPos;
            T1.world = world;
            T1.setPriority(Thread.MAX_PRIORITY);
            T1.l = div;
            T1.entity=entity;
            T1.start();
            T1.join();

            ActivatorThread T2 = new ActivatorThread("activatorThread");
            T2.blockPos = blockPos;
            T2.world = world;
            T2.setPriority(Thread.MIN_PRIORITY);
            T2.l = (int) ((48)*Math.sin((getHighestRadius(world, blockPos)/40.75)));
            T2.r = getHighestRadius(world, blockPos);
            T2.start();
            }
        }

    public static int getHighestRadius(World world, BlockPos pos) {
        int current = 0;
        for (BlockPos blockPos : Sphere.blockPosSphere(pos, 9, SculkCatalystBlock.SCULK_CATALYST_BLOCK, world)) {
            BlockEntity catalyst = world.getBlockEntity(blockPos);
            if (catalyst instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
                current=Math.max(current, sculkCatalystBlockEntity.lastSculkRange);
            }
        }
        return current;
    }
    }

class ActivatorThread extends Thread {
    private Thread t;
    public String threadName;
    public World world;
    public BlockPos blockPos;
    public int l;
    public int r;
    ActivatorThread(String name) {
        this.threadName=name;
    }

    @Override
    public void run() {
        try {
            placeActiveOmptim(l,r,blockPos,world);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start () {
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

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
            placeActivator(pos.add(x, 0, y), world, loop/3);
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
                        callPlace(world, NewSculk.up(), sensor.with(waterLogged, true));
                    } else if (stateUp.getBlock() !=waterBlock) {
                        if (stateUp == vein.with(waterLogged, true)) {
                            callPlace(world, NewSculk.up(), sensor.with(waterLogged, true));
                        } else {
                            callPlace(world, NewSculk.up(), sensor);
                        }
                    }
                } else {
                    if (stateUp == water) {
                        callPlace(world, NewSculk.up(), shrieker.with(waterLogged, true));
                    } else if (stateUp.getBlock() != waterBlock) {
                        if (stateUp == vein.with(waterLogged, true)) {
                            callPlace(world, NewSculk.up(), shrieker.with(waterLogged, true));
                        } else {
                            world.removeBlock(NewSculk.up(), true);
                            callPlace(world, NewSculk.up(), shrieker);
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
    public BlockPos solidsculkCheck(BlockPos blockPos, World world) { //Call For Up&Down Checks
        BlockPos check = checkPt2(blockPos, world);
        if (check!=null) { return check; }
        return checkPt1(blockPos, world);
    }
    public BlockPos checkPt1(BlockPos blockPos, World world) { //Check For Valid Placement Above
        int upward = world.getGameRules().getInt(WildMod.UPWARD_SPREAD);
        int MAX = world.getHeight();
        if (blockPos.getY() + upward >= MAX) {
            upward = (MAX - blockPos.getY())-1;
        }
        for (int h = 0; h < upward; h++) {
            BlockPos pos =  blockPos.up(h);
            if (solrepsculk(world, pos)) {
                return pos;
            }
        }
        return null;
    }
    public BlockPos checkPt2(BlockPos blockPos, World world) { //Check For Valid Placement Below
        int downward = world.getGameRules().getInt(WildMod.DOWNWARD_SPREAD);
        int MIN = world.getBottomY();
        if (blockPos.getY() - downward <= MIN) {
            downward = (blockPos.getY()-MIN)-1;
        }
        for (int h = 0; h < downward; h++) {
            BlockPos pos =  blockPos.down(h);
            if (solrepsculk(world, pos)) {
                return pos;
            }
        }
        return null;
    }

    public boolean solrepsculk(World world, BlockPos blockPos) {
        Block block = world.getBlockState(blockPos).getBlock();
        return (block==sculk.getBlock() && SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(blockPos.up()).getBlock()));
    }

    public void callPlace(World world, BlockPos pos, BlockState blockState) {
        Runnable runa2 = () -> world.setBlockState(pos, blockState);
        Objects.requireNonNull(world.getServer()).send(new ServerTask(0, runa2));
    }
}

class SculkThread extends Thread {
    private Thread t;
    public String threadName;
    public World world;
    public BlockPos blockPos;
    public Entity entity;
    public int l;

    SculkThread( String name) {
        threadName = name;
    }
    @Override
    public void run() {
        try {
            CatalystThreader.running++;
            sculk(blockPos, world, entity);
            CatalystThreader.running--;
        } catch (Exception e) {
            e.printStackTrace();
            CatalystThreader.running--;
        }
    }

    public void start () {
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }
    /** DEFAULT VARIABLES */
    public static final BlockState vein = SculkVeinBlock.SCULK_VEIN.getDefaultState();
    public static final BlockState brokenVein = SculkVeinBlock.SCULK_VEIN.getDefaultState().with(Properties.DOWN, false);
    public static final BlockState water = Blocks.WATER.getDefaultState();
    public static final BlockState air = Blocks.AIR.getDefaultState();
    public static final Block waterBlock = Blocks.WATER;
    public static final Block veinBlock = SculkVeinBlock.SCULK_VEIN;
    public static final BooleanProperty waterLogged = Properties.WATERLOGGED;
    public static final BlockState brokenWaterVein = SculkVeinBlock.SCULK_VEIN.getDefaultState().with(Properties.DOWN, false).with(waterLogged, true);


    /** MAIN CODE */
    public void sculk(BlockPos blockPos, World world, @Nullable Entity entity) { //Choose Amount Of Sculk + Initial Radius
        if (entity!=null) {
            world.playSound(null, blockPos, RegisterSounds.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 1F, 1F);
            BlockPos down = blockPos.down();
            if (SculkTags.THREE.contains(entity.getType())) {
                sculkOptim(3*l, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            } else if (SculkTags.FIVE.contains(entity.getType())) {
                sculkOptim(5*l, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            } else if (SculkTags.TEN.contains(entity.getType())) {
                sculkOptim(10*l, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            } else if (SculkTags.TWENTY.contains(entity.getType())) {
                sculkOptim(20*l, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            } else if (SculkTags.FIFTY.contains(entity.getType())) {
                sculkOptim(50*l, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            } else if (SculkTags.ONEHUNDRED.contains(entity.getType())) {
                sculkOptim(500*l, firstRadius(world, getHighestRadius(world, blockPos)), down, world);
            }
        }
    }
    public static void sculkOptim(float loop, int rVal, BlockPos down, World world) { //Call For Sculk Placement & Increase Radius If Stuck
        int timesFailed=0;
        int groupsFailed=1;
        float fLoop = loop * world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER);

        for (int l = 0; l < fLoop;) {
            double a = random() * 2 * PI;
            double r = sqrt(rVal +(groupsFailed-1)) * sqrt(random());
            boolean succeed = placeSculk(down.add((int) (r * sin(a)), 0, (int) (r * cos(a))), world);
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
        setCatalystRVal(world, down, rVal +(groupsFailed-1));
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
                Block block = world.getBlockState(NewSculk).getBlock();
                if (SculkTags.BLOCK_REPLACEABLE.contains(block)) {
                    placeSculkOptim(NewSculk, world);
                    return true;
                } else if (airveins(world, NewSculk)) {
                    if (SculkTags.SCULK_REPLACEABLE.contains(world.getBlockState(NewSculk.up()).getBlock()) && !SculkTags.SCULK.contains(world.getBlockState(NewSculk.up()).getBlock())) {
                        veins(NewSculk, world);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void placeSculkOptim(BlockPos NewSculk, World world) { //Place Sculk & Call For Veins
        callPlace(world, SculkBlock.SCULK_BLOCK.getDefaultState(), NewSculk);
        for (Direction direction : Direction.values()) {
            BlockPos pos = NewSculk.offset(direction);
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block==veinBlock) {
                if (state.get(waterLogged)) { //If Vein Is Waterlogged
                    if (state.with(getOpposite(direction), false)==brokenWaterVein) {
                        callPlace(world, water, pos);
                    } else {
                        callPlace(world, state.with(getOpposite(direction), false), pos);
                    }
                } else { // If Vein Isn't Waterlogged
                    if (state.with(getOpposite(direction), false)==brokenVein) {
                        callPlace(world, air, pos);
                    } else {
                        callPlace(world, state.with(getOpposite(direction), false), pos);
                    }
                }
            }
            if (direction==Direction.UP) {
                if (SculkTags.SCULK_REPLACEABLE.contains(block) && block!=waterBlock && !state.isAir()) {
                    if (SculkTags.ALWAYS_WATER.contains(block) || (state.contains(waterLogged) && state.get(waterLogged))) {
                        callPlace(world, water, pos);
                    } else {
                        callPlace(world, air, pos);
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
                    callPlace(world, vein.with(waterLogged, true).with(getOpposite(direction), true), pos);
                } else if (block != waterBlock) {
                    if (block == veinBlock) {
                        callPlace(world, state.with(getOpposite(direction), true), pos);
                    } else if (SculkTags.SCULK_REPLACEABLE.contains(block) || state.isAir()) {
                        callPlace(world, vein.with(getOpposite(direction), true), pos);
                    }
                }
            }
        }

    /** CALCULATIONS & CHECKS */
    public static BooleanProperty getOpposite(Direction direction) {
        if (direction==Direction.UP) { return Properties.DOWN; }
        if (direction==Direction.DOWN) { return Properties.UP; }
        if (direction==Direction.NORTH) { return Properties.SOUTH; }
        if (direction==Direction.SOUTH) { return Properties.NORTH; }
        if (direction==Direction.EAST) { return Properties.WEST; }
        if (direction==Direction.WEST) { return Properties.EAST; }
        return Properties.DOWN;
    }
    public static int firstRadius(World world, int i) {
        return MathHelper.clamp(i*world.getGameRules().getInt(WildMod.SCULK_MULTIPLIER),1, 33);
    }
    public static int getHighestRadius(World world, BlockPos pos) {
        int first = 3;
        int current = 0;
        for (BlockPos blockPos : Sphere.blockPosSphere(pos, 9, SculkCatalystBlock.SCULK_CATALYST_BLOCK, world)) {
            if (world.getBlockEntity(blockPos) instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
                current= (int) Math.max(first, (sculkCatalystBlockEntity.lastSculkRange)/(2*Math.cos((sculkCatalystBlockEntity.lastSculkRange*Math.PI)/175)));
                first=current;
            }
        }
        return current;
    }
    public static BlockPos sculkCheck(BlockPos blockPos, World world) { //Call For Up&Down Checks
        BlockPos check = checkPt2(blockPos, world);
        if (check!=null) { return check; }
        return checkPt1(blockPos, world);
    }
    public static BlockPos checkPt1(BlockPos blockPos, World world) { //Check For Valid Placement Above
        int upward = world.getGameRules().getInt(WildMod.UPWARD_SPREAD);
        int MAX = world.getHeight();
        if (blockPos.getY() + upward >= MAX) {
            upward = (MAX - blockPos.getY())-1;
        }
        for (int h = 0; h < upward; h++) {
            BlockPos pos =  blockPos.up(h);
            if (solrepsculk(world, pos)) {
                return pos;
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
            BlockPos pos =  blockPos.down(h);
            if (solrepsculk(world, pos)) {
                return pos;
            }
        }
        return null;
    }

    public static boolean airveins(World world, BlockPos blockPos) { //Check If Veins Are Above Invalid Block
        if (blockPos==null) { return false; }
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();
        Fluid fluid = world.getFluidState(blockPos).getFluid();
        return !SculkTags.SCULK.contains(block) && !SculkTags.SCULK_UNTOUCHABLE.contains(block) && !SculkTags.SCULK_REPLACEABLE.contains(block) && !state.isAir() && !FluidTags.WATER.contains(fluid) && !FluidTags.LAVA.contains(fluid);
    }

    public static boolean solrepsculk(World world, BlockPos blockPos) {
        Block block = world.getBlockState(blockPos).getBlock();
        return (!SculkTags.SCULK_REPLACEABLE.contains(block) && airOrReplaceableUp(world, blockPos) && !SculkTags.SCULK.contains(block));
    }

    public static boolean airOrReplaceableUp(World world, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            BlockState state = world.getBlockState(blockPos.offset(direction));
            if (state.hasSidedTransparency() || SculkTags.SCULK_REPLACEABLE.contains(state.getBlock()) || state.isAir()) {
                return true;
            }
        }
        return false;
    }

    /** MULTITHREADING-SPECIFIC */
    public static void setCatalysts(World world, BlockPos pos, int i) {
        for (BlockPos blockPos : Sphere.blockPosSphere(pos, 9, SculkCatalystBlock.SCULK_CATALYST_BLOCK, world)) {
            if (world.getBlockEntity(blockPos) instanceof SculkCatalystBlockEntity sculkCatalystBlockEntity) {
                if (sculkCatalystBlockEntity.lastSculkRange!=i) {
                    sculkCatalystBlockEntity.lastSculkRange=i;
                }
            }
        }
    }

    public static void setCatalystRVal(World world, BlockPos pos, int i) {
        Runnable runa2 = () -> setCatalysts(world, pos, i);
        Objects.requireNonNull(world.getServer()).send(new ServerTask(1, runa2));
    }

    public static void callPlace(World world, BlockState blockState, BlockPos pos) {
        Runnable runa2 = () -> world.setBlockState(pos, blockState);
        Objects.requireNonNull(world.getServer()).send(new ServerTask(0, runa2));
    }
    public static void callVeinPlace(World world, BlockState blockState, BlockPos pos) {
        Runnable runa2 = () -> {
            if (world.getBlockState(pos).getBlock()!=SculkVeinBlock.SCULK_VEIN) { world.setBlockState(pos, blockState); }
        };

        Objects.requireNonNull(world.getServer()).send(new ServerTask(0, runa2));
    }
    public static void callDestroy(World world, BlockPos pos) {
        Runnable runa = () -> world.setBlockState(pos, Blocks.AIR.getDefaultState());

        Objects.requireNonNull(world.getServer()).send(new ServerTask(0, runa));
    }
}