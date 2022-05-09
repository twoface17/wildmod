package net.frozenblock.wildmod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.WardenBrain;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;

import java.util.Objects;
import java.util.Optional;

public class RoarTask extends Task<WardenEntity> {
    private static final int SOUND_DELAY = 25;

    public RoarTask() {
        super(
            ImmutableMap.of(
                RegisterMemoryModules.ROAR_TARGET,
                MemoryModuleState.VALUE_PRESENT,
                MemoryModuleType.ATTACK_TARGET,
                MemoryModuleState.VALUE_ABSENT,
                RegisterMemoryModules.ROAR_SOUND_COOLDOWN,
                MemoryModuleState.REGISTERED,
                RegisterMemoryModules.ROAR_SOUND_DELAY,
                MemoryModuleState.REGISTERED
            ),
            WardenBrain.ROAR_DURATION
        );
    }

    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        Brain<WardenEntity> brain = wardenEntity.getBrain();
        brain.remember(RegisterMemoryModules.ROAR_SOUND_DELAY, Unit.INSTANCE, 25L);
        brain.forget(MemoryModuleType.WALK_TARGET);
        LookTargetUtil.lookAt(wardenEntity, (LivingEntity)wardenEntity.getBrain().getOptionalMemory(RegisterMemoryModules.ROAR_TARGET).get());
        wardenEntity.setPose(WildMod.ROARING);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        return true;
    }

    protected void keepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        if (!wardenEntity.getBrain().hasMemoryModule(RegisterMemoryModules.ROAR_SOUND_DELAY)
            && !wardenEntity.getBrain().hasMemoryModule(RegisterMemoryModules.ROAR_SOUND_COOLDOWN)) {
            wardenEntity.getBrain().remember(RegisterMemoryModules.ROAR_SOUND_COOLDOWN, Unit.INSTANCE, WardenBrain.ROAR_DURATION - 25);
            wardenEntity.playSound(RegisterSounds.ENTITY_WARDEN_ROAR, 3.0F, 1.0F);
        }
    }

    protected void finishRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        if (wardenEntity.isInPose(WildMod.ROARING)) {
            wardenEntity.setPose(EntityPose.STANDING);
        }

        wardenEntity.getBrain().getOptionalMemory(RegisterMemoryModules.ROAR_TARGET).ifPresent(wardenEntity::updateAttackTarget);
        wardenEntity.getBrain().forget(RegisterMemoryModules.ROAR_TARGET);
    }
}
