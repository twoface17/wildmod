package net.frozenblock.wildmod.entity.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public class LargeEntitySpawnHelper {
    public static <T extends MobEntity> Optional<T> trySpawnAt(
            EntityType<T> entityType,
            SpawnReason reason,
            ServerWorld world,
            BlockPos pos,
            int tries,
            int horizontalRange,
            int verticalRange,
            LargeEntitySpawnHelper.Requirements requirements
    ) {
        BlockPos.Mutable mutable = pos.mutableCopy();

        for (int i = 0; i < tries; ++i) {
            int j = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
            int k = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
            mutable.set(pos, j, verticalRange, k);
            if (world.getWorldBorder().contains(mutable) && findSpawnPos(world, verticalRange, mutable, requirements)) {
                T mobEntity = entityType.create(world, null, null, null, mutable, reason, false, false);
                if (mobEntity != null) {
                    if (mobEntity.canSpawn(world, reason) && mobEntity.canSpawn(world)) {
                        world.spawnEntityAndPassengers(mobEntity);
                        return Optional.of(mobEntity);
                    }

                    mobEntity.discard();
                }
            }
        }

        return Optional.empty();
    }

    private static boolean findSpawnPos(ServerWorld world, int verticalRange, BlockPos.Mutable pos, LargeEntitySpawnHelper.Requirements requirements) {
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(pos);
        BlockState blockState = world.getBlockState(mutable);

        for (int i = verticalRange; i >= -verticalRange; --i) {
            pos.move(Direction.DOWN);
            mutable.set(pos, Direction.UP);
            BlockState blockState2 = world.getBlockState(pos);
            if (requirements.canSpawnOn(world, pos, blockState2, mutable, blockState)) {
                pos.move(Direction.UP);
                return true;
            }

            blockState = blockState2;
        }

        return false;
    }

    public interface Requirements {
        LargeEntitySpawnHelper.Requirements IRON_GOLEM = (world, pos, state, abovePos, aboveState) -> (aboveState.isAir() || aboveState.getMaterial().isLiquid())
                && state.getMaterial().blocksLight();
        LargeEntitySpawnHelper.Requirements WARDEN = (world, pos, state, abovePos, aboveState) -> aboveState.getCollisionShape(world, abovePos).isEmpty()
                && Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP);

        boolean canSpawnOn(ServerWorld world, BlockPos pos, BlockState state, BlockPos abovePos, BlockState aboveState);
    }
}
