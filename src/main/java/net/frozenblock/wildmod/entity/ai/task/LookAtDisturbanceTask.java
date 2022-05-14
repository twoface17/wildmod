package net.frozenblock.wildmod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class LookAtDisturbanceTask extends Task<WardenEntity> {
    public LookAtDisturbanceTask() {
        super(ImmutableMap.of(RegisterMemoryModules.DISTURBANCE_LOCATION, MemoryModuleState.REGISTERED, RegisterMemoryModules.ROAR_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT));
    }

    protected boolean shouldRun(ServerWorld serverWorld, WardenEntity wardenEntity) {
        return wardenEntity.getBrain().hasMemoryModule(RegisterMemoryModules.DISTURBANCE_LOCATION) || wardenEntity.getBrain().hasMemoryModule(RegisterMemoryModules.ROAR_TARGET);
    }

    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        BlockPos blockPos = (BlockPos)wardenEntity.getBrain().getOptionalMemory(RegisterMemoryModules.ROAR_TARGET).map(Entity::getBlockPos).or(() -> {
            return wardenEntity.getBrain().getOptionalMemory(RegisterMemoryModules.DISTURBANCE_LOCATION);
        }).get();
        wardenEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, (new BlockPosLookTarget(blockPos)));
    }
}
