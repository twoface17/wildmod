package net.frozenblock.wildmod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class FindRoarTargetTask<E extends WardenEntity> extends Task<E> {
    private final Function<E, Optional<? extends LivingEntity>> targetFinder;

    public FindRoarTargetTask(Function<E, Optional<? extends LivingEntity>> targetFinder) {
        super(ImmutableMap.of(RegisterMemoryModules.ROAR_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED));
        this.targetFinder = targetFinder;
    }

    protected boolean shouldRun(ServerWorld serverWorld, E wardenEntity) {
        Optional<? extends LivingEntity> var10000 = this.targetFinder.apply(wardenEntity);
        Objects.requireNonNull(wardenEntity);
        return var10000.filter(wardenEntity::isValidTarget).isPresent();
    }

    protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
        (this.targetFinder.apply(wardenEntity)).ifPresent((target) -> {
            wardenEntity.getBrain().remember(RegisterMemoryModules.ROAR_TARGET, target);
            wardenEntity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        });
    }
}
