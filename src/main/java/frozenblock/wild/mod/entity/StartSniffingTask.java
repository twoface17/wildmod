package frozenblock.wild.mod.entity;

import com.google.common.collect.ImmutableMap;
import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.registry.RegisterEntities;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

public class StartSniffingTask extends Task<WardenEntity> {
    private static final int COOLDOWN = 120;

    public StartSniffingTask() {
        super(ImmutableMap.of(RegisterEntities.SNIFF_COOLDOWN, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleState.VALUE_PRESENT, RegisterEntities.DISTURBANCE_LOCATION, MemoryModuleState.VALUE_ABSENT));
    }

    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        Brain<WardenEntity> brain = wardenEntity.getBrain();
        brain.remember(RegisterEntities.IS_SNIFFING, Unit.INSTANCE);
        brain.remember(RegisterEntities.SNIFF_COOLDOWN, Unit.INSTANCE, 120L);
        brain.forget(MemoryModuleType.WALK_TARGET);
        wardenEntity.setPose(WildMod.SNIFFING);
    }
}
