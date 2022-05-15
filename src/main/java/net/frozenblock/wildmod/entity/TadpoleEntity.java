package net.frozenblock.wildmod.entity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.frozenblock.wildmod.entity.ai.TadpoleBrain;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.frozenblock.wildmod.registry.RegisterItems;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TadpoleEntity extends FishEntity {
    @VisibleForTesting
    public static int MAX_TADPOLE_AGE = Math.abs(-24000);
    private int tadpoleAge;
    protected static final ImmutableList<SensorType<? extends Sensor<? super TadpoleEntity>>> SENSORS = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY
    );
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.VISIBLE_MOBS,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.NEAREST_VISIBLE_ADULT
    );

    public TadpoleEntity(EntityType<? extends FishEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new YawAdjustingLookControl(this, 10);
    }

    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    protected Brain.Profile<TadpoleEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return TadpoleBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<TadpoleEntity> getBrain() {
        return (Brain<TadpoleEntity>) super.getBrain();
    }

    protected SoundEvent getFlopSound() {
        return RegisterSounds.ENTITY_TADPOLE_FLOP;
    }

    protected void mobTick() {
        this.world.getProfiler().push("tadpoleBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("tadpoleActivityUpdate");
        TadpoleBrain.updateActivities(this);
        this.world.getProfiler().pop();
        super.mobTick();
    }

    public static DefaultAttributeContainer.Builder createTadpoleAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0).add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0);
    }

    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient) {
            this.setTadpoleAge(this.tadpoleAge + 1);
        }

    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Age", this.tadpoleAge);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setTadpoleAge(nbt.getInt("Age"));
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return RegisterSounds.ENTITY_TADPOLE_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return RegisterSounds.ENTITY_TADPOLE_DEATH;
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.isSlimeBall(itemStack)) {
            this.eatSlimeBall(player, itemStack);
            return ActionResult.success(this.world.isClient);
        } else {
            return (ActionResult)Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
        }
    }

    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public boolean isFromBucket() {
        return true;
    }

    public void setFromBucket(boolean fromBucket) {
    }

    public void copyDataToStack(ItemStack stack) {
        Bucketable.copyDataToStack(this, stack);
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putInt("Age", this.getTadpoleAge());
    }

    public void copyDataFromNbt(NbtCompound nbt) {
        Bucketable.copyDataFromNbt(this, nbt);
        if (nbt.contains("Age")) {
            this.setTadpoleAge(nbt.getInt("Age"));
        }

    }

    public ItemStack getBucketItem() {
        return new ItemStack(RegisterItems.TADPOLE_BUCKET);
    }

    public SoundEvent getBucketFillSound() {
        return RegisterSounds.ITEM_BUCKET_FILL_TADPOLE;
    }

    private boolean isSlimeBall(ItemStack stack) {
        return FrogEntity.SLIME_BALL.test(stack);
    }

    private void eatSlimeBall(PlayerEntity player, ItemStack stack) {
        this.decrementItem(player, stack);
        this.increaseAge(toGrowUpAge(this.getTicksUntilGrowth()));
        this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
    }

    private void decrementItem(PlayerEntity player, ItemStack stack) {
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }

    }

    public static int toGrowUpAge(int breedingAge) {
        return (int)((float)(breedingAge / 20) * 0.1F);
    }

    private int getTadpoleAge() {
        return this.tadpoleAge;
    }

    private void increaseAge(int seconds) {
        this.setTadpoleAge(this.tadpoleAge + seconds * 20);
    }

    private void setTadpoleAge(int tadpoleAge) {
        this.tadpoleAge = tadpoleAge;
        if (this.tadpoleAge >= MAX_TADPOLE_AGE) {
            this.growUp();
        }

    }

    private void growUp() {
        World frogWorld = this.world;
        if (frogWorld instanceof ServerWorld serverWorld) {
            FrogEntity frogEntity = RegisterEntities.FROG.create(this.world);
            assert frogEntity != null;
            frogEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
            frogEntity.initialize(serverWorld, this.world.getLocalDifficulty(frogEntity.getBlockPos()), SpawnReason.CONVERSION, null, null);
            frogEntity.setAiDisabled(this.isAiDisabled());
            if (this.hasCustomName()) {
                frogEntity.setCustomName(this.getCustomName());
                frogEntity.setCustomNameVisible(this.isCustomNameVisible());
            }

            frogEntity.setPersistent();
            this.playSound(RegisterSounds.ENTITY_TADPOLE_GROW_UP, 0.15F, 1.0F);
            serverWorld.spawnEntityAndPassengers(frogEntity);
            this.discard();
        }

    }

    private int getTicksUntilGrowth() {
        return Math.max(0, MAX_TADPOLE_AGE - this.tadpoleAge);
    }
}

