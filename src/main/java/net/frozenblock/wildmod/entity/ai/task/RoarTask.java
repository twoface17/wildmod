package net.frozenblock.wildmod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.WardenBrain;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Objects;
import java.util.Optional;

public class RoarTask extends Task<WardenEntity> {
    private static final int SOUND_DELAY = 25;

    public RoarTask() {
        super(ImmutableMap.of(RegisterEntities.ROAR_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, RegisterEntities.ROAR_SOUND_COOLDOWN, MemoryModuleState.REGISTERED, RegisterEntities.ROAR_SOUND_DELAY, MemoryModuleState.REGISTERED), WardenBrain.ROAR_DURATION);
    }

    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        Brain<WardenEntity> brain = wardenEntity.getBrain();
        brain.remember(RegisterEntities.ROAR_SOUND_DELAY, Unit.INSTANCE, 25L);
        brain.forget(MemoryModuleType.WALK_TARGET);
        LookTargetUtil.lookAt(wardenEntity, (LivingEntity)wardenEntity.getBrain().getOptionalMemory(RegisterEntities.ROAR_TARGET).get());
        wardenEntity.setPose(WildMod.ROARING);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        Brain<WardenEntity> brain = wardenEntity.getBrain();
        Optional<LivingEntity> optional = brain.getOptionalMemory(RegisterEntities.ROAR_TARGET);
        if (optional.isPresent()) {
            LivingEntity livingEntity = (LivingEntity)optional.get();
            if (wardenEntity.isValidTarget(livingEntity)) {
                return true;
            }

            brain.forget(RegisterEntities.ROAR_TARGET);
        }

        return false;
    }

    protected void keepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        if (!wardenEntity.getBrain().hasMemoryModule(RegisterEntities.ROAR_SOUND_DELAY) && !wardenEntity.getBrain().hasMemoryModule(RegisterEntities.ROAR_SOUND_COOLDOWN)) {
            wardenEntity.getBrain().remember(RegisterEntities.ROAR_SOUND_COOLDOWN, Unit.INSTANCE, (long)(WardenBrain.ROAR_DURATION - 25));
            wardenEntity.playSound(RegisterSounds.ENTITY_WARDEN_ROAR, 3.0F, 1.0F);
        }
    }

    protected void finishRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        if (wardenEntity.isInPose(WildMod.ROARING)) {
            wardenEntity.setPose(EntityPose.STANDING);
        }

        Optional<? extends LivingEntity> var10000 = wardenEntity.getBrain().getOptionalMemory(RegisterEntities.ROAR_TARGET);
        Objects.requireNonNull(wardenEntity);
        var10000.ifPresent(wardenEntity::updateAttackTarget);
        wardenEntity.getBrain().forget(RegisterEntities.ROAR_TARGET);
    }
}
