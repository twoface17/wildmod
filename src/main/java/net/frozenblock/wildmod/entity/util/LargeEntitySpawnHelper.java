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
    public LargeEntitySpawnHelper() {
    }

    public static <T extends MobEntity> Optional<T> trySpawnAt(
            EntityType<T> entityType,
            SpawnReason reason,
            ServerWorld world,
            BlockPos pos,
            int tries,
            int horizontalRange,
            int verticalRange,
            LargeEntitySpawnHelper.class_7502 arg
    ) {
        BlockPos.Mutable mutable = pos.mutableCopy();

        for(int i = 0; i < tries; ++i) {
            int j = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
            int k = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
            mutable.set(pos, j, verticalRange, k);
            if (world.getWorldBorder().contains(mutable) && findSpawnPos(world, verticalRange, mutable, arg)) {
                T mobEntity = (T)entityType.create(world, null, null, null, mutable, reason, false, false);
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

    private static boolean findSpawnPos(ServerWorld world, int verticalRange, BlockPos.Mutable pos, LargeEntitySpawnHelper.class_7502 arg) {
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(pos);
        BlockState blockState = world.getBlockState(mutable);

        for(int i = verticalRange; i >= -verticalRange; --i) {
            pos.move(Direction.DOWN);
            mutable.set(pos, Direction.UP);
            BlockState blockState2 = world.getBlockState(pos);
            if (arg.canSpawnOn(world, pos, blockState2, mutable, blockState)) {
                pos.move(Direction.UP);
                return true;
            }

            blockState = blockState2;
        }

        return false;
    }

    public interface class_7502 {
        LargeEntitySpawnHelper.class_7502 field_39400 = (serverWorld, blockPos, blockState, blockPos2, blockState2) -> (
                blockState2.isAir() || blockState2.getMaterial().isLiquid()
        )
                && blockState.getMaterial().blocksLight();
        LargeEntitySpawnHelper.class_7502 field_39401 = (serverWorld, blockPos, blockState, blockPos2, blockState2) -> blockState2.getCollisionShape(
                        serverWorld, blockPos2
                )
                .isEmpty()
                && Block.isFaceFullSquare(blockState.getCollisionShape(serverWorld, blockPos), Direction.UP);

        boolean canSpawnOn(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2);
    }
}
