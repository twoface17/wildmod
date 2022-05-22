package net.frozenblock.wildmod.entity;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.ai.WardenBrain;
import net.frozenblock.wildmod.entity.ai.WardenPositionSource;
import net.frozenblock.wildmod.entity.ai.task.SonicBoomTask;
import net.frozenblock.wildmod.entity.ai.task.UpdateAttackTargetTask;
import net.frozenblock.wildmod.event.WildEventTags;
import net.frozenblock.wildmod.liukrastapi.Angriness;
import net.frozenblock.wildmod.liukrastapi.AnimationState;
import net.frozenblock.wildmod.liukrastapi.WardenAngerManager;
import net.frozenblock.wildmod.liukrastapi.WildServerWorld;
import net.frozenblock.wildmod.particle.WildVibrationParticleEffect;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.frozenblock.wildmod.registry.RegisterStatusEffects;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.PathNode;
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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class WardenEntity extends WildHostileEntity {

    /** WELCOME TO THE WARDEN MUSEUM
     * ALL THESE WILL LINK TO THE FIRST METHOD IN THEIR GIVEN SECTIONS
     * SUSPICION {@link WardenEntity#increaseAngerAt(Entity)}
     * SNIFFING & VIBRATIONS {@link WardenEntity#listen(BlockPos, World, Entity, Entity, int, BlockPos)}
     * ATTACKING {@link WardenEntity#tryAttack(Entity)}
     * NBT, VALUES & BOOLEANS {@link WardenEntity#writeCustomDataToNbt(NbtCompound)}
     * OVERRIDES & NON-WARDEN-SPECIFIC {@link WardenEntity#getHurtSound(DamageSource)}
     * VISUAlS {@link WardenEntity#createVibration(ServerWorld, WardenEntity, BlockPos)}
     * TICKMOVEMENT METHODS {@link WardenEntity#tickVibration()}
     * ALL VALUES ARE STORED AT THE END OF THIS MUSEUM.
     * */

    private static final Logger LOGGER = LogUtils.getLogger();
    /*protected void initGoals() {
        //this.goalSelector.add(1, new WardenSwimGoal(this));
        this.goalSelector.add(3, new WardenGoal(this, speed));
        //this.goalSelector.add(2, new SniffGoal(this, speed));
        //this.goalSelector.add(1, new WardenAttackNearGoal(this));
        //this.goalSelector.add(1, new WardenWanderGoal(this, 0.4));
    }

    */public boolean isInRange(Entity entity, double horizontalRadius, double verticalRadius) {
        double d = entity.getX() - this.getX();
        double e = entity.getY() - this.getY();
        double f = entity.getZ() - this.getZ();
        return MathHelper.squaredHypot(d, f) < MathHelper.square(horizontalRadius) && MathHelper.square(e) < MathHelper.square(verticalRadius);
    }

    public void tickMovement() {
        if (!this.isAiDisabled()) {
            if (this.world.getTime() - this.vibrationTimer <= 100 && !this.isDiggingOrEmerging() && !this.isInPose(WildMod.ROARING) && this.movementPriority<=2) {
                Entity lastEvent = this.getVibrationEntity();
                if (lastEvent!=null) {
                    /*if (this.getAnger()>40 && this.world.getTime() - this.vibrationTimer <= 19) {
                        BlockPos entityPos = lastEvent.getBlockPos();
                        //this.getNavigation().startMovingTo(entityPos.getX(), entityPos.getY(), entityPos.getZ(), (speed + (MathHelper.clamp(this.getAnger(), 0, 50) * 0.01) + (this.trueOverallAnger() * 0.0045)));
                        WardenBrain.lookAtDisturbance(this, entityPos);
                        this.movementPriority = 2;
                    }
                    if (this.getAnger()>80) {
                        BlockPos entityPos = lastEvent.getBlockPos();
                        //this.getNavigation().startMovingTo(entityPos.getX(), entityPos.getY(), entityPos.getZ(), (speed + (MathHelper.clamp(this.getAnger(), 0, 50) * 0.01) + (this.trueOverallAnger() * 0.0045)));
                        WardenBrain.lookAtDisturbance(this, entityPos);
                        this.movementPriority = 2;
                    }
                */} else {this.movementPriority=0;}
            }
            this.tickVibration();
        }
        //Movement
        //if (this.getNavigation().isIdle()) {this.movementPriority=0;}
        //Heartbeat & Anger
        if (this.world.getTime()-this.timeSinceNonEntity>300 && this.nonEntityAnger>0) {
            --this.nonEntityAnger;
        }
        //Client-Side Things
        if (world.isClient) {
            if (world.getLightLevel(LightType.BLOCK, this.getBlockPos())!=this.lastLightLevel) {
                int light = world.getLightLevel(LightType.BLOCK, this.getBlockPos());
                if (light>this.lastLightLevel) {
                    this.isLightHigher=true;
                    this.lightTransitionTicks = (int) (100 - (100*(Math.sin((light*Math.PI))/30)));
                } else {
                    this.lightTransitionTicks = (int) (100 + (100*(Math.cos((light*Math.PI))/30)));
                    this.isLightHigher = false;
                }
                this.lastLightLevel=light;
            }
            if (this.lightTransitionTicks<100 && !this.isLightHigher) {
                ++this.lightTransitionTicks;
            }
            if (this.lightTransitionTicks>0 && this.isLightHigher) { --this.lightTransitionTicks; }
        }
        super.tickMovement();
    }

    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_ATTACK_SOUND) {
            this.roaringAnimationState.stop();
            this.attackingAnimationState.start();
        } else if (status == EARS_TWITCH) {
            this.field_38162 = 10;
        } else if (status == SONIC_BOOM) {
            this.chargingSonicBoomAnimationState.start();
            if (WildMod.debugMode) {
                LOGGER.info("A Warden is charging a Sonic Boom");
            }
        } else if (!this.isAiDisabled() && status == 7) { //Set Last Vibration Time
            this.vibrationTimer=this.world.getTime();
        } else {
            super.handleStatus(status);
        }
    }

    public boolean canListen(ServerWorld world, BlockPos pos, Entity entity) {
        if (!this.isAiDisabled() && !this.isDead() && !this.getBrain().hasMemoryModule(RegisterMemoryModules.VIBRATION_COOLDOWN) && !this.isDiggingOrEmerging() /*&& world.getWorldBorder().contains(pos) */&& !this.isRemoved() && this.world == world && entity != null) {
            boolean var10000;
            if (entity instanceof LivingEntity livingEntity) {
                if (!this.isValidTarget(livingEntity)) {
                    var10000 = false;
                    return var10000;
                }
            }

            var10000 = true;
            return var10000;
        } else {
            return false;
        }
    }

    public void listen(BlockPos pos, World eventWorld, @Nullable Entity eventEntity, @Nullable Entity sourceEntity, int suspicion, BlockPos vibrationPos) { //TODO: RENAME TO "ACCEPT" TO MATCH 1.19 MAPPINGS
        if (eventWorld instanceof ServerWorld serverWorld && canListen(serverWorld, vibrationPos, eventEntity)) {
            this.brain.remember(RegisterMemoryModules.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
            world.sendEntityStatus(this, EARS_TWITCH);
            //this.playSound(RegisterSounds.ENTITY_WARDEN_TENDRIL_CLICKS, 5.0F, this.getSoundPitch());
            BlockPos blockPos = pos;
            if (sourceEntity != null) {
                if (this.isInRange(eventEntity, 30.0)) {
                    if (this.getBrain().hasMemoryModule(RegisterMemoryModules.RECENT_PROJECTILE)) {
                        if (this.isValidTarget(eventEntity)) {
                            blockPos = eventEntity.getBlockPos();
                        }

                        this.increaseAngerAt(sourceEntity);
                    } else {
                        this.increaseAngerAt(sourceEntity, 10, true);
                    }
                }

                this.getBrain().remember(RegisterMemoryModules.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
            } else {
                this.increaseAngerAt(eventEntity);
            }

            if (!this.getAngriness().isAngry()) {
                Optional<LivingEntity> optional = this.angerManager.getPrimeSuspect();
                if (eventEntity != null || optional.isEmpty() || optional.get() == eventEntity) {
                    WardenBrain.lookAtDisturbance(this, blockPos);
                }
            }
            this.vibX = pos.getX();
            this.vibY = pos.getY();
            this.vibZ = pos.getZ();
            this.lasteventworld = eventWorld;
            if (eventEntity!=null) {
                this.vibrationEntity = eventEntity.getUuidAsString();
            } else { this.vibrationEntity = "null"; }
            this.queuedSuspicion=suspicion;
            if (vibrationPos != null) {
                createVibration(serverWorld, this, vibrationPos);
                Vec3d start = Vec3d.ofCenter(vibrationPos);
                Vec3d end = Vec3d.ofCenter(this.getBlockPos());
                this.vibrationTicks = MathHelper.floor(start.distanceTo(end));
            } else {
                createVibration(serverWorld, this, pos);
                Vec3d start = Vec3d.ofCenter(pos);
                Vec3d end = Vec3d.ofCenter(this.getBlockPos());
                this.vibrationTicks = MathHelper.floor(start.distanceTo(end));
            }
        } else if (eventWorld instanceof ServerWorld serverworld && canListen(serverworld, pos, eventEntity)) {
            WardenBrain.resetDigCooldown(this);
            if (vibrationPos != null) { createFloorVibration(serverworld, this, vibrationPos);
                Vec3d start = Vec3d.ofCenter(vibrationPos);
                Vec3d end = Vec3d.ofCenter(this.getBlockPos());
                this.vibrationTicks = MathHelper.floor(start.distanceTo(end));
            }
            else {
                createFloorVibration(serverworld, this, pos);
                Vec3d start = Vec3d.ofCenter(pos);
                Vec3d end = Vec3d.ofCenter(this.getBlockPos());
                this.vibrationTicks = MathHelper.floor(start.distanceTo(end));
            }
        }
    }

    private int getHeartRate() {
        float f = (float)this.getAnger() / (float)Angriness.ANGRY.getThreshold();
        return 40 - MathHelper.floor(MathHelper.clamp(f, 0.0F, 1.0F) * 30.0F);
    }

    public float getTendrilPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.field_38163, this.field_38162) / 10.0F;
    }

    public float getHeartPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.field_38165, this.field_38164) / 10.0F;
    }

    public void tick() {
        World var2 = this.world;
        if (var2 instanceof ServerWorld serverWorld) {
            //this.gameEventHandler.getListener().tick(serverWorld);
            if (this.hasCustomName()) {
                WardenBrain.resetDigCooldown(this);
            }
        }

        super.tick();
        if (this.world.isClient()) {
            if (this.age % this.getHeartRate() == 0) {
                this.field_38164 = 10;
                if (!this.isSilent()) {
                    this.world.playSound(this.getX(), this.getY(), this.getZ(), RegisterSounds.ENTITY_WARDEN_HEARTBEAT, this.getSoundCategory(), 5.0F, this.getSoundPitch(), false);
                }
            }

            this.field_38163 = this.field_38162;
            if (this.field_38162 > 0) {
                --this.field_38162;
            }

            this.field_38165 = this.field_38164;
            if (this.field_38164 > 0) {
                --this.field_38164;
            }

            if (this.getPose()==WildMod.EMERGING) {
                this.addDigParticles(this.emergingAnimationState);
            } else if (this.getPose()==WildMod.DIGGING) {
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
            this.updateAnger();
        }

        WardenBrain.updateActivities(this);
    }

    public int eventSuspicionValue(GameEvent event, LivingEntity livingEntity) {
        int total=1;
        if (event==GameEvent.PROJECTILE_LAND) {
            return 0;
        }
        if (SculkSensorBlock.FREQUENCIES.containsKey(event)) {
            total=total + SculkSensorBlock.FREQUENCIES.getInt(event);
        }
        return MathHelper.clamp(total, 35,35);
    }

    public Entity getVibrationEntity() {
        if (!Objects.equals(this.vibrationEntity, "null")) {
            Box box = new Box(this.getBlockPos().add(-32, -32, -32), this.getBlockPos().add(32, 32, 32));
            List<LivingEntity> entities = this.world.getNonSpectatingEntities(LivingEntity.class, box);
            if (!entities.isEmpty()) {
                for (LivingEntity target : entities) {
                    if (Objects.equals(this.vibrationEntity, target.getUuidAsString()) && !(target instanceof WardenEntity)) {
                        return target;
                    }
                }
            }
        }
        return null;
    }
    /** ATTACKING */
    public boolean tryAttack(Entity target) {
        this.world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
        this.playSound(RegisterSounds.ENTITY_WARDEN_ATTACK_IMPACT, 10.0F, this.getSoundPitch());
        SonicBoomTask.cooldown(this, 40);
        if (WildMod.debugMode) {
            if (target instanceof PlayerEntity player) {
                LOGGER.info("A Warden has attacked " + player.getEntityName());
            } else {
                LOGGER.info("A Warden has attacked " + target.getName().getString());
            }
        }

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
        this.dataTracker.set(ANGER, this.getAngerAtTarget());
    }

    public TagKey<GameEvent> getTag() {
        return WildEventTags.WARDEN_CAN_LISTEN;
    }

    public boolean triggersAvoidCriterion() {
        return true;
    }

    @Contract("null->false")
    public boolean isValidTarget(@Nullable Entity entity) {
        boolean var10000;
        if (entity instanceof LivingEntity livingEntity) {
            if (this.world == entity.world && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity) && !this.isTeammate(entity) && livingEntity.getType() != EntityType.ARMOR_STAND && livingEntity.getType() != RegisterEntities.WARDEN && !livingEntity.isInvulnerable() && !livingEntity.isDead() && this.world.getWorldBorder().contains(livingEntity.getBoundingBox())) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    public static void addDarknessToClosePlayers(ServerWorld world, Vec3d pos, @Nullable Entity entity, int range) {
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(RegisterStatusEffects.DARKNESS, 260, 0, false, false);
        addEffectToPlayersWithinDistance(world, entity, pos, (double)range, statusEffectInstance, 200);
    }

    public static List<ServerPlayerEntity> addEffectToPlayersWithinDistance(ServerWorld world, @Nullable Entity entity, Vec3d origin, double range, StatusEffectInstance statusEffectInstance, int duration) {
        StatusEffect statusEffect = statusEffectInstance.getEffectType();
        List<ServerPlayerEntity> list = world.getPlayers((player) -> {
            return player.interactionManager.isSurvivalLike() && origin.isInRange(player.getPos(), range) && (!player.hasStatusEffect(statusEffect) || player.getStatusEffect(statusEffect).getAmplifier() < statusEffectInstance.getAmplifier() || player.getStatusEffect(statusEffect).getDuration() < duration);
        });
        list.forEach((player) -> {
            player.addStatusEffect(new StatusEffectInstance(statusEffectInstance), entity);
        });
        return list;
    }

    /** NBT, VALUES & BOOLEANS */
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        WardenAngerManager.method_43692(this::isValidTarget)
                .encodeStart(NbtOps.INSTANCE, this.angerManager)
                .resultOrPartial(LOGGER::error)
                .ifPresent(angerNbt -> nbt.put("anger", angerNbt));

        /*VibrationListener.createCodec(this).encodeStart(NbtOps.INSTANCE, this.gameEventHandler.getListener()).resultOrPartial(field_38138::error).ifPresent((nbtElement) -> {
            nbt.put("listener", nbtElement);
        });
        */nbt.putLong("vibrationTimer", this.vibrationTimer);
        nbt.putString("trackingEntity", this.trackingEntity);
        nbt.putInt("nonEntityAnger", this.nonEntityAnger);
        nbt.putLong("timeSinceNonEntity", this.timeSinceNonEntity);
        nbt.putString("vibrationEntity", this.vibrationEntity);
        nbt.putInt("vibX", this.vibX);
        nbt.putInt("vibY", this.vibY);
        nbt.putInt("vibZ", this.vibZ);
        nbt.putInt("queuedSuspicion", this.queuedSuspicion);
        nbt.putInt("movementPriority", this.movementPriority);
    }
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("anger")) {
            WardenAngerManager.method_43692(this::isValidTarget)
                    .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("anger")))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(angerManager -> this.angerManager = angerManager);
            this.updateAnger();
        }

        /*if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
            VibrationListener.createCodec(this).parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener"))).resultOrPartial(field_38138::error).ifPresent((vibrationListener) -> {
                this.gameEventHandler.setListener(vibrationListener, this.world);
            });
        }
        */this.vibrationTimer = nbt.getLong("vibrationTimer");
        this.trackingEntity = nbt.getString("trackingEntity");
        this.nonEntityAnger = nbt.getInt("nonEntityAnger");
        this.timeSinceNonEntity = nbt.getLong("timeSinceNonEntity");
        this.vibrationEntity = nbt.getString("vibrationEntity");
        this.vibX = nbt.getInt("vibX");
        this.vibY = nbt.getInt("vibY");
        this.vibZ = nbt.getInt("vibZ");
        this.queuedSuspicion = nbt.getInt("queuedSuspicion");
        this.movementPriority = nbt.getInt("movementPriority");
    }

    /** OVERRIDES & NON-WARDEN-SPECIFIC */

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(RegisterSounds.ENTITY_WARDEN_STEP, 10.0F, 1.0F);
    }
    protected SoundEvent getHurtSound(DamageSource source) {
        return RegisterSounds.ENTITY_WARDEN_HURT;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return !this.isInPose(WildMod.ROARING) && !this.isDiggingOrEmerging() ? this.getAngriness().getSound() : null;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    protected float getSoundVolume() {
        return 4.0F;
    }

    protected SoundEvent getDeathSound() {
        return RegisterSounds.ENTITY_WARDEN_DEATH;
    }
    @Override
    public void emitGameEvent(GameEvent event, @Nullable Entity entity, BlockPos pos) {}
    @Override
    public void emitGameEvent(GameEvent event, @Nullable Entity entity) {}
    @Override
    public void emitGameEvent(GameEvent event, BlockPos pos) {}
    @Override
    public void emitGameEvent(GameEvent event) {}

    private void playListeningSound() {
        if (!this.isInPose(WildMod.ROARING)) {
            this.playSound(this.getAngriness().getListeningSound(), 10.0F, this.getSoundPitch());
        }

    }

    public Angriness getAngriness() {
        return Angriness.getForAnger(this.getAngerAtTarget());
    }

    private int getAngerAtTarget() {
        return this.angerManager.getAngerFor(this.getTarget());
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
            if (entity instanceof PlayerEntity && bl && Angriness.getForAnger(this.angerManager.increaseAngerAt(entity, amount)).isAngry()) {
                this.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
            }

            if (listening) {
                this.playListeningSound();
            }

            if (WildMod.debugMode) {
                LOGGER.info("A Warden's anger has been increased");
            }
        }

    }

    public Optional<LivingEntity> getPrimeSuspect() {
        return this.getAngriness().isAngry() ? this.angerManager.getPrimeSuspect() : Optional.empty();
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.isPersistent();
    }

    public WardenEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
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

    public boolean isInPose(EntityPose pose) {
        return this.getPose() == pose;
    }

    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    public boolean disablesShield() {
        return true;
    }

    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.55F;
    }

    public static DefaultAttributeContainer.Builder addAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30000001192092896).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0);
    }

    public boolean occludeVibrationSignals() {
        return true;
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.getBrain().remember(RegisterMemoryModules.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
        if (spawnReason == SpawnReason.TRIGGERED) {
            this.setPose(WildMod.EMERGING);
            this.getBrain().remember(RegisterMemoryModules.IS_EMERGING, Unit.INSTANCE, WardenBrain.EMERGE_DURATION);
            this.playSound(RegisterSounds.ENTITY_WARDEN_AGITATED, 5.0F, 1.0F);
        }

        this.setPersistent();
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    /** VISUALS */
        public void createVibration(ServerWorld world, WardenEntity warden, BlockPos blockPos2) {
        WardenPositionSource wardenPositionSource = new WardenPositionSource(this.getId());
        Vec3d start = Vec3d.ofCenter(blockPos2);
        Vec3d end = Vec3d.ofCenter(this.getBlockPos());
        this.distance = (float)start.distanceTo(end);
        this.delay = MathHelper.floor(this.distance);

        //world.sendVibrationPacket(new Vibration(blockPos2, wardenPositionSource, this.delay));
        if (WildMod.debugMode) {
            LOGGER.info("A Warden has created a Vibration");
        }
    }
    public void createFloorVibration(ServerWorld world, WardenEntity warden, BlockPos blockPos2) {
        BlockPositionSource blockSource = new BlockPositionSource(this.getBlockPos().down());
        EntityPositionSource entitySource = new EntityPositionSource(this.getId());
        Vec3d start = Vec3d.ofCenter(blockPos2);
        Vec3d end = Vec3d.ofCenter(this.getBlockPos().down());
        this.distance = (float)start.distanceTo(end);
        this.delay = MathHelper.floor(this.distance);
        //((WildServerWorld)world).spawnParticles(new WildVibrationParticleEffect(entitySource, this.delay), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);

        if (WildMod.debugMode) {
            LOGGER.info("A Warden has created a Floor Vibration");
        }
    }
    public void addDigParticles(AnimationState animationState) {
        if ((float)animationState.getTimeRunning() < 4500.0F) {
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
            if (this.isInPose(WildMod.EMERGING)) {
                this.emergingAnimationState.start();
                if (WildMod.debugMode) {
                    LOGGER.info("A Warden is emerging");
                }
            } else if (this.isInPose(WildMod.DIGGING)) {
                this.diggingAnimationState.start();
                if (WildMod.debugMode) {
                    LOGGER.info("A Warden is digging");
                }
            } else if (this.isInPose(WildMod.ROARING)) {
                this.roaringAnimationState.start();
                if (WildMod.debugMode) {
                    LOGGER.info("A Warden is roaring");
                }
            } else if (this.isInPose(WildMod.SNIFFING)) {
                this.sniffingAnimationState.start();
                if (WildMod.debugMode) {
                    LOGGER.info("A Warden is sniffing");
                }
            }
        }

        super.onTrackedDataSet(data);
    }

    public boolean isImmuneToExplosion() {
            return this.isDiggingOrEmerging();
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

    public boolean damage(DamageSource source, float amount) {
        boolean bl = super.damage(source, amount);
        if (!this.world.isClient && !this.isAiDisabled() && amount > 0.0F) {
            Entity entity = source.getAttacker();
            this.increaseAngerAt(entity, Angriness.ANGRY.getThreshold() + 20, false);
            if (this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                if (!(source instanceof ProjectileDamageSource) || this.isInRange(livingEntity, 5.0)) {
                    this.updateAttackTarget(livingEntity);
                }
            }

            if (WildMod.debugMode) {
                LOGGER.info("A warden has taken damage from an entity");
            }
        }

        return bl;
    }

    public void updateAttackTarget(LivingEntity livingEntity) {
        this.getBrain().forget(RegisterMemoryModules.ROAR_TARGET);
        UpdateAttackTargetTask.updateAttackTarget(this, livingEntity);
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

    @VisibleForTesting
    public WardenAngerManager getAngerManager() {
            return this.angerManager;
    }

    /*protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world) {
            protected PathNodeNavigator createPathNodeNavigator(int range) {
                this.nodeMaker = new LandPathNodeMaker();
                this.nodeMaker.setCanEnterOpenDoors(true);
                return new WildPathNodeNavigator(this.nodeMaker, range) {
                    public float method_44000(WildPathNode pathNode, WildPathNode pathNode2) {
                        return pathNode.method_44022(pathNode2);
                    }
                };
            }
        };
    }*/

    public static class WildPathNode extends PathNode {
        public WildPathNode(int x, int y, int z) {
            super(x, y, z);
        }

        public float method_44022(WildPathNode pathNode) {
            float f = (float)(pathNode.x - this.x);
            float g = (float)(pathNode.z - this.z);
            return MathHelper.sqrt(f * f + g * g);
        }
    }

    @Override
    public boolean isTouchingWater() {
        return this.submergedInWater;
    }

    /** TICKMOVEMENT METHODS */

    public void tickVibration() {
        if (this.vibrationTicks>0) {
            --this.vibrationTicks;
        }
        if (this.vibrationTicks==0) {
            this.listenVibration();
            this.vibrationTicks=-1;
        }
    }
    public void listenVibration() {
        if (this.world instanceof ServerWorld serverWorld) {
            this.world.sendEntityStatus(this, (byte) 7);
            //this.world.sendEntityStatus(this, (byte) 15);
            this.vibrationTimer = this.world.getTime();
            this.playSound(RegisterSounds.ENTITY_WARDEN_TENDRIL_CLICKS, 5.0F, this.getSoundPitch());
            if (this.getVibrationEntity() != null && this.canListen(serverWorld, this.getVibrationEntity().getBlockPos(), this.getVibrationEntity())) {
                Entity eventEntity = this.getVibrationEntity();
                this.lasteventpos = new BlockPos(this.vibX, this.vibY, this.vibZ);
                int suspicion = this.queuedSuspicion;
                if (eventEntity != null) {
                    this.lastevententity = eventEntity;
                    increaseAngerAt(eventEntity, suspicion, true);
                    if (this.world.getTime() - reactionSoundTimer > 40) {
                        this.reactionSoundTimer = this.world.getTime();
                    }
                } else {
                    this.timeSinceNonEntity = this.world.getTime();
                    this.nonEntityAnger = this.nonEntityAnger + 3;
                    if (this.world.getTime() - reactionSoundTimer > 40) {
                        this.reactionSoundTimer = this.world.getTime();
                    }
                }
            } else {
                //this.playListeningSound();
                this.reactionSoundTimer = this.world.getTime();
                this.world.sendEntityStatus(this, (byte) 9);
                this.world.sendEntityStatus(this, (byte) 18);
                this.handleStatus((byte) 5);
                this.setInvisible(false);
            }
        }
    }

    public BlockPos lasteventpos;
    public World lasteventworld;
    public Entity lastevententity;
    public int vibX;
    public int vibY;
    public int vibZ;
    public int movementPriority;
    //Lists & Entity Tracking
    public String trackingEntity = "null";
    public String vibrationEntity = "null";
    public int queuedSuspicion;
    //Anger & Heartbeat
    public int nonEntityAnger;
    private static final TrackedData<Integer> ANGER = DataTracker.registerData(WardenEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private WardenAngerManager angerManager = new WardenAngerManager(this::isValidTarget, Collections.emptyList());
    //Emerging & Digging
    //Timers
    public long vibrationTimer;
    public long reactionSoundTimer;
    public int vibrationTicks=-1;
    //Stopwatches
    public long timeSinceNonEntity;

    public int delay = 0;
    protected float distance;

    //CLIENT VARIABLES (Use world.sendEntityStatus() to set these, we need to make "fake" variables for the client to use since that method is buggy)
    public int lightTransitionTicks;
    public int lastLightLevel;
    public boolean isLightHigher;

    //ANIMATION
    public AnimationState roaringAnimationState = new AnimationState();
    public AnimationState sniffingAnimationState = new AnimationState();
    public AnimationState emergingAnimationState = new AnimationState();
    public AnimationState diggingAnimationState = new AnimationState();
    public AnimationState attackingAnimationState = new AnimationState();
    public AnimationState chargingSonicBoomAnimationState = new AnimationState();

    //NO IDEA WHAT THESE ARE.
    private float field_38162;
    private float field_38163;
    private float field_38164;
    private float field_38165;

    public static final byte EARS_TWITCH = 61;
    public static final byte SONIC_BOOM = 62;

    //private final EntityGameEventHandler<VibrationListener> gameEventHandler = new EntityGameEventHandler<>(new VibrationListener(new EntityPositionSource(this, this.getStandingEyeHeight()), 16, this, null, 0, 0));

    /*@Override
    public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, net.frozenblock.wildmod.event.GameEvent event, net.frozenblock.wildmod.event.GameEvent.Emitter emitter) {
        if (!this.isAiDisabled() && !this.isDead() && !this.getBrain().hasMemoryModule(RegisterMemoryModules.VIBRATION_COOLDOWN) && !this.isDiggingOrEmerging() && world.getWorldBorder().contains(pos) && !this.isRemoved() && this.world == world) {
            Entity var7 = emitter.sourceEntity();
            boolean var10000;
            if (var7 instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)var7;
                if (!this.isValidTarget(livingEntity)) {
                    var10000 = false;
                    return var10000;
                }
            }

            var10000 = true;
            return var10000;
        } else {
            return false;
        }
    }

    @Override
    public void accept(ServerWorld world, GameEventListener listener, BlockPos pos, net.frozenblock.wildmod.event.GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, int delay) {
        this.brain.remember(RegisterMemoryModules.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
        world.sendEntityStatus(this, EARS_TWITCH);
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

        if (!this.getAngriness().isAngry()) {
            Optional<LivingEntity> optional = this.angerManager.getPrimeSuspect();
            if (sourceEntity != null || optional.isEmpty() || optional.get() == entity) {
                WardenBrain.lookAtDisturbance(this, blockPos);
            }
        }

    }
*/}
