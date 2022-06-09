package net.frozenblock.wildmod.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.ai.FrogBrain;
import net.frozenblock.wildmod.entity.ai.AxolotlSwimNavigation;
import net.frozenblock.wildmod.liukrastapi.animation.AnimationState;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.frozenblock.wildmod.registry.RegisterTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;


public class FrogEntity extends AnimalEntity {
    public static final Ingredient SLIME_BALL = Ingredient.ofItems(new ItemConvertible[]{Items.SLIME_BALL});
    protected static final ImmutableList<SensorType<? extends Sensor<? super FrogEntity>>> SENSORS = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, WildMod.FROG_ATTACKABLES, WildMod.FROG_TEMPTATIONS, WildMod.IS_IN_WATER
    );
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.MOBS,
            MemoryModuleType.VISIBLE_MOBS,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.LONG_JUMP_COOLING_DOWN,
            MemoryModuleType.LONG_JUMP_MID_JUMP,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.TEMPTING_PLAYER,
            MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
            new MemoryModuleType[]{
                    MemoryModuleType.IS_TEMPTED,
                    MemoryModuleType.HURT_BY,
                    MemoryModuleType.HURT_BY_ENTITY,
                    MemoryModuleType.NEAREST_ATTACKABLE,
                    RegisterMemoryModules.IS_IN_WATER,
                    RegisterMemoryModules.IS_PREGNANT
            }
    );
    //private static final TrackedData<FrogVariant> VARIANT = DataTracker.registerData(FrogEntity.class, WildRegistry.FROG_VARIANT_DATA);
    private static final TrackedData<OptionalInt> TARGET = DataTracker.registerData(FrogEntity.class, WildMod.OPTIONAL_INT);
    private static final int field_37459 = 5;
    public static final String VARIANT_KEY = "variant";
    public final AnimationState longJumpingAnimationState = new AnimationState();
    public final AnimationState croakingAnimationState = new AnimationState();
    public final AnimationState usingTongueAnimationState = new AnimationState();
    public final AnimationState walkingAnimationState = new AnimationState();
    public final AnimationState swimmingAnimationState = new AnimationState();
    public final AnimationState idlingInWaterAnimationState = new AnimationState();

    public FrogEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.lookControl = new FrogEntity.FrogLookControl(this);
        this.setPathfindingPenalty(PathNodeType.WATER, 4.0F);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.stepHeight = 1.0F;
    }

    protected Brain.Profile<FrogEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return FrogBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<FrogEntity> getBrain() {
        return (Brain<FrogEntity>) super.getBrain();
    }

    protected void initDataTracker() {
        super.initDataTracker();
        //this.dataTracker.startTracking(VARIANT, FrogVariant.TEMPERATE);
        this.dataTracker.startTracking(TARGET, OptionalInt.empty());
    }

    public void clearFrogTarget() {
        this.dataTracker.set(TARGET, OptionalInt.empty());
    }

    public Optional<Entity> getFrogTarget() {
        IntStream var10000 = ((OptionalInt)this.dataTracker.get(TARGET)).stream();
        World var10001 = this.world;
        Objects.requireNonNull(var10001);
        return this.dataTracker.get(TARGET).stream().mapToObj(this.world::getEntityById).filter(Objects::nonNull).findFirst();
    }

    public void setFrogTarget(Entity entity) {
        this.dataTracker.set(TARGET, OptionalInt.of(entity.getId()));
    }

    public int getMaxLookYawChange() {
        return 35;
    }

    public int getMaxHeadRotation() {
        return 5;
    }

    //public FrogVariant getVariant() {
        //return (FrogVariant)this.dataTracker.get(VARIANT);
    //}

    //public void setVariant(FrogVariant variant) {
        //this.dataTracker.set(VARIANT, variant);
    //}

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        //nbt.putString("variant", WildRegistry.FROG_VARIANT.getId(this.getVariant()).toString());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        //FrogVariant frogVariant = WildRegistry.FROG_VARIANT.get(Identifier.tryParse(nbt.getString("variant")));
        //if (frogVariant != null) {
            //this.setVariant(frogVariant);
        //}

    }

    public boolean canBreatheInWater() {
        return true;
    }

    private boolean shouldWalk() {
        return this.onGround && this.getVelocity().horizontalLengthSquared() > 1.0E-6 && !this.isInsideWaterOrBubbleColumn();
    }

    private boolean shouldSwim() {
        return this.getVelocity().horizontalLengthSquared() > 1.0E-6 && this.isInsideWaterOrBubbleColumn();
    }

    protected void mobTick() {
        this.world.getProfiler().push("frogBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("frogActivityUpdate");
        FrogBrain.updateActivities(this);
        this.world.getProfiler().pop();
        super.mobTick();
    }

    public void tick() {
        if (this.world.isClient()) {
            if (this.shouldWalk()) {
                this.walkingAnimationState.startIfNotRunning(this.age);
            } else {
                this.walkingAnimationState.stop();
            }

            if (this.shouldSwim()) {
                this.idlingInWaterAnimationState.stop();
                this.swimmingAnimationState.startIfNotRunning(this.age);
            } else if (this.isInsideWaterOrBubbleColumn()) {
                this.swimmingAnimationState.stop();
                this.idlingInWaterAnimationState.startIfNotRunning(this.age);
            } else {
                this.swimmingAnimationState.stop();
                this.idlingInWaterAnimationState.stop();
            }
        }

        super.tick();
    }

    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            EntityPose entityPose = this.getPose();
            if (entityPose == EntityPose.LONG_JUMPING) {
                this.longJumpingAnimationState.start(this.age);
            } else {
                this.longJumpingAnimationState.stop();
            }

            if (entityPose == WildMod.CROAKING) {
                this.croakingAnimationState.start(this.age);
            } else {
                this.croakingAnimationState.stop();
            }

            if (entityPose == WildMod.USING_TONGUE) {
                this.usingTongueAnimationState.start(this.age);
            } else {
                this.usingTongueAnimationState.stop();
            }
        }

        super.onTrackedDataSet(data);
    }

    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        FrogEntity frogEntity = RegisterEntities.FROG.create(world);
        if (frogEntity != null) {
            FrogBrain.coolDownLongJump(frogEntity, world.getRandom());
        }

        return frogEntity;
    }

    public boolean isBaby() {
        return false;
    }

    public void setBaby(boolean baby) {
    }

    public void breed(ServerWorld world, AnimalEntity other) {
        ServerPlayerEntity serverPlayerEntity = this.getLovingPlayer();
        if (serverPlayerEntity == null) {
            serverPlayerEntity = other.getLovingPlayer();
        }

        if (serverPlayerEntity != null) {
            serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
            Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this, other, null);
        }

        this.setBreedingAge(6000);
        other.setBreedingAge(6000);
        this.resetLoveTicks();
        other.resetLoveTicks();
        this.getBrain().remember(RegisterMemoryModules.IS_PREGNANT, Unit.INSTANCE);
        world.sendEntityStatus(this, (byte)18);
        if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }

    }

    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        RegistryEntry<Biome> registryEntry = world.getBiome(this.getBlockPos());
        if (registryEntry.isIn(RegisterTags.SPAWNS_COLD_VARIANT_FROGS)) {
            //this.setVariant(FrogVariant.COLD);
        } else if (registryEntry.isIn(RegisterTags.SPAWNS_WARM_VARIANT_FROGS)) {
            //this.setVariant(FrogVariant.WARM);
        } else {
            //this.setVariant(FrogVariant.TEMPERATE);
        }

        FrogBrain.coolDownLongJump(this, world.getRandom());
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public static DefaultAttributeContainer.Builder createFrogAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0);
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return RegisterSounds.ENTITY_FROG_AMBIENT;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return RegisterSounds.ENTITY_FROG_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return RegisterSounds.ENTITY_FROG_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(RegisterSounds.ENTITY_FROG_STEP, 0.15F, 1.0F);
    }

    public boolean isPushedByFluids() {
        return false;
    }

    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return super.computeFallDamage(fallDistance, damageMultiplier) - 5;
    }

    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(this.getMovementSpeed(), movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
        } else {
            super.travel(movementInput);
        }

    }

    public static boolean isValidFrogFood(LivingEntity entity) {
        if (entity instanceof SlimeEntity) {
            SlimeEntity slimeEntity = (SlimeEntity)entity;
            if (slimeEntity.getSize() != 1) {
                return false;
            }
        }

        return entity.getType().isIn(RegisterTags.FROG_FOOD);
    }

    protected EntityNavigation createNavigation(World world) {
        return new FrogEntity.FrogSwimNavigation(this, world);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return SLIME_BALL.test(stack);
    }

    public static boolean canSpawn(EntityType<? extends AnimalEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        return world.getBlockState(pos.down()).isIn(RegisterTags.FROGS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
    }

    class FrogLookControl extends LookControl {
        FrogLookControl(MobEntity entity) {
            super(entity);
        }

        protected boolean shouldStayHorizontal() {
            return FrogEntity.this.getFrogTarget().isEmpty();
        }
    }

    static class FrogSwimNavigation extends AxolotlSwimNavigation {
        FrogSwimNavigation(FrogEntity frog, World world) {
            super(frog, world);
        }

        protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new FrogEntity.FrogSwimPathNodeMaker(true);
            this.nodeMaker.setCanEnterOpenDoors(true);
            return new PathNodeNavigator(this.nodeMaker, range);
        }
    }

    static class FrogSwimPathNodeMaker extends AmphibiousPathNodeMaker {
        private final BlockPos.Mutable pos = new BlockPos.Mutable();

        public FrogSwimPathNodeMaker(boolean bl) {
            super(bl);
        }

        public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
            this.pos.set(x, y - 1, z);
            BlockState blockState = world.getBlockState(this.pos);
            return blockState.isIn(RegisterTags.FROG_PREFER_JUMP_TO) ? PathNodeType.OPEN : getLandNodeType(world, this.pos.move(Direction.UP));
        }
    }
}
