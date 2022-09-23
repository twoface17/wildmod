package net.frozenblock.wildmod.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.frozenblock.wildmod.entity.AllayEntity;
import net.frozenblock.wildmod.entity.ai.task.GiveInventoryToLookTargetTask;
import net.frozenblock.wildmod.entity.ai.task.WalkTowardsLookTargetTask;
import net.frozenblock.wildmod.misc.NoPenaltyStrollTask;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class AllayBrain {
    private static final float field_38406 = 1.0f;
    private static final float field_38407 = 2.25f;
    private static final float field_38408 = 1.75f;
    private static final float field_39113 = 2.5f;
    private static final int field_38938 = 4;
    private static final int field_38939 = 16;
    private static final int field_38410 = 6;
    private static final int field_38411 = 30;
    private static final int field_38412 = 60;
    private static final int field_38413 = 600;
    private static final int field_38940 = 32;

    public static Brain<?> create(Brain<AllayEntity> brain) {
        AllayBrain.addCoreActivities(brain);
        AllayBrain.addIdleActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<AllayEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8F), new LookAroundTask(45, 90), new WanderAroundTask(), new TemptationCooldownTask(RegisterMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS), new TemptationCooldownTask(RegisterMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS)));
    }

    private static void addIdleActivities(Brain<AllayEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(Pair.of(0, new WalkToNearestVisibleWantedItemTask<>(allay -> true, 1.75f, true, 32)), Pair.of(1, new GiveInventoryToLookTargetTask<>(AllayBrain::getLookTarget, 2.25f)), Pair.of(2, new WalkTowardsLookTargetTask<>(AllayBrain::getLookTarget, 4, 16, 2.25f)), Pair.of(3, new TimeLimitedTask<LivingEntity>(new FollowMobTask(allay -> true, 6.0f), UniformIntProvider.create(30, 60))), Pair.of(4, new RandomTask<>(ImmutableList.of(Pair.of(new NoPenaltyStrollTask(1.0f), 2), Pair.of(new GoTowardsLookTarget(1.0f, 3), 2), Pair.of(new WaitTask(30, 60), 1))))), ImmutableSet.of());
    }

    public static void updateActivities(AllayEntity allay) {
        allay.getBrain().resetPossibleActivities(ImmutableList.of(Activity.IDLE));
    }

    public static void rememberNoteBlock(LivingEntity allay, BlockPos pos) {
        Brain<?> brain = allay.getBrain();
        GlobalPos globalPos = GlobalPos.create(allay.getWorld().getRegistryKey(), pos);
        Optional<GlobalPos> optional = brain.getOptionalMemory(RegisterMemoryModules.LIKED_NOTEBLOCK);
        if (optional.isEmpty()) {
            brain.remember(RegisterMemoryModules.LIKED_NOTEBLOCK, globalPos);
            brain.remember(RegisterMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
        } else if (optional.get().equals(globalPos)) {
            brain.remember(RegisterMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
        }

    }

    private static Optional<LookTarget> getLookTarget(LivingEntity allay) {
        Brain<?> brain = allay.getBrain();
        Optional<GlobalPos> optional = brain.getOptionalMemory(RegisterMemoryModules.LIKED_NOTEBLOCK);
        if (optional.isPresent()) {
            BlockPos blockPos = optional.get().getPos();
            if (shouldGoTowardsNoteBlock(allay, brain, blockPos)) {
                return Optional.of(new BlockPosLookTarget(blockPos.up()));
            }

            brain.forget(RegisterMemoryModules.LIKED_NOTEBLOCK);
        }
        return getLikedLookTarget(allay);
    }

    private static boolean shouldGoTowardsNoteBlock(LivingEntity allay, Brain<?> brain, BlockPos pos) {
        Optional<Integer> optional = brain.getOptionalMemory(RegisterMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS);
        return allay.getWorld().getBlockState(pos).isOf(Blocks.NOTE_BLOCK) && optional.isPresent();
    }

    private static Optional<LookTarget> getLikedLookTarget(LivingEntity allay) {
        return getLikedPlayer(allay).map((player) -> {
            return new EntityLookTarget(player, true);
        });
    }

    public static Optional<ServerPlayerEntity> getLikedPlayer(LivingEntity allay) {
        World world = allay.getWorld();
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            Optional<UUID> optional = allay.getBrain().getOptionalMemory(RegisterMemoryModules.LIKED_PLAYER);
            if (optional.isPresent()) {
                Entity entity = serverWorld.getEntity(optional.get());
                Optional<ServerPlayerEntity> var10000;
                if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                    var10000 = Optional.of(serverPlayerEntity);
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            }
        }

        return Optional.empty();
    }
}
