package net.frozenblock.wildmod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.entity.WildDamageSource;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.frozenblock.wildmod.registry.RegisterParticles;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SonicBoomTask extends Task<WardenEntity> {
    private static final int SOUND_DELAY = MathHelper.ceil(34.0) + 5;
    private static final int RUN_TIME = MathHelper.ceil(60.0F);

    public SonicBoomTask() {
        super(
                ImmutableMap.of(
                        MemoryModuleType.ATTACK_TARGET,
                        MemoryModuleState.VALUE_PRESENT,
                        RegisterMemoryModules.SONIC_BOOM_COOLDOWN,
                        MemoryModuleState.VALUE_ABSENT,
                        RegisterMemoryModules.SONIC_BOOM_SOUND_COOLDOWN,
                        MemoryModuleState.REGISTERED,
                        RegisterMemoryModules.SONIC_BOOM_SOUND_DELAY,
                        MemoryModuleState.REGISTERED
                ),
                RUN_TIME
        );
    }

    protected boolean shouldRun(ServerWorld serverWorld, WardenEntity wardenEntity) {
        return wardenEntity.isInRange(wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get(), 15.0, 20.0);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        return true;
    }

    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long time) {
        wardenEntity.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, RUN_TIME);
        wardenEntity.getBrain().remember(RegisterMemoryModules.SONIC_BOOM_SOUND_DELAY, Unit.INSTANCE, SOUND_DELAY);
        serverWorld.sendEntityStatus(wardenEntity, (byte) 62);
        wardenEntity.playSound(RegisterSounds.ENTITY_WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
    }

    protected void keepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long time) {
        wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(target -> wardenEntity.getLookControl().lookAt(target.getPos()));
        if (!wardenEntity.getBrain().hasMemoryModule(RegisterMemoryModules.SONIC_BOOM_SOUND_DELAY)
                && !wardenEntity.getBrain().hasMemoryModule(RegisterMemoryModules.SONIC_BOOM_SOUND_COOLDOWN)) {
            wardenEntity.getBrain().remember(RegisterMemoryModules.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE, (long) (RUN_TIME - SOUND_DELAY));
            wardenEntity.getBrain()
                    .getOptionalMemory(MemoryModuleType.ATTACK_TARGET)
                    .filter(wardenEntity::isValidTarget)
                    .filter(target -> wardenEntity.isInRange(target, 15.0, 20.0))
                    .ifPresent(target -> {
                        Vec3d wardenPos = wardenEntity.getPos().add(0.0, 1.6F, 0.0);
                        Vec3d targetPos = target.getEyePos().subtract(wardenPos);
                        Vec3d distance = targetPos.normalize();

                        for (int i = 1; i < MathHelper.floor(targetPos.length()) + 7; ++i) {
                            Vec3d vec3d4 = wardenPos.add(distance.multiply(i));
                            serverWorld.spawnParticles(RegisterParticles.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
                        }

                        wardenEntity.playSound(RegisterSounds.ENTITY_WARDEN_SONIC_BOOM, 3.0F, 1.0F);
                        target.damage(WildDamageSource.sonicBoom(wardenEntity), 10.0F);
                        double verticalKnockback = 0.5 * (1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                        double horizontalKnockback = 2.5 * (1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                        target.addVelocity(distance.getX() * horizontalKnockback, distance.getY() * verticalKnockback, distance.getZ() * horizontalKnockback);
                    });
        }
    }

    protected void finishRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long time) {
        cooldown(wardenEntity, 60L);
    }

    public static void cooldown(LivingEntity warden, long cooldown) {
        warden.getBrain().remember(RegisterMemoryModules.SONIC_BOOM_COOLDOWN, Unit.INSTANCE, cooldown);
    }
}
