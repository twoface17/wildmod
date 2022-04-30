package net.frozenblock.wildmod.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.frozenblock.wildmod.liukrastapi.GiveInventoryToLookTargetTask;
import net.frozenblock.wildmod.liukrastapi.NoPenaltyStrollTask;
import net.frozenblock.wildmod.liukrastapi.WalkTowardsLookTargetTask;
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
    private static final float field_38406 = 1.0F;
    private static final float field_38407 = 1.25F;
    private static final float field_38408 = 2.0F;
    private static final int field_38409 = 16;
    private static final int field_38410 = 6;
    private static final int field_38411 = 30;
    private static final int field_38412 = 60;
    private static final int field_38413 = 600;

    public AllayBrain() {
    }

    protected static Brain<?> create(Brain<AllayEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<AllayEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8F), new LookAroundTask(45, 90), new WanderAroundTask(), new TemptationCooldownTask(RegisterMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS), new TemptationCooldownTask(RegisterMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS)));
    }

    private static void addIdleActivities(Brain<AllayEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(Pair.of(0, new WalkToNearestVisibleWantedItemTask<>((allay) -> {
            return true;
        }, 2.0F, true, 9)), Pair.of(1, new GiveInventoryToLookTargetTask<>(AllayBrain::getLookTarget, 1.25F)), Pair.of(2, new WalkTowardsLookTargetTask<>(AllayBrain::getLookTarget, 16, 1.25F)), Pair.of(3, new TimeLimitedTask<>(new FollowMobTask((allay) -> {
            return true;
        }, 6.0F), UniformIntProvider.create(30, 60))), Pair.of(4, new RandomTask<>(ImmutableList.of(Pair.of(new NoPenaltyStrollTask(1.0F), 2), Pair.of(new GoTowardsLookTarget(1.0F, 3), 2), Pair.of(new WaitTask(30, 60), 1))))), ImmutableSet.of());
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
        } else if (((GlobalPos)optional.get()).equals(globalPos)) {
            brain.remember(RegisterMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
        }

    }

    private static Optional<LookTarget> getLookTarget(LivingEntity allay) {
        Brain<?> brain = allay.getBrain();
        Optional<GlobalPos> optional = brain.getOptionalMemory(RegisterMemoryModules.LIKED_NOTEBLOCK);
        if (optional.isPresent()) {
            BlockPos blockPos = ((GlobalPos)optional.get()).getPos();
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
        if (!world.isClient() && world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            Optional<UUID> optional = allay.getBrain().getOptionalMemory(RegisterMemoryModules.LIKED_PLAYER);
            if (optional.isPresent()) {
                Entity entity = serverWorld.getEntity((UUID)optional.get());
                Optional var10000;
                if (entity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
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
