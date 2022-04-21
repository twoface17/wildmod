package frozenblock.wild.mod.entity;

import com.google.common.collect.ImmutableMap;
import frozenblock.wild.mod.registry.RegisterEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class LookAtDisturbanceTask extends Task<WardenEntity> {
    public LookAtDisturbanceTask() {
        super(ImmutableMap.of(RegisterEntities.DISTURBANCE_LOCATION, MemoryModuleState.REGISTERED, RegisterEntities.ROAR_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT));
    }

    protected boolean shouldRun(ServerWorld serverWorld, WardenEntity wardenEntity) {
        return wardenEntity.getBrain().hasMemoryModule(RegisterEntities.DISTURBANCE_LOCATION) || wardenEntity.getBrain().hasMemoryModule(RegisterEntities.ROAR_TARGET);
    }

    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        BlockPos blockPos = wardenEntity.getBrain().getOptionalMemory(RegisterEntities.ROAR_TARGET).map(Entity::getBlockPos).or(() -> {
            return wardenEntity.getBrain().getOptionalMemory(RegisterEntities.DISTURBANCE_LOCATION);
        }).get();
        wardenEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(blockPos));
    }
}
