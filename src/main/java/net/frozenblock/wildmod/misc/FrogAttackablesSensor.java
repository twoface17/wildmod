package net.frozenblock.wildmod.misc;

import net.frozenblock.wildmod.entity.FrogEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;

public class FrogAttackablesSensor
        extends NearestVisibleLivingEntitySensor {
    public static final float RANGE = 10.0f;

    @Override
    protected boolean matches(LivingEntity entity, LivingEntity target) {
        if (!entity.getBrain().hasMemoryModule(MemoryModuleType.HAS_HUNTING_COOLDOWN) && Sensor.testAttackableTargetPredicate(entity, target) && FrogEntity.isValidFrogFood(target)) {
            return target.isInRange(entity, 10.0);
        }
        return false;
    }

    @Override
    protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}
