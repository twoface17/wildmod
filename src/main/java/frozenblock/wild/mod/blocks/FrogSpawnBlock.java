package frozenblock.wild.mod.blocks;

import frozenblock.wild.mod.entity.TadpoleEntity;
import frozenblock.wild.mod.registry.RegisterEntities;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class FrogSpawnBlock
        extends PlantBlock {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.5, 16.0);

    public FrogSpawnBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            world.syncWorldEvent(2005, pos, 0);
            world.createAndScheduleBlockTick(pos, world.getBlockState(pos).getBlock(), UniformIntProvider.create(400, 1800).get(world.getRandom()));
        }

    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
            return VoxelShapes.empty();
    }

    @Override
    public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos pos, Random random) {
            int tadpoles = UniformIntProvider.create(1, 6).get(serverWorld.getRandom());
            for (int t = 0; t < tadpoles; t++) {
                TadpoleEntity tadpoleEntity = (TadpoleEntity) RegisterEntities.TADPOLE.create(serverWorld);
                tadpoleEntity.refreshPositionAndAngles((double) pos.getX() + 0.3D + 0.2D, (double) pos.getY(), (double) pos.getZ() + 0.3D, 0.0F, 0.0F);
                serverWorld.spawnEntity(tadpoleEntity);
            }
            serverWorld.breakBlock(pos, false);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        FluidState fluidState2 = world.getFluidState(pos.up());
        return (fluidState.getFluid() == Fluids.WATER && fluidState2.getFluid() == Fluids.EMPTY);
    }

    public static boolean isWater(BlockView world, BlockPos pos) {
        return world.getBlockState(pos)==Blocks.WATER.getDefaultState();
    }

}
