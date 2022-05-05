package net.frozenblock.wildmod.liukrastapi;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.AllayBrain;
import net.frozenblock.wildmod.entity.AllayEntity;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class GiveInventoryToLookTargetTask<E extends LivingEntity & InventoryOwner> extends Task<E> {
    private static final int field_38387 = 3;
    private static final int field_38388 = 100;
    private final Function<LivingEntity, Optional<LookTarget>> lookTargetFunction;
    private final float speed;

    public GiveInventoryToLookTargetTask(Function<LivingEntity, Optional<LookTarget>> lookTargetFunction, float speed) {
        super(Map.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, RegisterMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleState.REGISTERED));
        this.lookTargetFunction = lookTargetFunction;
        this.speed = speed;
    }

    protected boolean shouldRun(ServerWorld world, E entity) {
        return this.hasItemAndTarget(entity);
    }

    protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
        return this.hasItemAndTarget(entity);
    }

    protected void run(ServerWorld world, E entity, long time) {
        //this.lookTargetFunction.apply(entity).ifPresent((target) -> LookTargetUtil.walkTowards(entity, (Entity) target, this.speed, 3));
    }

    protected void keepRunning(ServerWorld world, E entity, long time) {
        Optional<LookTarget> optional = (Optional<LookTarget>)this.lookTargetFunction.apply(entity);
        if (!optional.isEmpty()) {
            LookTarget lookTarget = (LookTarget)optional.get();
            double d = lookTarget.getPos().distanceTo(entity.getEyePos());
            if (d < 3.0D) {
                ItemStack itemStack = ((InventoryOwner)entity).getInventory().removeStack(0, 1);
                if (!itemStack.isEmpty()) {
                    LookTargetUtil.give(entity, itemStack, offsetTarget(lookTarget));
                    if (entity instanceof AllayEntity) {
                        AllayEntity allayEntity = (AllayEntity)entity;
                        AllayBrain.getLikedPlayer(allayEntity).ifPresent((player) -> {
                            this.triggerCriterion(lookTarget, itemStack, player);
                        });
                    }

                    entity.getBrain().remember(RegisterMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS, 100);
                }
            }

        }
    }

    private void triggerCriterion(LookTarget target, ItemStack stack, ServerPlayerEntity player) {
        BlockPos blockPos = target.getBlockPos().down();
        WildMod.ALLAY_DROP_ITEM_ON_BLOCK.trigger(player, blockPos, stack);
    }

    private boolean hasItemAndTarget(E entity) {
        return !((InventoryOwner)entity).getInventory().isEmpty() && ((Optional)this.lookTargetFunction.apply(entity)).isPresent();
    }

    private static Vec3d offsetTarget(LookTarget target) {
        return target.getPos().add(0.0D, 1.0D, 0.0D);
    }
}
