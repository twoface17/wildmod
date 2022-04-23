package frozenblock.wild.mod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.entity.WardenEntity;
import frozenblock.wild.mod.registry.RegisterEntities;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class EmergeTask<E extends WardenEntity> extends Task<E> {
    public EmergeTask(int duration) {
        super(ImmutableMap.of(RegisterEntities.IS_EMERGING, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED), duration);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        return true;
    }

    protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
        wardenEntity.setPose(WildMod.EMERGING);
        wardenEntity.playSound(RegisterSounds.ENTITY_WARDEN_EMERGE, 5.0F, 1.0F);
    }

    protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        if (wardenEntity.isInPose(WildMod.EMERGING)) {
            wardenEntity.setPose(EntityPose.STANDING);
        }

    }
}
