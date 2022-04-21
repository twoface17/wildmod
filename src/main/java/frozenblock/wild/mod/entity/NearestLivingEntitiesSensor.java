package frozenblock.wild.mod.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NearestLivingEntitiesSensor<T extends LivingEntity> extends Sensor<T> {
    public NearestLivingEntitiesSensor() {

    }

    protected void sense(ServerWorld world, T entity) {
        Box box = entity.getBoundingBox().expand((double)this.method_43081(), (double)this.method_43082(), (double)this.method_43081());
        List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, box, (e) -> {
            return e != entity && e.isAlive();
        });
        Objects.requireNonNull(entity);
        list.sort(Comparator.comparingDouble(entity::squaredDistanceTo));
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.MOBS, list);
        brain.remember(MemoryModuleType.VISIBLE_MOBS, new LivingTargetCache(entity, list));
    }
    protected int method_43081() {
        return 16;
    }

    protected int method_43082() {
        return 16;
    }

    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS);
    }
}
