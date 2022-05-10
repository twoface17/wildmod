package net.frozenblock.wildmod.entity;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.ai.task.SonicBoomTask;
import net.frozenblock.wildmod.entity.ai.task.UpdateAttackTargetTask;
import net.frozenblock.wildmod.event.*;
import net.frozenblock.wildmod.liukrastapi.Angriness;
import net.frozenblock.wildmod.liukrastapi.AnimationState;
import net.frozenblock.wildmod.liukrastapi.WardenAngerManager;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.frozenblock.wildmod.registry.RegisterStatusEffects;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;

public class WardenEntity extends WildHostileEntity implements VibrationListener.Callback {
    private static final Logger field_38138 = LogUtils.getLogger();
    private static final int field_38139 = 16;
    private static final int field_38142 = 40;
    private static final int field_38860 = 200;
    private static final int field_38143 = 500;
    private static final float field_38144 = 0.3F;
    private static final float field_38145 = 1.0F;
    private static final float field_38146 = 1.5F;
    private static final int field_38147 = 30;
    private static final TrackedData<Integer> ANGER = DataTracker.registerData(WardenEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int field_38149 = 200;
    private static final int field_38150 = 260;
    private static final int field_38151 = 20;
    private static final int field_38152 = 120;
    private static final int field_38153 = 20;
    private static final int field_38155 = 35;
    private static final int field_38156 = 10;
    private static final int field_39117 = 20;
    private static final int field_38157 = 100;
    private static final int field_38158 = 20;
    private static final int field_38159 = 30;
    private static final float field_38160 = 4.5F;
    private static final float field_38161 = 0.7F;
    private int tendril_animation_ticks;
    private int previous_tendril_animation_ticks;
    private int heartbeat_animation_ticks;
    private int previous_heartbeat_animation_ticks;
    public AnimationState roaringAnimationState = new AnimationState();
    public AnimationState sniffingAnimationState = new AnimationState();
    public AnimationState emergingAnimationState = new AnimationState();
    public AnimationState diggingAnimationState = new AnimationState();
    public AnimationState attackingAnimationState = new AnimationState();
    public AnimationState chargingSonicBoomAnimationState = new AnimationState();
    private final EntityGameEventHandler<VibrationListener> gameEventHandler;
    private WardenAngerManager angerManager = new WardenAngerManager(this::isValidTarget, Collections.emptyList());

    public WardenEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.gameEventHandler = new EntityGameEventHandler<>(
                new VibrationListener(new EntityPositionSource(this, this.getStandingEyeHeight()), 16, this, null, 0, 0)
        );
        this.experiencePoints = 5;
        this.disablesShield(); // Allows the Warden to disable shields
        this.getNavigation().setCanSwim(true);
        this.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0F);
        this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
    }

    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.isInPose(WildMod.EMERGING) ? 1 : 0);
    }

    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        if (packet.getEntityData() == 1) {
            this.setPose(WildMod.EMERGING);
        }

    }

    public boolean canSpawn(WorldView world) {
        return super.canSpawn(world) && world.isSpaceEmpty(this, this.getType().getDimensions().getBoxAt(this.getPos()));
    }

    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return 0.0F;
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return this.isDiggingOrEmerging() && !damageSource.isOutOfWorld() ? true : super.isInvulnerableTo(damageSource);
    }

    private boolean isDiggingOrEmerging() {
        return this.isInPose(WildMod.DIGGING) || this.isInPose(WildMod.EMERGING);
    }

    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    public boolean disablesShield() {
        return this.getMainHandStack().getItem() instanceof AxeItem;
    }

    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.55F;
    }

    public static DefaultAttributeContainer.Builder addAttributes() {
        return WildHostileEntity.createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0);
    }

    public boolean occludeVibrationSignals() {
        return true;
    }

    protected float getSoundVolume() {
        return 4.0F;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return !this.isInPose(WildMod.ROARING) && !this.isDiggingOrEmerging() ? this.getAngriness().getSound() : null;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return RegisterSounds.ENTITY_WARDEN_HURT;
    }

    protected SoundEvent getDeathSound() {
        return RegisterSounds.ENTITY_WARDEN_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(RegisterSounds.ENTITY_WARDEN_STEP, 10.0F, 1.0F);
    }

    public boolean tryAttack(Entity target) {
        this.world.sendEntityStatus(this, (byte)4);
        this.playSound(RegisterSounds.ENTITY_WARDEN_ATTACK_IMPACT, 10.0F, this.getSoundPitch());
        SonicBoomTask.cooldown(this, 40);
        return super.tryAttack(target);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGER, 0);
    }

    public int getAnger() {
        return this.dataTracker.get(ANGER);
    }

    private void updateAnger() {
        this.dataTracker.set(ANGER, this.angerManager.getPrimeSuspectAnger());
    }

    public void tick() {
        World var2 = this.world;
        if (var2 instanceof ServerWorld serverWorld) {
            this.gameEventHandler.getListener().tick(serverWorld);
            if (this.hasCustomName()) {
                WardenBrain.resetDigCooldown(this);
            }
        }

        super.tick();
        if (this.world.isClient()) {
            if (this.age % this.getHeartRate() == 0) {
                this.heartbeat_animation_ticks = 10;
                if (!this.isSilent()) {
                    this.world
                            .playSound(
                                    this.getX(),
                                    this.getY(),
                                    this.getZ(),
                                    RegisterSounds.ENTITY_WARDEN_HEARTBEAT,
                                    this.getSoundCategory(),
                                    5.0F,
                                    this.getSoundPitch(),
                                    false
                            );
                }
            }

            this.previous_tendril_animation_ticks = this.tendril_animation_ticks;
            if (this.tendril_animation_ticks > 0) {
                --this.tendril_animation_ticks;
            }

            this.previous_heartbeat_animation_ticks = this.heartbeat_animation_ticks;
            if (this.heartbeat_animation_ticks > 0) {
                --this.heartbeat_animation_ticks;
            }

            if (this.isInPose(WildMod.EMERGING)) {
                this.addDigParticles(this.emergingAnimationState);
            }
            if (this.isInPose(WildMod.DIGGING)) {
                this.addDigParticles(this.diggingAnimationState);
            }
        }

    }

    protected void mobTick() {
        ServerWorld serverWorld = (ServerWorld)this.world;
        serverWorld.getProfiler().push("wardenBrain");
        this.getBrain().tick(serverWorld, this);
        this.world.getProfiler().pop();
        super.mobTick();
        if ((this.age + this.getId()) % 120 == 0) {
            addDarknessToClosePlayers(serverWorld, this.getPos(), this, 20);
        }

        if (this.age % 20 == 0) {
            this.angerManager.tick(serverWorld, this::isValidTarget);
        }

        this.updateAnger();
        WardenBrain.updateActivities(this);
    }

    public void handleStatus(byte status) {
        if (status == 4) {
            this.attackingAnimationState.start();
        } else if (status == 61) {
            this.tendril_animation_ticks = 10;
        } else if (status == 62) {
            this.chargingSonicBoomAnimationState.start();
        } else {
            super.handleStatus(status);
        }

    }

    private int getHeartRate() {
        float f = (float)this.getAnger() / (float)Angriness.ANGRY.getThreshold();
        return 40 - MathHelper.floor(MathHelper.clamp(f, 0.0F, 1.0F) * 30.0F);
    }

    public float getTendrilPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, (float)this.previous_tendril_animation_ticks, (float)this.tendril_animation_ticks) / 10.0F;
    }

    public float getHeartPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, (float)this.previous_heartbeat_animation_ticks, (float)this.heartbeat_animation_ticks) / 10.0F;
    }

    private void addDigParticles(AnimationState animationState) {
        if ((float)animationState.method_43687() < 4500.0F) {
            Random random = this.getRandom();
            BlockState blockState = this.world.getBlockState(this.getBlockPos().down());
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                for(int i = 0; i < 30; ++i) {
                    double d = this.getX() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
                    double e = this.getY();
                    double f = this.getZ() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
                    this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0, 0.0, 0.0);
                }
            }
        }

    }

    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            if(this.getPose()==WildMod.ROARING) {
                this.roaringAnimationState.start();
            } if (this.getPose()==WildMod.SNIFFING) {
                this.sniffingAnimationState.start();
            } if (this.getPose()==WildMod.EMERGING) {
                this.emergingAnimationState.start();
            } if (this.getPose()==WildMod.DIGGING) {
                this.diggingAnimationState.start();
            }
        }
        super.onTrackedDataSet(data);
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return WardenBrain.create(this, dynamic);
    }

    public Brain<WardenEntity> getBrain() {
        return (Brain<WardenEntity>) super.getBrain();
    }

    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
        World var3 = this.world;
        if (var3 instanceof ServerWorld serverWorld) {
            callback.accept(this.gameEventHandler, serverWorld);
        }

    }

    public TagKey<net.minecraft.world.event.GameEvent> getTag() {
        return WildEventTags.WARDEN_CAN_LISTEN;
    }

    public boolean canAvoidVibrations() {
        return true;
    }

    public boolean isValidTarget(@Nullable Entity entity) {
        if (entity instanceof LivingEntity livingEntity
                && this.world == entity.world
                && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity)
                && !this.isTeammate(entity)
                && livingEntity.getType() != EntityType.ARMOR_STAND
                && livingEntity.getType() != RegisterEntities.WARDEN
                && !livingEntity.isInvulnerable()
                && !livingEntity.isDead()
                && this.world.getWorldBorder().contains(livingEntity.getBoundingBox())) {
            return true;
        }

        return false;
    }

    public static void addDarknessToClosePlayers(ServerWorld world, Vec3d pos, @Nullable Entity entity, int range) {
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(RegisterStatusEffects.DARKNESS, 260, 0, false, false);
        addEffectToPlayersWithinDistance(world, entity, pos, range, statusEffectInstance, 200);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        WardenAngerManager.method_43692(this::isValidTarget)
                .encodeStart(NbtOps.INSTANCE, this.angerManager)
                .resultOrPartial(field_38138::error)
                .ifPresent(angerNbt -> nbt.put("anger", angerNbt));
        VibrationListener.createCodec(this)
                .encodeStart(NbtOps.INSTANCE, (VibrationListener)this.gameEventHandler.getListener())
                .resultOrPartial(field_38138::error)
                .ifPresent(nbtElement -> nbt.put("listener", nbtElement));
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("anger")) {
            WardenAngerManager.method_43692(this::isValidTarget)
                    .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("anger")))
                    .resultOrPartial(field_38138::error)
                    .ifPresent(angerManager -> this.angerManager = angerManager);
            this.updateAnger();
        }

        if (nbt.contains("listener", 10)) {
            VibrationListener.createCodec(this)
                    .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")))
                    .resultOrPartial(field_38138::error)
                    .ifPresent(vibrationListener -> this.gameEventHandler.setListener(vibrationListener, this.world));
        }

    }

    private void playListeningSound() {
        if (!this.isInPose(WildMod.ROARING)) {
            this.playSound(this.getAngriness().getListeningSound(), 10.0F, this.getSoundPitch());
        }

    }

    public Angriness getAngriness() {
        return Angriness.getForAnger(this.angerManager.getPrimeSuspectAnger());
    }

    public void removeSuspect(Entity entity) {
        this.angerManager.removeSuspect(entity);
    }

    public void increaseAngerAt(@Nullable Entity entity) {
        this.increaseAngerAt(entity, 35, true);
    }

    @VisibleForTesting
    public void increaseAngerAt(@Nullable Entity entity, int amount, boolean listening) {
        if (!this.isAiDisabled() && this.isValidTarget(entity)) {
            WardenBrain.resetDigCooldown(this);
            boolean bl = !(this.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null) instanceof PlayerEntity);
            int i = this.angerManager.increaseAngerAt(entity, amount);
            if (entity instanceof PlayerEntity && bl && Angriness.getForAnger(i).method_43691()) {
                this.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
            }

            if (listening) {
                this.playListeningSound();
            }
        }

    }

    public Optional<LivingEntity> getPrimeSuspect() {
        return this.getAngriness().method_43691() ? this.angerManager.getPrimeSuspect() : Optional.empty();
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.isPersistent();
    }

    @Nullable
    public EntityData initialize(
            ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
    ) {
        this.getBrain().remember(RegisterMemoryModules.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
        if (spawnReason == SpawnReason.TRIGGERED) {
            this.setPose(WildMod.EMERGING);
            this.getBrain().remember(RegisterMemoryModules.IS_EMERGING, Unit.INSTANCE, (long)WardenBrain.EMERGE_DURATION);
            this.setPersistent();
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public boolean damage(DamageSource source, float amount) {
        boolean bl = super.damage(source, amount);
        if (this.world.isClient) {
            return false;
        } else {
            if (bl && !this.isAiDisabled()) {
                Entity entity = source.getAttacker();
                this.increaseAngerAt(entity, Angriness.ANGRY.getThreshold() + 20, false);
                if (this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()
                        && entity instanceof LivingEntity livingEntity
                        && (!(source instanceof ProjectileDamageSource) || this.isInRange(livingEntity, 5.0))) {
                    this.updateAttackTarget(livingEntity);
                }
            }

            return bl;
        }
    }

    public void updateAttackTarget(LivingEntity entity) {
        UpdateAttackTargetTask.updateAttackTarget(this, entity);
        SonicBoomTask.cooldown(this, 200);
    }

    public EntityDimensions getDimensions(EntityPose pose) {
        EntityDimensions entityDimensions = super.getDimensions(pose);
        return this.isDiggingOrEmerging() ? EntityDimensions.fixed(entityDimensions.width, 1.0F) : entityDimensions;
    }

    public boolean isPushable() {
        return !this.isDiggingOrEmerging() && super.isPushable();
    }

    protected void pushAway(Entity entity) {
        if (!this.isAiDisabled() && !this.getBrain().hasMemoryModule(RegisterMemoryModules.TOUCH_COOLDOWN)) {
            this.getBrain().remember(RegisterMemoryModules.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
            this.increaseAngerAt(entity);
            WardenBrain.lookAtDisturbance(this, entity.getBlockPos());
        }

        super.pushAway(entity);
    }

    public boolean accepts(ServerWorld world, net.frozenblock.wildmod.event.GameEventListener listener, BlockPos pos, net.frozenblock.wildmod.event.GameEvent event, net.frozenblock.wildmod.event.GameEvent.Emitter emitter) {
        if (!this.isAiDisabled()
                && !this.isDead()
                && !this.getBrain().hasMemoryModule(RegisterMemoryModules.VIBRATION_COOLDOWN)
                && !this.isDiggingOrEmerging()
                && world.getWorldBorder().contains(pos)
                && !this.isRemoved()
                && this.world == world) {
            Entity var7 = emitter.sourceEntity();
            if (var7 instanceof LivingEntity livingEntity && !this.isValidTarget(livingEntity)) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public void accept(
            ServerWorld world, GameEventListener listener, BlockPos pos, net.frozenblock.wildmod.event.GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, int delay
    ) {
        this.brain.remember(RegisterMemoryModules.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
        world.sendEntityStatus(this, (byte)61);
        this.playSound(RegisterSounds.ENTITY_WARDEN_TENDRIL_CLICKS, 5.0F, this.getSoundPitch());
        BlockPos blockPos = pos;
        if (sourceEntity != null) {
            if (this.isInRange(sourceEntity, 30.0)) {
                if (this.getBrain().hasMemoryModule(RegisterMemoryModules.RECENT_PROJECTILE)) {
                    if (this.isValidTarget(sourceEntity)) {
                        blockPos = sourceEntity.getBlockPos();
                    }

                    this.increaseAngerAt(sourceEntity);
                } else {
                    this.increaseAngerAt(sourceEntity, 10, true);
                }
            }

            this.getBrain().remember(RegisterMemoryModules.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
        } else {
            this.increaseAngerAt(entity);
        }

        if (!this.getAngriness().method_43691()) {
            Optional<LivingEntity> optional = this.angerManager.getPrimeSuspect();
            if (sourceEntity != null || optional.isEmpty() || optional.get() == entity) {
                WardenBrain.lookAtDisturbance(this, blockPos);
            }
        }

    }

    @VisibleForTesting
    public WardenAngerManager getAngerManager() {
        return this.angerManager;
    }

    public boolean isInPose(EntityPose pose) {
        return this.getPose() == pose;
    }

    public static List<ServerPlayerEntity> addEffectToPlayersWithinDistance(
            ServerWorld world, @Nullable Entity entity, Vec3d origin, double range, StatusEffectInstance statusEffectInstance, int duration
    ) {
        StatusEffect statusEffect = statusEffectInstance.getEffectType();
        List<ServerPlayerEntity> list = world.getPlayers(
                player -> player.interactionManager.isSurvivalLike()
                        && origin.isInRange(player.getPos(), range)
                        && (
                        !player.hasStatusEffect(statusEffect)
                                || player.getStatusEffect(statusEffect).getAmplifier() < statusEffectInstance.getAmplifier()
                                || player.getStatusEffect(statusEffect).getDuration() < duration
                )
        );
        list.forEach(player -> player.addStatusEffect(new StatusEffectInstance(statusEffectInstance), entity));
        return list;
    }

    public boolean isInRange(Entity entity, double horizontalRadius, double verticalRadius) {
        double d = entity.getX() - this.getX();
        double e = entity.getY() - this.getY();
        double f = entity.getZ() - this.getZ();
        return MathHelper.squaredHypot(d, f) < MathHelper.square(horizontalRadius) && MathHelper.square(e) < MathHelper.square(verticalRadius);
    }
}
