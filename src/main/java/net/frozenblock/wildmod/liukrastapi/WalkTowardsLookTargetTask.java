package net.frozenblock.wildmod.liukrastapi;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class WalkTowardsLookTargetTask<E extends LivingEntity> extends Task<E> {
    private final Function<LivingEntity, Optional<LookTarget>> lookTargetFunction;
    private final int completionRange;
    private int searchRange;
    private final float speed;

    public WalkTowardsLookTargetTask(Function<LivingEntity, Optional<LookTarget>> lookTargetFunction, int completionRange, float speed) {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        this.lookTargetFunction = lookTargetFunction;
        this.completionRange = completionRange;
        this.searchRange = searchRange;
        this.speed = speed;
    }

    protected boolean shouldRun(ServerWorld world, E entity) {
        Optional<LookTarget> optional = (Optional)this.lookTargetFunction.apply(entity);
        if (optional.isEmpty()) {
            return false;
        } else {
            LookTarget lookTarget = (LookTarget)optional.get();
            return lookTarget.isSeenBy(entity) && !entity.getPos().isInRange(lookTarget.getPos(), (double)this.searchRange);
        }
    }

    @Nullable
    protected void run(ServerWorld world, E entity, long time) {
        //LookTargetUtil.walkTowards((LivingEntity) entity, (Entity) ((Optional)this.lookTargetFunction.apply(entity)).get(), this.speed, this.completionRange);
    }
}
