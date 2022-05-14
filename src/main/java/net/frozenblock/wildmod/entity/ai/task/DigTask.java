package net.frozenblock.wildmod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class DigTask<E extends WardenEntity> extends Task<E> {
    public DigTask(int duration) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), duration);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        return true;
    }

    protected boolean shouldRun(ServerWorld serverWorld, E wardenEntity) {
        return wardenEntity.isOnGround();
    }

    protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
        wardenEntity.setPose(WildMod.DIGGING);
        wardenEntity.playSound(RegisterSounds.ENTITY_WARDEN_DIG, 5.0F, 1.0F);
    }

    protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        wardenEntity.remove(Entity.RemovalReason.DISCARDED);
    }
}
