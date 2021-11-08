package frozenblock.wild.mod.blocks;

import com.google.common.collect.ImmutableMap;
import frozenblock.wild.mod.liukrastapi.Sphere;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterParticles;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.block.*;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.block.enums.Tilt;
import net.minecraft.data.client.model.BlockStateVariantMap;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Random;

import static net.minecraft.block.SculkSensorBlock.SCULK_SENSOR_PHASE;
import static net.minecraft.block.SculkSensorBlock.getPhase;

public class SculkShriekerBlock extends Block implements Waterloggable {

    public static final VoxelShape BOTTOM_SHAPE;
    public static final VoxelShape TOP_SHAPE;
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    static {
        WATERLOGGED = Properties.WATERLOGGED;
        TOP_SHAPE = Block.createCuboidShape(1.0D, 8.0D, 1.0D, 15.0D, 16.0D, 15.0D);
        BOTTOM_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(TOP_SHAPE, BOTTOM_SHAPE);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BOTTOM_SHAPE;
    }

    public SculkShriekerBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState) this.getDefaultState().with(WATERLOGGED, false));
        this.setDefaultState((BlockState) this.getDefaultState().with(POWERED, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
        builder.add(POWERED);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if ((Boolean) state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean) state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        world.getBlockTickScheduler().schedule(new BlockPos(x, y, z), this, 1);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (Sphere.checkSphere(Blocks.SCULK_SENSOR.getDefaultState().with(SCULK_SENSOR_PHASE, SculkSensorPhase.COOLDOWN), world, pos, 10))
              if(!world.isClient) {
                  world.playSound(
                          null,
                          pos,
                          RegisterSounds.SHRIEKER_EVENT,
                          SoundCategory.AMBIENT,
                          1f,
                          1f
                  );
                  world.spawnParticles(RegisterParticles.SHRIEKER, x, y, z, 10, 0, 2, 0, 1);
              }
        world.getBlockTickScheduler().schedule(new BlockPos(x, y, z), this, 1);
    }
}
