package frozenblock.wild.mod.entity;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.common.collect.ImmutableMap;
import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.registry.RegisterEntities;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

import java.util.Objects;
import java.util.Optional;

public class SniffTask<E extends WardenEntity> extends Task<E> {
    private static final double HORIZONTAL_RADIUS = 6.0;
    private static final double VERTICAL_RADIUS = 20.0;

    public SniffTask(int i) {
        super(ImmutableMap.of(RegisterEntities.IS_SNIFFING, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleState.REGISTERED), i);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        return true;
    }

    protected void run(ServerWorld serverWorld, E wardenEntity, long l) {
        wardenEntity.playSound(RegisterSounds.ENTITY_WARDEN_SNIFF, 5.0F, 1.0F);
    }

    protected void finishRunning(ServerWorld serverWorld, E wardenEntity, long l) {
        if (wardenEntity.isInPose(WildMod.SNIFFING)) {
            wardenEntity.setPose(EntityPose.STANDING);
        }

        wardenEntity.getBrain().forget(RegisterEntities.IS_SNIFFING);
        Optional<? extends LivingEntity> var10000 = wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        Objects.requireNonNull(wardenEntity);
        var10000.filter(wardenEntity::isValidTarget).ifPresent((livingEntity) -> {
            if (wardenEntity.isInRange(livingEntity, 6.0, 20.0)) {
                wardenEntity.increaseAngerAt(livingEntity);
            }

            WardenBrain.lookAtDisturbance(wardenEntity, livingEntity.getBlockPos());
        });
    }
}
