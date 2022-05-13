package net.frozenblock.wildmod.entity;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.ai.*;
import net.frozenblock.wildmod.entity.ai.task.SonicBoomTask;
import net.frozenblock.wildmod.entity.ai.task.UpdateAttackTargetTask;
import net.frozenblock.wildmod.fromAccurateSculk.SculkTags;
import net.frozenblock.wildmod.liukrastapi.Angriness;
import net.frozenblock.wildmod.liukrastapi.AnimationState;
import net.frozenblock.wildmod.liukrastapi.MathAddon;
import net.frozenblock.wildmod.liukrastapi.WardenAngerManager;
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
import net.minecraft.entity.ai.pathing.*;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.*;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class WardenEntity extends HostileEntity {

    /** WELCOME TO THE WARDEN MUSEUM
     * ALL THESE WILL LINK TO THE FIRST METHOD IN THEIR GIVEN SECTIONS
     * SUSPICION {@link WardenEntity#addSuspicion(LivingEntity, int)}
     * SNIFFING & VIBRATIONS {@link WardenEntity#getVibrationEntity()}
     * ATTACKING {@link WardenEntity#tryAttack(Entity)}
     * NBT, VALUES & BOOLEANS {@link WardenEntity#writeCustomDataToNbt(NbtCompound)}
     * OVERRIDES & NON-WARDEN-SPECIFIC {@link WardenEntity#getHurtSound(DamageSource)}
     * VISUAlS {@link WardenEntity#createVibration(World, WardenEntity, BlockPos)}
     * TICKMOVEMENT METHODS {@link WardenEntity#tickEmerge()}
     * ALL VALUES ARE STORED AT THE END OF THIS MUSEUM.
     * */

    private static final Logger field_38138 = LogUtils.getLogger();
    protected void initGoals() {
        //this.goalSelector.add(1, new WardenSwimGoal(this));
        this.goalSelector.add(3, new WardenGoal(this, speed));
        //this.goalSelector.add(2, new SniffGoal(this, speed));
        //this.goalSelector.add(1, new WardenAttackNearGoal(this));
        //this.goalSelector.add(1, new WardenWanderGoal(this, 0.4));
    }

    public boolean isInRange(Entity entity, double horizontalRadius, double verticalRadius) {
        double d = entity.getX() - this.getX();
        double e = entity.getY() - this.getY();
        double f = entity.getZ() - this.getZ();
        return MathHelper.squaredHypot(d, f) < MathHelper.square(horizontalRadius) && MathHelper.square(e) < MathHelper.square(verticalRadius);
    }

    public void tickMovement() {
        if (!this.isAiDisabled()) {
            if (this.leaveTime-this.world.getTime() >= 1040) {this.getLookControl().lookAt(this.lookX, this.lookY, this.lookZ);}
            if (this.world.getTime() - this.vibrationTimer <= 100 && this.roarOtherCooldown <= 0 && this.movementPriority<=2) {
                LivingEntity lastEvent = this.getVibrationEntity();
                if (lastEvent!=null) {
                    if (this.getSuspicion(lastEvent)>30 && this.world.getTime() - this.vibrationTimer <= 19) {
                        this.getNavigation().stop();
                        BlockPos entityPos = lastEvent.getBlockPos();
                        this.getNavigation().startMovingTo(entityPos.getX(), entityPos.getY(), entityPos.getZ(), (speed + (MathHelper.clamp(this.getSuspicion(lastEvent), 0, 50) * 0.01) + (this.trueOverallAnger() * 0.0045)));
                        WardenBrain.lookAtDisturbance(this, entityPos);
                        this.movementPriority = 2;
                    }
                    if (this.getSuspicion(lastEvent)>44) {
                        this.getNavigation().stop();
                        BlockPos entityPos = lastEvent.getBlockPos();
                        this.getNavigation().startMovingTo(entityPos.getX(), entityPos.getY(), entityPos.getZ(), (speed + (MathHelper.clamp(this.getSuspicion(lastEvent), 0, 50) * 0.01) + (this.trueOverallAnger() * 0.0045)));
                        this.getLookControl().lookAt(lastEvent);
                        this.movementPriority = 2;
                    }
                } else {this.movementPriority=0;}
            }
            if (this.roarOtherCooldown > 0) { --this.roarOtherCooldown; }
            this.tickEmerge();
            this.tickVibration();
            if (this.attackNearCooldown>0) { --this.attackNearCooldown; }
        }
        //Movement
        if (this.ticksToWander>0) {--this.ticksToWander;}
        if (this.getNavigation().isIdle()) {this.movementPriority=0;}
        //Heartbeat & Anger
        this.heartbeatTime = getHeartRate();
        if (this.world.getTime()>=this.nextHeartBeat && !isAiDisabled()) {
            this.nextHeartBeat=this.world.getTime()+heartbeatTime;
            this.lastHeartBeat=this.world.getTime();
            this.world.sendEntityStatus(this, (byte)8);
        }
        if (this.world.getTime()-this.timeSinceNonEntity>300 && this.nonEntityAnger>0) { --this.nonEntityAnger; }
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
            if (this.lightTransitionTicks<100 && !this.isLightHigher) { ++this.lightTransitionTicks; }
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
        } else if (!this.isAiDisabled() && status == 7) { //Set Last Vibration Time
            this.vibrationTimer=this.world.getTime();
            this.canTendrilAnim=true;
        } else if (!this.isAiDisabled() && status == 8) { //Set Last Client Beat Time
            this.lastClientHeartBeat = this.world.getTime();
        } else {
            super.handleStatus(status);
        }
    }

    public void listen(BlockPos eventPos, World eventWorld, LivingEntity eventEntity, int suspicion, BlockPos vibrationPos) {
        boolean shouldListen = true;
        if (eventEntity instanceof PlayerEntity) { shouldListen = !((PlayerEntity)eventEntity).getAbilities().creativeMode; }
        if (!this.isAiDisabled() && shouldListen && this.roarOtherCooldown<=0 && !(this.emergeTicksLeft > 0) && this.world.getTime() - this.vibrationTimer >= 20 && this.vibrationTicks==-1 && this.emergeTicksLeft!=-5) {
            this.vibX = eventPos.getX();
            this.vibY = eventPos.getY();
            this.vibZ = eventPos.getZ();
            this.lasteventworld = eventWorld;
            if (eventEntity!=null) {
                this.vibrationEntity = eventEntity.getUuidAsString();
            } else { this.vibrationEntity = "null"; }
            this.hasDetected = true;
            this.leaveTime = this.world.getTime() + 1200;
            this.queuedSuspicion=suspicion;
            if (vibrationPos != null) { createVibration(this.world, this, vibrationPos);
                this.vibrationTicks=(int)(Math.floor(Math.sqrt(this.getBlockPos().getSquaredDistance(vibrationPos.getX(), vibrationPos.getY(), vibrationPos.getZ()))) * this.vibrationDelayAnger());}
            else { createVibration(this.world, this, eventPos);
                this.vibrationTicks=(int)(Math.floor(Math.sqrt(this.getBlockPos().getSquaredDistance(eventPos.getX(), eventPos.getY(), eventPos.getZ()))) * this.vibrationDelayAnger());}
        } else if (!this.isAiDisabled() && shouldListen && this.roarOtherCooldown<=0 && this.emergeTicksLeft==-5  && this.world.getTime() - this.vibrationTimer >= 20 && this.vibrationTicks==-1 && this.emergeTicksLeft==-5) {
            this.leaveTime = this.world.getTime() + 1200;
            if (vibrationPos != null) { createFloorVibration(this.world, this, vibrationPos);
                this.vibrationTicks=(int)(Math.floor(Math.sqrt(this.getBlockPos().getSquaredDistance(vibrationPos.getX(), vibrationPos.getY(), vibrationPos.getZ()))) * 2);}
            else { createFloorVibration(this.world, this, eventPos);
                this.vibrationTicks=(int)(Math.floor(Math.sqrt(this.getBlockPos().getSquaredDistance(eventPos.getX(), eventPos.getY(), eventPos.getZ()))) * 2);}
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
        if (var2 instanceof ServerWorld) {
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

    /** SUSPICION */
    public void addSuspicion(LivingEntity entity, int suspicion) {
        if (this.world.getDifficulty().getId() != 0 && entity != null && !(entity instanceof WardenEntity)) {
            if (!this.entityList.isEmpty()) {
                if (this.entityList.contains(entity.getUuid().hashCode())) {
                    int slot = this.entityList.indexOf(entity.getUuid().hashCode());
                    this.susList.set(slot, this.susList.getInt(slot) + suspicion);
                    if (this.susList.getInt(slot) >= 45 && this.getTrackingEntity() == null) {
                        this.trackingEntity = entity.getUuidAsString();
                    }
                } else { this.entityList.add(entity.getUuid().hashCode());
                    this.susList.add(suspicion);
                }
            } else { this.entityList.add(entity.getUuid().hashCode());
                this.susList.add(suspicion);
            }
        }
    }
    public int getSuspicion(Entity entity) {
        if (!this.entityList.isEmpty() && entity!=null && !(entity instanceof WardenEntity)) {
            if (this.entityList.contains(entity.getUuid().hashCode())) {
                return this.susList.getInt(this.entityList.indexOf(entity.getUuid().hashCode()));
            }
        } return 0;
    }
    public int eventSuspicionValue(GameEvent event, LivingEntity livingEntity) {
        int total=1;
        if (event==GameEvent.PROJECTILE_LAND) { return 0; }
        if (SculkSensorBlock.FREQUENCIES.containsKey(event)) { total=total + SculkSensorBlock.FREQUENCIES.getInt(event); }
        if (livingEntity instanceof PlayerEntity) {
            return MathHelper.clamp(total, 3,15);
        }
        return MathHelper.clamp(total, 2,15);
    }
    public int trueOverallAnger() {
        int anger=0;
        if (this.world.getDifficulty().getId()!=0) {
            Box box = new Box(this.getBlockPos().add(-24, -24, -24), this.getBlockPos().add(24, 24, 24));
            List<LivingEntity> entities = world.getNonSpectatingEntities(LivingEntity.class, box);
            if (!entities.isEmpty()) {
                for (LivingEntity target : entities) {anger = anger + this.getSuspicion(target); }
            }
            anger = anger + nonEntityAnger;
            anger = MathHelper.clamp(anger, 0, 50);
            /*if (this.headRoll!=headRollFromAnger(anger)) {
                this.world.sendEntityStatus(this, this.statusForHeadroll(headRollFromAnger(anger)));
            }
        */} return anger;
    }

    public double vibrationDelayAnger() {
        int a = this.trueOverallAnger();
        a = a/27;
        return MathHelper.clamp(2-a,0.1,2);
    }
    public LivingEntity getTrackingEntity() {
        Box box = new Box(this.getBlockPos().add(-24,-24,-24), this.getBlockPos().add(24,24,24));
        List<LivingEntity> entities = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        if (!entities.isEmpty()) {
            for (LivingEntity target : entities) {
                if (Objects.equals(this.trackingEntity, target.getUuidAsString()) && !(target instanceof WardenEntity) && MathAddon.distance(target.getX(), target.getY(), target.getZ(), this.getX(), this.getY(), this.getZ()) <= 24) { return target; }
            }
        } return null;
    }
    public LivingEntity getTrackingEntityForRoarNavigation() {
        Box box = new Box(this.getBlockPos().add(-32,-32,-32), this.getBlockPos().add(32,32,32));
        List<LivingEntity> entities = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        if (!entities.isEmpty()) {
            for (LivingEntity target : entities) {
                if (Objects.equals(this.trackingEntity, target.getUuidAsString()) && !(target instanceof WardenEntity)) { return target; }
            }
        } return null;
    }
    public LivingEntity mostSuspiciousAround() {
        int highest = 0;
        LivingEntity most = null;
        Box box = new Box(this.getBlockPos().add(-16,-16,-16), this.getBlockPos().add(16,16,16));
        List<LivingEntity> entities = world.getNonSpectatingEntities(LivingEntity.class, box);
        if (!entities.isEmpty()) {
            for (LivingEntity target : entities) {
                if (this.getBlockPos().getSquaredDistance(target.getBlockPos())<=16 && this.getSuspicion(target)>highest && !(target instanceof WardenEntity)) {
                    highest = this.getSuspicion(target);
                    most = target;
                }
            }
        } return most;
    }

    public LivingEntity getVibrationEntity() {
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
        }return null;
    }
    /** ATTACKING */
    public boolean tryAttack(Entity target) {
        this.world.sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
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
        this.dataTracker.set(ANGER, this.method_43999());
    }

    public LivingEntity getClosestEntity() {
        Box box = new Box(this.getBlockPos().add(-2.15, -2.15, -2.15), this.getBlockPos().add(2.15, 2.15, 2.15));
        List<LivingEntity> entities = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        double closest=3;
        LivingEntity chosen=null;
        if (!entities.isEmpty()) {
            for (LivingEntity target : entities) {
                if (!(target instanceof WardenEntity) && this.squaredDistanceTo(target)<closest) {
                    closest=this.squaredDistanceTo(target);
                    chosen=target;
                }
            }
        } return chosen;
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
                .resultOrPartial(field_38138::error)
                .ifPresent(angerNbt -> nbt.put("anger", angerNbt));
        nbt.putLong("vibrationTimer", this.vibrationTimer);
        nbt.putLong("leaveTime", this.leaveTime);
        nbt.putInt("emergeTicksLeft", this.emergeTicksLeft);
        nbt.putBoolean("hasEmerged", this.hasEmerged);
        nbt.putBoolean("hasSentStatusStart", this.hasSentStatusStart);
        nbt.putIntArray("entityList", this.entityList);
        nbt.putIntArray("susList", this.susList);
        nbt.putString("trackingEntity", this.trackingEntity);
        nbt.putInt("sniffCooldown", this.sniffCooldown);
        nbt.putInt("nonEntityAnger", this.nonEntityAnger);
        nbt.putLong("timeSinceNonEntity", this.timeSinceNonEntity);
        nbt.putString("vibrationEntity", this.vibrationEntity);
        nbt.putInt("vibX", this.vibX);
        nbt.putInt("vibY", this.vibY);
        nbt.putInt("vibZ", this.vibZ);
        nbt.putInt("queuedSuspicion", this.queuedSuspicion);
        nbt.putInt("attackNearCooldown", this.attackNearCooldown);
        nbt.putInt("roarOtherCooldown", this.roarOtherCooldown);
        nbt.putBoolean("ableToDig", this.ableToDig);
        nbt.putBoolean("hasDug", this.hasDug);
        nbt.putInt("movementPriority", this.movementPriority);
        nbt.putInt("ticksToWander", this.ticksToWander);
        nbt.putDouble("lookX", this.lookX);
        nbt.putDouble("lookY", this.lookY);
        nbt.putDouble("lookZ", this.lookZ);
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
        this.vibrationTimer = nbt.getLong("vibrationTimer");
        this.leaveTime = nbt.getLong("leaveTime");
        this.emergeTicksLeft = nbt.getInt("emergeTicksLeft");
        this.hasEmerged = nbt.getBoolean("hasEmerged");
        this.hasSentStatusStart = nbt.getBoolean("hasSentStatusStart");
        this.entityList = IntArrayList.wrap(nbt.getIntArray("entityList"));
        this.susList = IntArrayList.wrap(nbt.getIntArray("susList"));
        this.trackingEntity = nbt.getString("trackingEntity");
        this.sniffCooldown = nbt.getInt("sniffCooldown");
        this.nonEntityAnger = nbt.getInt("nonEntityAnger");
        this.timeSinceNonEntity = nbt.getLong("timeSinceNonEntity");
        this.vibrationEntity = nbt.getString("vibrationEntity");
        this.vibX = nbt.getInt("vibX");
        this.vibY = nbt.getInt("vibY");
        this.vibZ = nbt.getInt("vibZ");
        this.queuedSuspicion = nbt.getInt("queuedSuspicion");
        this.attackNearCooldown = nbt.getInt("attackNearCooldown");
        this.roarOtherCooldown = nbt.getInt("roarOtherCooldown");
        this.ableToDig = nbt.getBoolean("ableToDig");
        this.hasDug = nbt.getBoolean("hasDug");
        this.movementPriority = nbt.getInt("movementPriority");
        this.ticksToWander = nbt.getInt("ticksToWander");
        this.lookX = nbt.getDouble("lookX");
        this.lookY = nbt.getDouble("lookY");
        this.lookZ = nbt.getDouble("lookZ");
    }

    @Deprecated
    public boolean canFollow(Entity entity, boolean mustBeTracking) {
        Box box = new Box(this.getBlockPos().add(-20,-20,-20), this.getBlockPos().add(20,20,20));
        List<Entity> entities = world.getNonSpectatingEntities(Entity.class, box);
        if (!entities.isEmpty() && entities.contains(entity)) {
            if (MathAddon.distance(entity.getX(), entity.getY(), entity.getZ(), this.getX(), this.getY(), this.getZ()) <= 18) {
                if (mustBeTracking) {
                    return entity == this.getTrackingEntity();
                } else {
                    return true;
                }
            }
        } return false;
    }
    @Deprecated
    public int mostSuspiciousAroundInt() {
        int value=0;
        if (mostSuspiciousAround()!=null) {value=this.getSuspicion(mostSuspiciousAround());}
        return value;
    }
    @Deprecated
    public int getHighestSuspicionInt() {
        int highest = 0;
        if (!this.susList.isEmpty()) {
            for (int i=0; i<this.susList.size(); i++) {
                if (this.susList.getInt(i)>highest) {highest=this.susList.getInt(i);}
            }
        } return highest;
    }

    /** OVERRIDES & NON-WARDEN-SPECIFIC */
    public void onPlayerCollision(PlayerEntity player) {
        if (this.age % 20 == 0) {
            if (!player.getAbilities().creativeMode) {this.addSuspicion(player,10);}
            this.leaveTime=this.world.getTime()+1200;
            this.lookX=player.getX();
            this.lookY=player.getY();
            this.lookZ=player.getZ();
        }
    }
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

    public SoundCategory getSoundCategory() { return SoundCategory.HOSTILE; }

    protected float getSoundVolume() { return 4.0F; }

    protected SoundEvent getDeathSound() { return RegisterSounds.ENTITY_WARDEN_DEATH; }
    protected boolean isDisallowedInPeaceful() { return false; }
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
        return Angriness.getForAnger(this.method_43999());
    }

    private int method_43999() {
        return this.angerManager.getPrimeSuspectAnger(this.getTarget());
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
            if (entity instanceof PlayerEntity && bl && Angriness.getForAnger(i).isAngry()) {
                this.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
            }

            if (listening) {
                this.playListeningSound();
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
        this.disablesShield();
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

    public boolean disablesShield() {
        return this.getMainHandStack().getItem() instanceof AxeItem;
    }

    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.55F;
    }

    @Override
    public int getSafeFallDistance() { return 16; }

    public static DefaultAttributeContainer.Builder addAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30000001192092896).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0);
    }

    public boolean occludeVibrationSignals() {
        return true;
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.getBrain().remember(RegisterMemoryModules.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
        if (spawnReason == SpawnReason.TRIGGERED) {
            this.setPose(WildMod.EMERGING);
            this.getBrain().remember(RegisterMemoryModules.IS_EMERGING, Unit.INSTANCE, WardenBrain.EMERGE_DURATION);
            this.playSound(RegisterSounds.ENTITY_WARDEN_AGITATED, 5.0F, 1.0F);
        }
        this.leaveTime=this.world.getTime()+1200;
        this.setPersistent();
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void onKilledOther(ServerWorld world, LivingEntity other) {
        super.onKilledOther(world, other);
        if (this.getSuspicion(other)!=0) {
            this.susList.removeInt(this.entityList.indexOf(other.getUuid().hashCode()));
            this.entityList.removeInt(this.entityList.indexOf(other.getUuid().hashCode()));
            if (this.getTrackingEntityForRoarNavigation()!=null && other==this.getTrackingEntityForRoarNavigation()) {
                this.trackingEntity="null";
            }
        }
    }

    /** VISUALS */
        public void createVibration(World world, WardenEntity warden, BlockPos blockPos2) {
        WardenPositionSource wardenPositionSource = new WardenPositionSource(this.getId());
        this.delay = this.distance = (int)(Math.floor(Math.sqrt(warden.getBlockPos().getSquaredDistance(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()))) * this.vibrationDelayAnger());
        ((ServerWorld)world).sendVibrationPacket(new Vibration(blockPos2, wardenPositionSource, this.delay));
    }
    public void createFloorVibration(World world, WardenEntity warden, BlockPos blockPos2) {
        BlockPositionSource blockSource = new BlockPositionSource(this.getBlockPos().down());
        this.delay = this.distance = (int)(Math.floor(Math.sqrt(warden.getBlockPos().getSquaredDistance(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()))) * 2);
        ((ServerWorld)world).sendVibrationPacket(new Vibration(blockPos2, blockSource, this.delay));
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
            } else if (this.isInPose(WildMod.DIGGING)) {
                this.diggingAnimationState.start();
            } else if (this.isInPose(WildMod.ROARING)) {
                this.roaringAnimationState.start();
            } else if (this.isInPose(WildMod.SNIFFING)) {
                this.sniffingAnimationState.start();
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

    public boolean damage(DamageSource source, float amount) {
        boolean bl = super.damage(source, amount);
        if (!this.world.isClient && !this.isAiDisabled() && amount > 0.0F) {
            Entity entity = source.getAttacker();
            LivingEntity livingEntity = (LivingEntity)entity;
            this.addSuspicion(livingEntity, Angriness.ANGRY.getThreshold() + 20);
            if (this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()) {
                if (!(source instanceof ProjectileDamageSource) || this.isInRange(livingEntity, 5.0)) {
                    this.updateAttackTarget(livingEntity);
                }
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

    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world) {
            protected PathNodeNavigator createPathNodeNavigator(int range) {
                this.nodeMaker = new LandPathNodeMaker();
                this.nodeMaker.setCanEnterOpenDoors(true);
                return new PathNodeNavigator(this.nodeMaker, range) {
                    //protected float method_44000(WildPathNode pathNode, PathNode pathNode2) {
                        //return pathNode.method_44022(pathNode2);
                    //}
                };
            }
        };
    }

    @Override
    public boolean isTouchingWater() {
        return this.submergedInWater;
    }

    /** TICKMOVEMENT METHODS */
    public void tickEmerge() {
        if (!this.hasSentStatusStart) { //Set Client EmergeTicks To 160
            this.world.sendEntityStatus(this, (byte)9);
            this.hasSentStatusStart=true;
        }
        if (this.emergeTicksLeft > 0 && !this.hasEmerged) { //Tick Down Emerging
            this.setVelocity(0, 0, 0);
            this.emergeTicksLeft--;
        }
        if (this.emergeTicksLeft == 0 && !this.hasEmerged) { //Stop Emerging
            this.world.sendEntityStatus(this, (byte)12);
            this.hasEmerged = true;
            this.sniffCooldown = random.nextInt(5,110);
            this.emergeTicksLeft = -1;
        }
        if (world.getTime()==this.leaveTime) { this.ableToDig=true; }
        if (this.digAttemptCooldown>0) { --this.digAttemptCooldown; }
        if (this.ableToDig && this.digAttemptCooldown == 0 && this.emergeTicksLeft == -1) { //Start Digging
            if (!SculkTags.blockTagContains(world.getBlockState(this.getBlockPos().down()).getBlock(), SculkTags.WARDEN_UNSPAWNABLE) && !world.getBlockState(this.getBlockPos().down()).isAir()) {
                this.world.sendEntityStatus(this, (byte) 11);
                this.handleStatus((byte) 6);
                this.hasDug=true;
                this.susList.clear();
                this.entityList.clear();
            } else this.digAttemptCooldown = 240;
        }
        if (this.emergeTicksLeft > 0 && this.hasEmerged) { //Tick Down While Digging
            this.setVelocity(0, 0, 0);
            --this.emergeTicksLeft;
        }
        if (this.emergeTicksLeft == 0 && this.hasEmerged && this.ableToDig) { //Handle Finish Digging
            if (!this.hasCustomName()) {
                this.remove(RemovalReason.DISCARDED);
            } else {
                this.setInvisible(true);
                this.emergeTicksLeft=-5;
                this.world.sendEntityStatus(this, (byte)17);
            }
        }
        if (this.sendRenderBooleanCooldown>0) { --this.sendRenderBooleanCooldown; }
        if (this.sendRenderBooleanCooldown == 0 && this.hasEmerged && this.ableToDig) { //Sync shouldRender With Clients
            if (this.hasCustomName() && this.emergeTicksLeft==-5) {
                this.world.sendEntityStatus(this, (byte)17);
                this.setInvisible(true);
                this.sendRenderBooleanCooldown=120;
            }
        }
    }

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
        this.world.sendEntityStatus(this, (byte)7);
        this.world.sendEntityStatus(this, (byte)15);
        this.vibrationTimer=this.world.getTime();
        this.world.playSound(null, this.getBlockPos().up(2), RegisterSounds.ENTITY_WARDEN_TENDRIL_CLICKS, this.getSoundCategory(), 5.0F, world.random.nextFloat() * 0.2F + 0.8F);
        this.ableToDig=false;
        this.hasDug=false;
        if (this.emergeTicksLeft != -5) {
            LivingEntity eventEntity = this.getVibrationEntity();
            this.lasteventpos = new BlockPos(this.vibX, this.vibY, this.vibZ);
            this.lookX=this.vibX;
            this.lookY=this.vibY;
            this.lookZ=this.vibZ;
            int suspicion = this.queuedSuspicion;
            if (eventEntity != null) {
                this.lastevententity = eventEntity;
                addSuspicion(eventEntity, suspicion);
                if (this.world.getTime() - reactionSoundTimer > 40) {
                    this.reactionSoundTimer = this.world.getTime();
                    if (getSuspicion(eventEntity) < 25) {
                        this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_LISTENING, this.getSoundCategory(), 10.0F, world.random.nextFloat() * 0.2F + 0.8F);
                    } else if (getSuspicion(eventEntity) > 25) {
                        this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_LISTENING_ANGRY, this.getSoundCategory(), 10.0F, world.random.nextFloat() * 0.2F + 0.8F);
                    }
                }
            } else {
                this.timeSinceNonEntity = this.world.getTime();
                this.nonEntityAnger = this.nonEntityAnger + 3;
                if (this.world.getTime() - reactionSoundTimer > 40) {
                    this.reactionSoundTimer = this.world.getTime();
                    if (this.trueOverallAnger() < 25) {
                        this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_LISTENING, this.getSoundCategory(), 10.0F, world.random.nextFloat() * 0.2F + 0.8F);
                    } else {
                        this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_LISTENING, this.getSoundCategory(), 10.0F, world.random.nextFloat() * 0.2F + 0.8F);
                    }
                }
            }
        } else {
            this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_LISTENING, this.getSoundCategory(), 10.0F, world.random.nextFloat() * 0.2F + 0.8F);
            this.reactionSoundTimer = this.world.getTime();
            this.world.sendEntityStatus(this, (byte)9);
            this.world.sendEntityStatus(this, (byte)18);
            this.handleStatus((byte)5);
            this.setInvisible(false);
        }
    }

    //Movement
    public int timeStuck=0;
    public BlockPos stuckPos;
    public long timeSinceLastRecalculation;
    public BlockPos lasteventpos;
    public World lasteventworld;
    public LivingEntity lastevententity;
    public LivingEntity navigationEntity;
    public int vibX;
    public int vibY;
    public int vibZ;
    public int movementPriority;
    public int ticksToWander;
    public double lookX;
    public double lookY;
    public double lookZ;
    //Lists & Entity Tracking
    public IntArrayList entityList = new IntArrayList();
    public IntArrayList susList = new IntArrayList();
    public String trackingEntity = "null";
    public String vibrationEntity = "null";
    public int queuedSuspicion;
    //Anger & Heartbeat
    public int heartbeatTime = getHeartRate();
    public int nonEntityAnger;
    public long nextHeartBeat;
    public long lastHeartBeat;
    private static final TrackedData<Integer> ANGER = DataTracker.registerData(WardenEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private WardenAngerManager angerManager = new WardenAngerManager(this::isValidTarget, Collections.emptyList());
    //Emerging & Digging
    public boolean hasDetected=false;
    public boolean hasEmerged;
    public boolean hasSentStatusStart;
    public int emergeTicksLeft;
    public int digAttemptCooldown;
    public boolean ableToDig;
    public boolean hasDug;
    public int sendRenderBooleanCooldown;
    //Timers
    public long leaveTime;
    public long vibrationTimer;
    public long reactionSoundTimer;
    public int attackNearCooldown;
    public int roarOtherCooldown;
    public int vibrationTicks=-1;
    //Stopwatches
    public long timeSinceNonEntity;
    public int sniffCooldown;

    public int delay = 0;
    protected int distance;
    private static final double speed = 0.4D;

    //CLIENT VARIABLES (Use world.sendEntityStatus() to set these, we need to make "fake" variables for the client to use since that method is buggy)
    public long lastClientHeartBeat; //Status 8
    public long clientSniffStart; //Status 10
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
    public boolean canTendrilAnim; //Status 7
    public float tendrilAnimStartTime=-200;

    //NO IDEA WHAT THESE ARE.
    private float field_38162;
    private float field_38163;
    private float field_38164;
    private float field_38165;

    public static final byte EARS_TWITCH = 61;
    public static final byte SONIC_BOOM = 62;
}
