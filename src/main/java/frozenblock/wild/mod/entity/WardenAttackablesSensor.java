package frozenblock.wild.mod.entity;

import java.util.function.Predicate;

import com.google.common.collect.Iterables;
import net.minecraft.entity.EntityType;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import java.util.Set;
import java.util.stream.Stream;


import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WardenAttackablesSensor extends NearestLivingEntitiesSensor<WardenEntity> {
    public WardenAttackablesSensor() {
    }

    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.copyOf(Iterables.concat(super.getOutputMemoryModules(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    protected void sense(ServerWorld serverWorld, WardenEntity wardenEntity) {
        super.sense(serverWorld, wardenEntity);
        method_43086(wardenEntity, (entity) -> {
            return entity.getType() == EntityType.PLAYER;
        }).or(() -> {
            return method_43086(wardenEntity, (livingEntity) -> {
                return livingEntity.getType() != EntityType.PLAYER;
            });
        }).ifPresentOrElse((entity) -> {
            wardenEntity.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, entity);
        }, () -> {
            wardenEntity.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE);
        });
    }

    private static Optional<LivingEntity> method_43086(WardenEntity wardenEntity, Predicate<LivingEntity> predicate) {
        Stream<LivingEntity> var10000 = wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.MOBS).stream().flatMap(Collection::stream);
        Objects.requireNonNull(wardenEntity);
        return var10000.filter(t -> wardenEntity.isValidTarget(wardenEntity)).filter(predicate).findFirst();
    }

    protected int method_43081() {
        return 24;
    }

    protected int method_43082() {
        return 24;
    }
}
