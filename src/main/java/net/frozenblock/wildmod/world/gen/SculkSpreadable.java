/*package net.frozenblock.wildmod.world.gen;

import net.frozenblock.wildmod.block.SculkVeinBlock;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.frozenblock.wildmod.world.gen.random.WildAbstractRandom;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface SculkSpreadable {
    SculkSpreadable VEIN_ONLY_SPREADER = new SculkSpreadable() {
        @Override
        public boolean spread(WorldAccess world, BlockPos pos, BlockState state, @Nullable Collection<Direction> directions, boolean markForPostProcessing) {
            if (directions == null) {
                return ((SculkVeinBlock)RegisterBlocks.SCULK_VEIN).getSamePositionOnlyGrower().grow(world.getBlockState(pos), world, pos, markForPostProcessing) > 0L;
            } else if (!directions.isEmpty()) {
                return !state.isAir() && !state.getFluidState().isOf(Fluids.WATER) ? false : SculkVeinBlock.place(world, pos, state, directions);
            } else {
                return SculkSpreadable.super.spread(world, pos, state, directions, markForPostProcessing);
            }
        }

        @Override
        public int spread(
                Cursor cursor, WorldAccess world, BlockPos catalystPos, WildAbstractRandom random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock
        ) {
            return cursor.getDecay() > 0 ? cursor.getCharge() : 0;
        }

        @Override
        public int getDecay(int oldDecay) {
            return Math.max(oldDecay - 1, 0);
        }
    };

    default byte getUpdate() {
        return 1;
    }

    default void spreadAtSamePosition(WorldAccess world, BlockState state, BlockPos pos, WildAbstractRandom random) {
    }

    default boolean method_41470(WorldAccess worldAccess, BlockPos blockPos, WildAbstractRandom abstractRandom) {
        return false;
    }

    default boolean spread(WorldAccess world, BlockPos pos, BlockState state, @Nullable Collection<Direction> directions, boolean markForPostProcessing) {
        return ((AbstractLichenBlock)RegisterBlocks.SCULK_VEIN).getGrower().grow(state, world, pos, markForPostProcessing) > 0L;
    }

    default boolean shouldConvertToSpreadable() {
        return true;
    }

    default int getDecay(int oldDecay) {
        return 1;
    }

    int spread(Cursor cursor, WorldAccess world, BlockPos catalystPos, WildAbstractRandom random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock);
}
*/