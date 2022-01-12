package frozenblock.wild.mod.entity;


import frozenblock.wild.mod.liukrastapi.WardenGoal;
import frozenblock.wild.mod.registry.RegisterAccurateSculk;
import frozenblock.wild.mod.registry.RegisterSounds;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.Vibration;
import net.minecraft.world.World;
import net.minecraft.world.event.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WardenEntity extends HostileEntity {

    private int attackTicksLeft1;

    private int roarTicksLeft1;

    private double roarAnimationProgress;

    private static final double speed = 0.4D;

    public BlockPos lasteventpos;
    public World lasteventworld;
    public LivingEntity lastevententity;
    public IntArrayList entityList = new IntArrayList();
    public IntArrayList susList = new IntArrayList();
    public String trackingEntity = "null";

    public boolean hasDetected=false;
    public int emergeTicksLeft;
    public boolean hasEmerged;
    public long vibrationTimer = 0;
    public long leaveTime;
    protected int delay = 0;
    protected int distance;

    public int hearbeatTime = 60;
    public long nextHeartBeat;


    public WardenEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createWardenAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 31.0D);
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.handleStatus((byte) 5);
        this.leaveTime=this.world.getTime()+1200;
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }


    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new WardenGoal(this, speed));
    }
    @Override
    public void emitGameEvent(GameEvent event, @Nullable Entity entity, BlockPos pos) {}
    @Override
    public void emitGameEvent(GameEvent event, @Nullable Entity entity) {}
    @Override
    public void emitGameEvent(GameEvent event, BlockPos pos) {}
    @Override
    public void emitGameEvent(GameEvent event) {}


    public void tickMovement() {
        if(this.attackTicksLeft1 > 0) {
            --this.attackTicksLeft1;
        }
        if(this.roarTicksLeft1 > 0) {
            --this.roarTicksLeft1;
        }
        if(this.emergeTicksLeft > 0 && !this.hasEmerged) {
            digParticles(this.world, this.getBlockPos(), this.emergeTicksLeft);
            this.setInvulnerable(true);
            this.setVelocity(0,0,0);
            this.emergeTicksLeft--;
        }
        if (this.emergeTicksLeft==0 && !this.hasEmerged) {
            this.setInvulnerable(false);
            this.hasEmerged=true;
            this.emergeTicksLeft=-1;
        }
        if(this.emergeTicksLeft > 0 && this.hasEmerged) {
            digParticles(this.world, this.getBlockPos(), this.emergeTicksLeft);
            this.setInvulnerable(true);
            this.setVelocity(0,0,0);
            --this.emergeTicksLeft;
        }
        if (this.emergeTicksLeft==0 && this.hasEmerged) {
            this.remove(RemovalReason.DISCARDED);
        }
        if(world.getTime()==this.leaveTime) {
            this.handleStatus((byte) 6);
        }
        //Heartbeat
        this.hearbeatTime = (int) (60 - ((MathHelper.clamp(this.overallAnger(),0,15)*3.3)));
        if (this.world.getTime()>=this.nextHeartBeat) {
            this.world.playSound(null, this.getBlockPos().up(), RegisterSounds.ENTITY_WARDEN_HEARTBEAT, SoundCategory.HOSTILE, 1F, 1F);
            this.nextHeartBeat=this.world.getTime()+hearbeatTime;
        }
        super.tickMovement();
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    public void setRoarAnimationProgress(double a) {
        this.roarAnimationProgress = a;
    }
    public void roar() {
        this.attackTicksLeft1 = 10;
        this.world.sendEntityStatus(this, (byte)3);
    }

    public boolean tryAttack(Entity target) {
        float f = this.getAttackDamage();
        float g = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
        boolean bl = target.damage(DamageSource.mob(this), g);
        if (bl) {
            this.attackTicksLeft1 = 10;
            this.world.sendEntityStatus(this, (byte)4);
            target.setVelocity(target.getVelocity().add(0.0D, 0.4000000059604645D, 0.0D));
            this.applyDamageEffects(this, target);
            world.playSound(null, this.getBlockPos(), RegisterSounds.ENTITY_WARDEN_SLIGHTLY_ANGRY, SoundCategory.HOSTILE, 1.0F,1.0F);
        }
        return bl;
    }

    public int getAttackTicksLeft1() {
        return this.attackTicksLeft1;
    }

    public double getRoarAnimationProgress() {
        return this.roarAnimationProgress;
    }

    public int getRoarTicksLeft1() {
        return this.roarTicksLeft1;
    }

    public void handleStatus(byte status) {
        if (status == 4) {
            this.attackTicksLeft1 = 10;
            world.playSound(null, this.getBlockPos(), RegisterSounds.ENTITY_WARDEN_AMBIENT, SoundCategory.HOSTILE, 1.0F,1.0F);
        } else if(status == 3) {
            this.roarTicksLeft1 = 10;
        } else if(status == 5) {
            //Emerging
            this.emergeTicksLeft=120;
            this.hasEmerged=false;
            world.playSound(null, this.getBlockPos(), RegisterSounds.ENTITY_WARDEN_EMERGE, SoundCategory.HOSTILE, 1F, 1F);
        } else if(status == 6) {
            //Digging Back
            this.emergeTicksLeft=60;
            this.hasEmerged=true;
            world.playSound(null, this.getBlockPos(), RegisterSounds.ENTITY_WARDEN_DIG, SoundCategory.HOSTILE, 1F, 1F);
        }  else {
            super.handleStatus(status);
        }

    }

    public void digParticles(World world, BlockPos pos, int ticks) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeInt(ticks);
        for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world, pos, 32)) {
            ServerPlayNetworking.send(player, RegisterAccurateSculk.WARDEN_DIG_PARTICLES, buf);
        }
    }

    protected SoundEvent getAmbientSound(){return RegisterSounds.ENTITY_WARDEN_AMBIENT;}

    protected SoundEvent getHurtSound(DamageSource source) {return RegisterSounds.ENTITY_WARDEN_HURT;}

    protected SoundEvent getStepSound() {return RegisterSounds.ENTITY_WARDEN_STEP;}

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.5F, 1.0F);
    }

    public void listen(BlockPos eventPos, World eventWorld, LivingEntity eventEntity, int suspicion) {
            if (!(this.emergeTicksLeft > 0) && this.lasteventpos == eventPos && this.lasteventworld == eventWorld && this.lastevententity == eventEntity && this.world.getTime() - this.vibrationTimer >= 23) {
                this.hasDetected = true;
                this.vibrationTimer = this.world.getTime();
                this.leaveTime = this.world.getTime() + 1200;
                this.world.playSound(null, this.getBlockPos().up(2), RegisterSounds.ENTITY_WARDEN_VIBRATION, SoundCategory.HOSTILE, 0.5F, world.random.nextFloat() * 0.2F + 0.8F);
                BlockPos WardenHead = this.getBlockPos().up((3));
                //Find a way to use EntityPositionSource AND have the vibration move to Warden's head (Might be impossible atm)
                BlockPositionSource wardenPositionSource = new BlockPositionSource(Optional.of(WardenHead)) {
                    @Override
                    public Optional<BlockPos> getPos(World world) {
                        return Optional.of(WardenHead);
                    }
                    @Override
                    public PositionSourceType<?> getType() {
                        return PositionSourceType.BLOCK;
                    }
                };
                CreateVibration(this.world, lasteventpos, wardenPositionSource, WardenHead);
                if (eventEntity != null) {
                    addSuspicion(eventEntity, suspicion);
                }
            }
            this.lasteventpos = eventPos;
            this.lasteventworld = eventWorld;
            this.lastevententity = eventEntity;
    }

    public void CreateVibration(World world, BlockPos blockPos, PositionSource positionSource, BlockPos blockPos2) {
        this.delay = this.distance = (int)Math.floor(Math.sqrt(blockPos.getSquaredDistance(blockPos2, false))) * 2;
        ((ServerWorld)world).sendVibrationPacket(new Vibration(blockPos, positionSource, this.delay));
    }

    public void addSuspicion(LivingEntity entity, int suspicion) {
        if (!this.entityList.isEmpty()) {
            if (this.entityList.contains(entity.getUuid().hashCode())) {
                int slot = this.entityList.indexOf(entity.getUuid().hashCode());
                this.susList.set(slot, this.susList.get(slot) + suspicion);
                if (this.susList.get(slot)>=15 && this.getTrackingEntity()==null) {
                    this.trackingEntity=entity.getUuidAsString();
                    this.world.playSound(null, this.getBlockPos().up(), RegisterSounds.ENTITY_WARDEN_ROAR, SoundCategory.HOSTILE, 1F, 1F);
                    this.roar();
                }
            } else {
                this.entityList.add(entity.getUuid().hashCode());
                this.susList.add(suspicion);
            }
        } else {
            this.entityList.add(entity.getUuid().hashCode());
            this.susList.add(suspicion);
        }
    }

    public int getSuspicion(LivingEntity entity) {
        if (!this.entityList.isEmpty()) {
            if (this.entityList.contains(entity.getUuid().hashCode())) {
                int slot = this.entityList.indexOf(entity.getUuid().hashCode());
                return this.susList.get(slot);
            }
        }
        return 0;
    }

    public int getHighestSuspicionInt() {
        int highest = 0;
        if (!this.susList.isEmpty()) {
            for (int i=0; i<this.susList.size(); i++) {
                if (this.susList.getInt(i)>highest) {
                    highest=this.susList.getInt(i);
                }
            }
        }
        return highest;
    }

    public LivingEntity mostSuspiciousAround() {
        int highest = 0;
        LivingEntity most = null;
        Box box = new Box(this.getBlockPos().add(-16,-16,-16), this.getBlockPos().add(16,16,16));
        List<LivingEntity> entities = world.getNonSpectatingEntities(LivingEntity.class, box);
        if (!entities.isEmpty()) {
            for (LivingEntity target : entities) {
                if (this.getBlockPos().getSquaredDistance(target.getBlockPos())<=16) {
                    if (this.getSuspicion(target)>highest) {
                        highest = this.getSuspicion(target);
                        most = target;
                    }
                }
            }
        }
        return most;
    }
    public int mostSuspiciousAroundInt() {
        int value=0;
        if (mostSuspiciousAround()!=null) {
            value=this.getSuspicion(mostSuspiciousAround());
        }
        return value;
    }
    public LivingEntity getTrackingEntity() {
        LivingEntity most = null;
        Box box = new Box(this.getBlockPos().add(-20,-20,-20), this.getBlockPos().add(20,20,20));
        List<LivingEntity> entities = world.getNonSpectatingEntities(LivingEntity.class, box);
        if (!entities.isEmpty()) {
            for (LivingEntity target : entities) {
                if (this.getBlockPos().getSquaredDistance(target.getBlockPos())<=21) {
                    if (Objects.equals(this.trackingEntity, target.getUuidAsString())) {
                        most = target;
                    }
                }
            }
        }
        return most;
    }
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putLong("vibrationTimer", this.vibrationTimer);
        nbt.putLong("leaveTime", this.leaveTime);
        nbt.putInt("emergeTicksLeft", this.emergeTicksLeft);
        nbt.putBoolean("hasEmerged", this.hasEmerged);
        nbt.putIntArray("entityList", this.entityList);
        nbt.putIntArray("susList", this.susList);
        nbt.putString("trackingEntity", this.trackingEntity);
    }
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.vibrationTimer = nbt.getLong("vibrationTimer");
        this.leaveTime = nbt.getLong("leaveTime");
        this.emergeTicksLeft = nbt.getInt("emergeTicksLeft");
        this.hasEmerged = nbt.getBoolean("hasEmerged");
        this.entityList = IntArrayList.wrap(nbt.getIntArray("entityList"));
        this.susList = IntArrayList.wrap(nbt.getIntArray("susList"));
        this.trackingEntity = nbt.getString("trackingEntity");
    }

    public int eventSuspicionValue(GameEvent event, LivingEntity livingEntity) {
        int total=1;
        EntityType entity=livingEntity.getType();
        if (entity==EntityType.PLAYER) {
            total=total+2;
        }
        if (entity==EntityType.IRON_GOLEM) { //They're loud, you know?
            total=total+1;
        }
        if (entity==EntityType.VILLAGER) { //It's similar to a player, you know?
            total=total+1;
        }
        if (entity==EntityType.WITHER) { //It's loud, you know?
            total=total+1;
        }
        if(this.getBlockPos().getSquaredDistance(livingEntity.getBlockPos(), true)<=8) {
            total=total+1;
        }
        return total;
    }
    public int overallAnger() {
        int anger=0;
        for (int i=0; i<this.susList.size(); i++) {
            anger=anger+this.susList.getInt(i);
        }
        return MathHelper.clamp(anger,0,25);
    }
}
