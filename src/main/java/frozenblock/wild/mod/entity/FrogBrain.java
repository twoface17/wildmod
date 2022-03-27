package frozenblock.wild.mod.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.liukrastapi.*;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterEntities;
import frozenblock.wild.mod.registry.RegisterSounds;
import frozenblock.wild.mod.registry.RegisterTags;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class FrogBrain {
    private static final float field_37469 = 2.0f;
    private static final float field_37470 = 1.0f;
    private static final float field_37471 = 1.0f;
    private static final float field_37472 = 1.0f;
    private static final float field_37473 = 0.75f;
    private static final UniformIntProvider longJumpCooldownRange = UniformIntProvider.create(100, 140);
    private static final int field_37475 = 2;
    private static final int field_37476 = 4;
    private static final float field_37477 = 1.5f;
    private static final float field_37478 = 1.25f;

    protected static void coolDownLongJump(FrogEntity frog) {
        frog.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, longJumpCooldownRange.get(frog.world.random));
    }


    protected static Brain<?> create(Brain<FrogEntity> brain) {
        FrogBrain.addCoreActivities(brain);
        FrogBrain.addIdleActivities(brain);
        FrogBrain.addSwimActivities(brain);
        FrogBrain.addLaySpawnActivities(brain);
        FrogBrain.addTongueActivities(brain);
        FrogBrain.addLongJumpActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new WalkTask(2.0f), new LookAroundTask(45, 90), new WanderAroundTask(), new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new TemptationCooldownTask(MemoryModuleType.LONG_JUMP_COOLING_DOWN)));
    }

    private static void addIdleActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(Pair.of(0, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))), Pair.of(0, new BreedTask(RegisterEntities.FROG, 1.0f)), Pair.of(1, new TemptTask(frog -> 1.25f)), Pair.of(2, new UpdateAttackTargetTask<>(FrogBrain::isNotBreeding, frog -> frog.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE))), Pair.of(3, new WalkTowardsLandTask(6, 1.0f)), Pair.of(4, new RandomTask<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(new StrollTask(1.0f), 1), Pair.of(new GoTowardsLookTarget(1.0f, 3), 1), Pair.of(new CroakTask(), 3), Pair.of(new ConditionalTask<LivingEntity>(Entity::isOnGround, new WaitTask(5, 20)), 2))))), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT), Pair.of(RegisterEntities.IS_IN_WATER, MemoryModuleState.VALUE_ABSENT)));
    }

    private static void addSwimActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(WildMod.SWIM, ImmutableList.of(Pair.of(0, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))), Pair.of(1, new TemptTask(frog -> 1.25f)), Pair.of(2, new UpdateAttackTargetTask<>(FrogBrain::isNotBreeding, frog -> frog.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE))), Pair.of(3, new WalkTowardsLandTask(8, 1.5f)), Pair.of(5, new CompositeTask<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), ImmutableSet.of(), CompositeTask.Order.ORDERED, CompositeTask.RunMode.TRY_ALL, ImmutableList.of(Pair.of(new AquaticStrollTask(0.75f), 1), Pair.of(new StrollTask(1.0f, true), 1), Pair.of(new GoTowardsLookTarget(1.0f, 3), 1), Pair.of(new ConditionalTask<LivingEntity>(Entity::isInsideWaterOrBubbleColumn, new WaitTask(30, 60)), 5))))), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT), Pair.of(RegisterEntities.IS_IN_WATER, MemoryModuleState.VALUE_PRESENT)));
    }

    private static void addLaySpawnActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(WildMod.LAY_SPAWN, ImmutableList.of(Pair.of(0, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))), Pair.of(1, new UpdateAttackTargetTask<>(FrogBrain::isNotBreeding, frog -> frog.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE))), Pair.of(2, new WalkTowardsWaterTask(8, 1.0f)), Pair.of(3, new LayFrogSpawnTask(RegisterBlocks.FROG_SPAWN, RegisterEntities.IS_PREGNANT)), Pair.of(4, new RandomTask<>(ImmutableList.of(Pair.of(new StrollTask(1.0f), 2), Pair.of(new GoTowardsLookTarget(1.0f, 3), 1), Pair.of(new CroakTask(), 2), Pair.of(new ConditionalTask<LivingEntity>(Entity::isOnGround, new WaitTask(5, 20)), 1))))), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT), Pair.of(RegisterEntities.IS_PREGNANT, MemoryModuleState.VALUE_PRESENT)));
    }

    private static void addLongJumpActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(Activity.LONG_JUMP, ImmutableList.of(Pair.of(0, new LeapingChargeTask(longJumpCooldownRange, RegisterSounds.ENTITY_FROG_STEP)), Pair.of(1, new BiasedLongJumpTask<>(longJumpCooldownRange, 2, 4, 1.5f, frog -> RegisterSounds.ENTITY_FROG_LONG_JUMP, RegisterTags.FROG_PREFER_JUMP_TO, 0.5f, state -> state.isOf(Blocks.LILY_PAD)))), ImmutableSet.of(Pair.of(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.LONG_JUMP_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT), Pair.of(RegisterEntities.IS_IN_WATER, MemoryModuleState.VALUE_ABSENT)));
    }

    private static void addTongueActivities(Brain<FrogEntity> brain) {
        brain.setTaskList(WildMod.TONGUE, 0, ImmutableList.of(new ForgetAttackTargetTask<>(), new FrogEatEntityTask(RegisterSounds.ENTITY_FROG_TONGUE, RegisterSounds.ENTITY_FROG_EAT)), MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean isNotBreeding(FrogEntity frog) {
        return !FrogBrain.hasBreedTarget(frog);
    }

    public static boolean hasBreedTarget(LivingEntity entity) {
        return entity.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET);
    }

    public static void updateActivities(FrogEntity frog) {
        frog.getBrain().resetPossibleActivities(ImmutableList.of(WildMod.TONGUE, WildMod.LAY_SPAWN, Activity.LONG_JUMP, WildMod.SWIM, Activity.IDLE));
    }

    public static Ingredient getTemptItems() {
        return FrogEntity.SLIME_BALL;
    }
}
