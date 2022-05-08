package net.frozenblock.wildmod.entity.ai.task;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class BiasedLongJumpTask<E extends MobEntity> extends WildLongJumpTask<E> {
    private final TagKey<Block> favoredBlocks;
    private final float biasChance;
    private final List<Target> unfavoredTargets = new ArrayList<>();
    private boolean useBias;

    public BiasedLongJumpTask(
            UniformIntProvider cooldownRange,
            int verticalRange,
            int horizontalRange,
            float maxRange,
            Function<E, SoundEvent> entityToSound,
            TagKey<Block> favoredBlocks,
            float biasChance,
            Predicate<BlockState> jumpToPredicate
    ) {
        super(cooldownRange, verticalRange, horizontalRange, maxRange, entityToSound, jumpToPredicate);
        this.favoredBlocks = favoredBlocks;
        this.biasChance = biasChance;
    }

    protected void run(ServerWorld serverWorld, E mobEntity, long l) {
        super.run(serverWorld, mobEntity, l);
        this.unfavoredTargets.clear();
        this.useBias = mobEntity.getRandom().nextFloat() < this.biasChance;
    }

    protected Optional<Target> getTarget(ServerWorld world) {
        if (!this.useBias) {
            return super.getTarget(world);
        } else {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            while(!this.targets.isEmpty()) {
                Optional<Target> optional = super.getTarget(world);
                if (optional.isPresent()) {
                    Target target = (Target)optional.get();
                    if (world.getBlockState(mutable.set(target.getPos(), Direction.DOWN)).isIn(this.favoredBlocks)) {
                        return optional;
                    }

                    this.unfavoredTargets.add(target);
                }
            }

            return !this.unfavoredTargets.isEmpty() ? Optional.of((Target)this.unfavoredTargets.remove(0)) : Optional.empty();
        }
    }

    protected boolean canJumpTo(ServerWorld world, E entity, BlockPos pos) {
        return super.canJumpTo(world, entity, pos) && this.isFluidStateAndBelowEmpty(world, pos);
    }

    private boolean isFluidStateAndBelowEmpty(ServerWorld world, BlockPos pos) {
        return world.getFluidState(pos).isEmpty() && world.getFluidState(pos.down()).isEmpty();
    }
}
