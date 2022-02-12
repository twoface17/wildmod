package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.fromAccurateSculk.SculkTags;
import frozenblock.wild.mod.fromAccurateSculk.WardenPositionSource;
import frozenblock.wild.mod.liukrastapi.*;
import frozenblock.wild.mod.registry.RegisterAccurateSculk;
import frozenblock.wild.mod.registry.RegisterSounds;
import frozenblock.wild.mod.registry.RegisterStatusEffects;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.*;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class WardenEntity extends HostileEntity {
    /** WELCOME TO THE WARDEN MUSEUM
     * ALL THESE WILL LINK TO THE FIRST METHOD IN THEIR GIVEN SECTIONS
     * SUSPICION {@link WardenEntity#addSuspicion(LivingEntity, int)}
     * SNIFFING & VIBRATIONS {@link WardenEntity#getSniffEntity()}
     * ATTACKING & ROARING {@link WardenEntity#roar()}
     * NBT, VALUES & BOOLEANS {@link WardenEntity#writeCustomDataToNbt(NbtCompound)}
     * OVERRIDES & NON-WARDEN-SPECIFIC {@link WardenEntity#getHurtSound(DamageSource)}
     * VISUAlS {@link WardenEntity#CreateVibration(World, WardenEntity, BlockPos)}
     * TICKMOVEMENT METHODS {@link WardenEntity#tickEmerge()}
     * ALL VALUES ARE STORED AT THE END OF THIS MUSEUM.
     * */

    protected void initGoals() {
        this.goalSelector.add(1, new WardenSwimGoal(this));
        this.goalSelector.add(3, new WardenGoal(this, speed));
        this.goalSelector.add(2, new SniffGoal(this, speed));
        this.goalSelector.add(1, new WardenAttackNearGoal(this));
        this.goalSelector.add(1, new WardenWanderGoal(this, 0.4));
    }

    public void tickMovement() {
        this.tickBurn();
        if (!this.isAiDisabled()) {
            if (this.attackTicksLeft1 > 0) { --this.attackTicksLeft1; }
            if (this.roarTicksLeft1 > 0) {
                --this.roarTicksLeft1;
                this.getNavigation().stop();
            }
            if (this.roarTicksLeft1==0) {
                this.roarTicksLeft1=-1;
                this.world.sendEntityStatus(this, (byte)14);
            }
            if (this.roarTicksLeft1<=0 && this.roarOtherCooldown > 0) {
                LivingEntity lastEvent = this.getTrackingEntityForRoarNavigation();
                if (lastEvent!=null) {
                    BlockPos roarPos = lastEvent.getBlockPos();
                    this.getNavigation().startMovingTo(roarPos.getX(), roarPos.getY(), roarPos.getZ(), (speed + (45 * 0.01) + (45 * 0.002)));
                }
            }
            if (this.roarOtherCooldown > 0) { --this.roarOtherCooldown; }
            if (this.attackCooldown > 0) { --this.attackCooldown; }
            this.tickEmerge();
            this.tickStuck();
            this.tickSniff();
            this.tickVibration();
            if (this.ticksToDarkness > 0) { --this.ticksToDarkness; }
            if (this.ticksToDarkness==0) {
                this.sendDarkness(48, this.getBlockPos(), this.world);
                this.ticksToDarkness=100;
            }
            if (this.attackNearCooldown>0) { --this.attackNearCooldown; }
        }
        //Heartbeat & Anger
        this.heartbeatTime = (int) (40 - ((MathHelper.clamp(this.trueOverallAnger(),0,50)*0.6)));
        if (this.world.getTime()>=this.nextHeartBeat && !isAiDisabled()) {
            if (!this.hasDug && this.emergeTicksLeft!=-5) {
                this.world.playSound(null, this.getBlockPos().up(), RegisterSounds.ENTITY_WARDEN_HEARTBEAT, SoundCategory.HOSTILE, 1F, (float) (0.85F + (MathHelper.clamp(this.trueOverallAnger(), 0, 45) * 0.0066)));
            }
            this.nextHeartBeat=this.world.getTime()+heartbeatTime;
            this.lastHeartBeat=this.world.getTime();
            this.world.sendEntityStatus(this, (byte)8);
        }
        if (this.world.getTime()-this.timeSinceNonEntity>300 && this.nonEntityAnger>0) { --this.nonEntityAnger; }
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
        if (!this.isAiDisabled() && status == 4) { //Set Attack Ticks And Set CanAttackAnim Boolean
            this.attackTicksLeft1 = 10;
            this.canAttackAnim=true;
            world.playSound(null, this.getBlockPos(), RegisterSounds.ENTITY_WARDEN_AMBIENT, SoundCategory.HOSTILE, 1.0F,1.0F);
        } else if(!this.isAiDisabled() && status == 3) { //Set CanRoarAnim Boolean
            this.canRoarAnim=true;
        } else if(!this.isAiDisabled() && status == 5) { //Emerging
            this.emergeTicksLeft=150;
            this.hasEmerged=false;
            world.playSound(null, this.getBlockPos(), RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.HOSTILE, 1F, 1F);
        } else if(!this.isAiDisabled() && status == 6) { //Digging Back
            this.emergeTicksLeft=60;
            this.hasEmerged=true;
            world.playSound(null, this.getBlockPos(), RegisterSounds.ENTITY_WARDEN_DIG, SoundCategory.HOSTILE, 1F, 1F);
        } else if (!this.isAiDisabled() && status == 7) { //Set Last Vibration Time
            this.vibrationTimer=this.world.getTime();
            this.canTendrilAnim=true;
        } else if (!this.isAiDisabled() && status == 8) { //Set Last Client Beat Time
            this.lastClientHeartBeat=this.world.getTime();
        } else if (!this.isAiDisabled() && status == 9) { //Set CanEmergeAnim Boolean
            this.canEmergeAnim=true;
        } else if (!this.isAiDisabled() && status == 10) { //Set Sniff Starting Time & CanSniffAnim Boolean
            this.clientSniffStart=this.world.getTime();
            this.canSniffAnim=true;
        } else if (!this.isAiDisabled() && status == 11) { //Set CanDigAnim Boolean
            this.canDigAnim=true;
        } else if (!this.isAiDisabled() && status == 12) { //Stop Emerge Animation
            this.stopEmergeAnim=true;
            this.canEmergeAnim=false;
        } else if (!this.isAiDisabled() && status == 13) { //Stop Dig Animation
            this.stopDigAnim=true;
            this.canDigAnim=false;
        } else if (!this.isAiDisabled() && status == 14) { //Stop Roar Animation
            this.stopRoarAnim=true;
            this.canRoarAnim=false;
        } else if (!this.isAiDisabled() && status == 15) { //Stop Sniff Animation
            this.stopSniffAnim=true;
            this.canSniffAnim=false;
        } else if (!this.isAiDisabled() && status == 16) { //Stop Attack Animation
            this.stopAttackAnim=true;
            this.canAttackAnim=false;
        } else if (status == 17) { //Don't Render
            this.shouldRender=false;
            this.hasDug=true;
        } else if (status == 18) { //Render
            this.shouldRender=true;
            this.hasDug=false;
        } else if (status == 24) { //Headroll 0
            this.headRoll=0;
        }  else if (status == 25) { //Headroll 1
            this.headRoll=1;
        } else if (status == 26) { //Headroll 2
            this.headRoll=2;
        } else if (status == 27) { //Headroll 3
            this.headRoll=3;
        } else if (status == 28) { //Headroll 4
            this.headRoll=4;
        } else { super.handleStatus(status); }
    }

    public void listen(BlockPos eventPos, World eventWorld, LivingEntity eventEntity, int suspicion, BlockPos vibrationPos) {
        boolean shouldListen = true;
        if (eventEntity instanceof PlayerEntity) { shouldListen = !((PlayerEntity)eventEntity).getAbilities().creativeMode; }
        if (!this.isAiDisabled() && shouldListen && this.roarOtherCooldown<=0 && !(this.emergeTicksLeft > 0) && this.world.getTime() - this.vibrationTimer >= 20 && this.vibrationTicks==-1 && this.emergeTicksLeft!=-5) {
            this.sniffTicksLeft=-1;
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
            if (vibrationPos != null) { CreateVibration(this.world, this, vibrationPos);
                this.vibrationTicks=(int)(Math.floor(Math.sqrt(this.getBlockPos().getSquaredDistance(vibrationPos, false))) * this.vibrationDelayAnger());}
            else { CreateVibration(this.world, this, eventPos);
                this.vibrationTicks=(int)(Math.floor(Math.sqrt(this.getBlockPos().getSquaredDistance(eventPos, false))) * this.vibrationDelayAnger());}
        } else if (!this.isAiDisabled() && shouldListen && this.roarOtherCooldown<=0 && this.emergeTicksLeft==-5  && this.world.getTime() - this.vibrationTimer >= 20 && this.vibrationTicks==-1 && this.emergeTicksLeft==-5) {
            this.leaveTime = this.world.getTime() + 1200;
            if (vibrationPos != null) { CreateFloorVibration(this.world, this, vibrationPos);
                this.vibrationTicks=(int)(Math.floor(Math.sqrt(this.getBlockPos().getSquaredDistance(vibrationPos, false))) * 2);}
            else { CreateFloorVibration(this.world, this, eventPos);
                this.vibrationTicks=(int)(Math.floor(Math.sqrt(this.getBlockPos().getSquaredDistance(eventPos, false))) * 2);}
        }
    }

    /** SUSPICION */
    public void addSuspicion(LivingEntity entity, int suspicion) {
        if (this.world.getDifficulty().getId() != 0 && !(entity instanceof WardenEntity)) {
            if (!this.entityList.isEmpty()) {
                if (this.entityList.contains(entity.getUuid().hashCode())) {
                    int slot = this.entityList.indexOf(entity.getUuid().hashCode());
                    this.susList.set(slot, this.susList.getInt(slot) + suspicion);
                    if (this.susList.getInt(slot) >= 45 && this.getTrackingEntity() == null) {
                        this.trackingEntity = entity.getUuidAsString();
                        this.world.playSound(null, this.getBlockPos().up(), RegisterSounds.ENTITY_WARDEN_ROAR, SoundCategory.HOSTILE, 1F, 1F);
                        this.roar();
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
            if (this.headRoll!=headRollFromAnger(anger)) {
                this.world.sendEntityStatus(this, this.statusForHeadroll(headRollFromAnger(anger)));
            }
        } return anger;
    }
    public int headRollFromAnger(int i) {
        if (i<10) {return 0;}
        if (i<20) {return 1;}
        if (i<30) {return 2;}
        if (i<40) {return 3;}
        return 4;
    }
    public byte statusForHeadroll(int i) {
        if (i==0) {return (byte)24;}
        if (i==1) {return (byte)25;}
        if (i==2) {return (byte)26;}
        if (i==3) {return (byte)27;}
        return 28;
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

    /** SNIFFING & VIBRATIONS */
    public LivingEntity getSniffEntity() {
        Box box = new Box(this.getBlockPos().add(-32,-32,-32), this.getBlockPos().add(32,32,32));
        List<LivingEntity> entities = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        if (!entities.isEmpty()) {
            for (LivingEntity target : entities) {
                if (Objects.equals(this.sniffEntity, target.getUuidAsString()) && !(target instanceof WardenEntity)) { return target; }
            }
        } return null;
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
    /** ATTACKING & ROARING */
    public void roar() {
        this.getNavigation().stop();
        this.attackTicksLeft1 = 10;
        this.world.sendEntityStatus(this, (byte)3);
        this.roarTicksLeft1 = 70;
        this.roarOtherCooldown = 130;
    }

    public boolean tryAttack(Entity target) {
        if (this.world.getDifficulty().getId()!=0 && !(target instanceof WardenEntity)) {
            boolean bl = target.damage(DamageSource.mob(this), this.getAttackDamage());
            if (bl && this.attackCooldown <= 0) {
                this.attackTicksLeft1 = 10;
                this.world.sendEntityStatus(this, (byte) 4);
                target.setVelocity(target.getVelocity().add(0.0D, 0.4000000059604645D, 0.0D));
                this.applyDamageEffects(this, target);
                world.playSound(null, this.getBlockPos(), RegisterSounds.ENTITY_WARDEN_ATTACK, SoundCategory.HOSTILE, 1.0F, 1.0F);
                this.attackCooldown = 35;
            } return bl;
        } return false;
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

    /** NBT, VALUES & BOOLEANS */
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putLong("vibrationTimer", this.vibrationTimer);
        nbt.putLong("leaveTime", this.leaveTime);
        nbt.putInt("emergeTicksLeft", this.emergeTicksLeft);
        nbt.putBoolean("hasEmerged", this.hasEmerged);
        nbt.putBoolean("hasSentStatusStart", this.hasSentStatusStart);
        nbt.putIntArray("entityList", this.entityList);
        nbt.putIntArray("susList", this.susList);
        nbt.putString("trackingEntity", this.trackingEntity);
        nbt.putInt("sniffTicksLeft", this.sniffTicksLeft);
        nbt.putInt("sniffCooldown", this.sniffCooldown);
        nbt.putInt("attackCooldown", this.attackCooldown);
        nbt.putDouble("sniffX", this.sniffX);
        nbt.putDouble("sniffY", this.sniffY);
        nbt.putDouble("sniffZ", this.sniffZ);
        nbt.putString("sniffEntity", this.sniffEntity);
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
    }
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.vibrationTimer = nbt.getLong("vibrationTimer");
        this.leaveTime = nbt.getLong("leaveTime");
        this.emergeTicksLeft = nbt.getInt("emergeTicksLeft");
        this.hasEmerged = nbt.getBoolean("hasEmerged");
        this.hasSentStatusStart = nbt.getBoolean("hasSentStatusStart");
        this.entityList = IntArrayList.wrap(nbt.getIntArray("entityList"));
        this.susList = IntArrayList.wrap(nbt.getIntArray("susList"));
        this.trackingEntity = nbt.getString("trackingEntity");
        this.sniffTicksLeft = nbt.getInt("sniffTicksLeft");
        this.sniffCooldown = nbt.getInt("sniffCooldown");
        this.attackCooldown = nbt.getInt("attackCooldown");
        this.sniffX = nbt.getDouble("sniffX");
        this.sniffY = nbt.getDouble("sniffY");
        this.sniffZ = nbt.getDouble("sniffZ");
        this.sniffEntity = nbt.getString("sniffEntity");
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
    }

    public int getRoarTicksLeft1() {return this.roarTicksLeft1;}

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
    protected SoundEvent getHurtSound(DamageSource source) {return RegisterSounds.ENTITY_WARDEN_HURT;}
    protected SoundEvent getStepSound() {return RegisterSounds.ENTITY_WARDEN_STEP;}
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }
    protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(this.getStepSound(), 1.0F, 1.0F); }
    protected SoundEvent getAmbientSound() {
        if (this.emergeTicksLeft != -5) {
            return RegisterSounds.ENTITY_WARDEN_AMBIENT;
        } else if (UniformIntProvider.create(0, 3).get(random) >=2) {
            return RegisterSounds.ENTITY_WARDEN_AMBIENT_UNDERGROUND;
        } return null;
    }
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

    public boolean collides() {
        if (this.isRemoved()) {return false;}
        return this.emergeTicksLeft!=-5;
    }
    public boolean isPushable() { return this.emergeTicksLeft!=-5 || (this.isAlive() && !this.isSpectator() && !this.isClimbing()); }

    public WardenEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.LAVA, 16.0F);
        this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
        this.stepHeight = 1.0F;
    }

    @Override
    public int getSafeFallDistance() { return 16; }

    private float getAttackDamage() { return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE); }
    public static DefaultAttributeContainer.Builder createWardenAttributes() {return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 31.0D);}
    protected boolean burnsInDaylight() { return !this.hasDug || this.emergeTicksLeft==-5; }
    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.handleStatus((byte) 5);
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

    public boolean isPushedByFluids() { return false; }

    /** VISUALS */
    public void CreateVibration(World world, WardenEntity warden, BlockPos blockPos2) {
        WardenPositionSource wardenPositionSource = new WardenPositionSource(this.getId());
        this.delay = this.distance = (int)(Math.floor(Math.sqrt(warden.getBlockPos().getSquaredDistance(blockPos2, false))) * this.vibrationDelayAnger());
        ((ServerWorld)world).sendVibrationPacket(new Vibration(blockPos2, wardenPositionSource, this.delay));
    }
    public void CreateFloorVibration(World world, WardenEntity warden, BlockPos blockPos2) {
        BlockPositionSource blockSource = new BlockPositionSource(this.getBlockPos().down());
        this.delay = this.distance = (int)(Math.floor(Math.sqrt(warden.getBlockPos().getSquaredDistance(blockPos2, false))) * 2);
        ((ServerWorld)world).sendVibrationPacket(new Vibration(blockPos2, blockSource, this.delay));
    }
    public void digParticles(World world, BlockPos pos, int ticks) {
        if (world instanceof ServerWorld) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);
            buf.writeInt(ticks);
            for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world, pos, 32)) {
                ServerPlayNetworking.send(player, RegisterAccurateSculk.WARDEN_DIG_PARTICLES, buf);
            }
        }
    }

    @Override
    protected void applyDamage(DamageSource source, float amount) {
        super.applyDamage(source, amount);
        if (source.getAttacker() instanceof LivingEntity entity) {
            if (entity instanceof PlayerEntity player) {
                if (!player.getAbilities().creativeMode) {
                    this.addSuspicion(player, 23);
                }
            } else {
                this.addSuspicion(entity, 23);
            }
        }
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
            digParticles(this.world, this.getBlockPos(), this.emergeTicksLeft);
            this.setInvulnerable(true);
            this.setVelocity(0, 0, 0);
            this.emergeTicksLeft--;
        }
        if (this.emergeTicksLeft == 0 && !this.hasEmerged) { //Stop Emerging
            this.setInvulnerable(false);
            this.world.sendEntityStatus(this, (byte)12);
            this.hasEmerged = true;
            this.sniffCooldown = UniformIntProvider.create(5,110).get(random);
            this.emergeTicksLeft = -1;
        }
        if (world.getTime()==this.leaveTime) { this.ableToDig=true; }
        if (this.digAttemptCooldown>0) { --this.digAttemptCooldown; }
        if (this.ableToDig && this.digAttemptCooldown == 0 && this.emergeTicksLeft == -1) { //Start Digging
            if (!SculkTags.WARDEN_UNSPAWNABLE.contains(world.getBlockState(this.getBlockPos().down()).getBlock()) && !world.getBlockState(this.getBlockPos().down()).isAir()) {
                this.world.sendEntityStatus(this, (byte) 11);
                this.handleStatus((byte) 6);
                this.hasDug=true;
                this.susList.clear();
                this.entityList.clear();
            } else this.digAttemptCooldown = 240;
        }
        if (this.emergeTicksLeft > 0 && this.hasEmerged) { //Tick Down While Digging
            digParticles(this.world, this.getBlockPos(), this.emergeTicksLeft);
            this.setInvulnerable(true);
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
    public void sendDarkness(int dist, BlockPos blockPos, World world) {
        if (world instanceof ServerWorld) {
            if (world.getGameRules().getBoolean(WildMod.DARKNESS_ENABLED)) {
                Box box = (new Box(blockPos.add(-50, -50, -50), blockPos.add(50, 50, 50)));
                List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
                Iterator<PlayerEntity> var11 = list.iterator();
                PlayerEntity playerEntity;
                while (var11.hasNext()) {
                    playerEntity = var11.next();
                    if (!playerEntity.getAbilities().creativeMode && playerEntity.getBlockPos().isWithinDistance(blockPos, (dist + 1))) {
                        playerEntity.addStatusEffect(new StatusEffectInstance(RegisterStatusEffects.DARKNESS, 300, 3, true, false, false));
                    }
                }
            }
        }
    }
    public void tickSniff() {
        if (this.sniffTicksLeft > 0) { --this.sniffTicksLeft; }
        if (this.sniffCooldown > 0) { --this.sniffCooldown; }
        if (this.sniffTicksLeft == 0) {
            this.sniffTicksLeft = -1;
            this.world.sendEntityStatus(this, (byte)15);
            if (this.getSniffEntity() != null) {
                LivingEntity sniffEntity = this.getSniffEntity();
                this.addSuspicion(sniffEntity, 7);
                this.lasteventpos= new BlockPos(this.sniffX, this.sniffY, this.sniffZ);
                this.lasteventworld=this.getWorld();
                this.lastevententity=sniffEntity;
            }
        }
    }
    public void tickStuck() {
        if (this.stuckPos != null && this.getBlockPos().getSquaredDistance(this.stuckPos) < 2 && this.hasEmerged && this.hasDetected && !(this.sniffTicksLeft > 0)) {
            this.timeStuck++;
        } else {
            this.timeStuck = 0;
            this.stuckPos = this.getBlockPos();
        }
        if (this.timeStuck >= 90 && this.hasEmerged && this.world.getTime() - this.vibrationTimer < 120 && this.world.getTime() - this.timeSinceLastRecalculation > 49 && !(this.sniffTicksLeft > 0)) {
            this.getNavigation().recalculatePath();
            this.timeSinceLastRecalculation = this.world.getTime();
        }
    }
    public void tickBurn() {
    if (this.isAlive() && !this.hasDug && this.emergeTicksLeft!=-5) {
        if (world.getGameRules().getBoolean(WildMod.WARDEN_BURNS)) {
            boolean bl = this.burnsInDaylight() && this.isAffectedByDaylight();
            if (bl) { ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageable()) {itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
                        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                            this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    } bl = false;
                }
                if (bl) { this.setOnFireFor(8); }
            }
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
        this.world.playSound(null, this.getBlockPos().up(2), RegisterSounds.ENTITY_WARDEN_VIBRATION, SoundCategory.HOSTILE, 0.5F, world.random.nextFloat() * 0.2F + 0.8F);
        this.ableToDig=false;
        this.hasDug=false;
        if (this.emergeTicksLeft != -5) {
            LivingEntity eventEntity = this.getVibrationEntity();
            this.lasteventpos = new BlockPos(this.vibX, this.vibY, this.vibZ);
            int suspicion = this.queuedSuspicion;
            if (eventEntity != null) {
                this.lastevententity = eventEntity;
                addSuspicion(eventEntity, suspicion);
                if (this.world.getTime() - reactionSoundTimer > 40) {
                    this.reactionSoundTimer = this.world.getTime();
                    if (getSuspicion(eventEntity) < 12) {
                        this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_SLIGHTLY_ANGRY, SoundCategory.HOSTILE, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
                    } else if (getSuspicion(eventEntity) < 25) {
                        this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_SLIGHTLY_ANGRY, SoundCategory.HOSTILE, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
                    } else if (this.trueOverallAnger() < 45) {
                        this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_SLIGHTLY_ANGRY, SoundCategory.HOSTILE, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
                    } else if (this.trueOverallAnger() >= 45) {
                        this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_ANGRY, SoundCategory.HOSTILE, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
                    }
                }
            } else {
                this.timeSinceNonEntity = this.world.getTime();
                this.nonEntityAnger = this.nonEntityAnger + 3;
                if (this.world.getTime() - reactionSoundTimer > 40) {
                    this.reactionSoundTimer = this.world.getTime();
                    if (this.trueOverallAnger() < 25) {
                        this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_SLIGHTLY_ANGRY, SoundCategory.HOSTILE, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
                    } else {
                        this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_ANGRY, SoundCategory.HOSTILE, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
                    }
                }
            }
        } else {
            this.world.playSound(null, this.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_SLIGHTLY_ANGRY, SoundCategory.HOSTILE, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
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
    public double sniffX;
    public double sniffY;
    public double sniffZ;
    public int vibX;
    public int vibY;
    public int vibZ;
    //Lists & Entity Tracking
    public IntArrayList entityList = new IntArrayList();
    public IntArrayList susList = new IntArrayList();
    public String trackingEntity = "null";
    public String sniffEntity = "null";
    public String vibrationEntity="null";
    public int queuedSuspicion;
    //Anger & Heartbeat
    public int heartbeatTime = 40;
    public int nonEntityAnger;
    public long nextHeartBeat;
    public long lastHeartBeat;
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
    public int attackCooldown;
    public long reactionSoundTimer;
    public int attackTicksLeft1;
    public int roarTicksLeft1;
    public int sniffTicksLeft;
    public int ticksToDarkness;
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
    public boolean shouldRender=true; //Status 17 (False) Status 18 (True)

    //ANIMATION
    public boolean canEmergeAnim; //Status 9
    public boolean stopEmergeAnim; // Status 12
    public float emergeAnimStartTime=-200;

    public boolean canSniffAnim; //Status 10
    public boolean stopSniffAnim; //Status 15
    public float sniffAnimStartTime=-200;

    public boolean canDigAnim; //Status 11
    public boolean stopDigAnim; //Status 13
    public float digAnimStartTime=-200;

    public boolean canRoarAnim; //Status 3
    public boolean stopRoarAnim; //Status 14
    public float roarAnimStartTime=-200;

    public boolean canAttackAnim; //Status 4
    public boolean stopAttackAnim; //Status 16
    public float attackAnimStartTime=-200;

    public boolean canTendrilAnim; //Status 7
    public float tendrilAnimStartTime=-200;

    public int headRoll=0; //HAS VALUES OF 0,1,2,3 AND 4 - DERIVATIVE OF OVERALL ANGER
}
