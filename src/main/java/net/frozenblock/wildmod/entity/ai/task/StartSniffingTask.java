package net.frozenblock.wildmod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class StartSniffingTask extends Task<WardenEntity> {
    private static final IntProvider COOLDOWN = UniformIntProvider.create(100, 200);

    public StartSniffingTask() {
        super(
                ImmutableMap.of(
                        RegisterMemoryModules.SNIFF_COOLDOWN,
                        MemoryModuleState.VALUE_ABSENT,
                        MemoryModuleType.NEAREST_ATTACKABLE,
                        MemoryModuleState.VALUE_PRESENT,
                        RegisterMemoryModules.DISTURBANCE_LOCATION,
                        MemoryModuleState.VALUE_ABSENT
                )
        );
    }

    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        Brain<WardenEntity> brain = wardenEntity.getBrain();
        brain.remember(RegisterMemoryModules.IS_SNIFFING, Unit.INSTANCE);
        brain.remember(RegisterMemoryModules.SNIFF_COOLDOWN, Unit.INSTANCE, COOLDOWN.get(serverWorld.getRandom()));
        brain.forget(MemoryModuleType.WALK_TARGET);
        wardenEntity.setPose(WildMod.SNIFFING);
    }
}
