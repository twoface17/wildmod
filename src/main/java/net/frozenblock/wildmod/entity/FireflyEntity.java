package net.frozenblock.wildmod.entity;

import net.frozenblock.wildmod.liukrastapi.FireflyWanderGoal;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.NoWaterTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireflyEntity extends WildPathAwareEntity implements Flutterer {
    public boolean spawnSet;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    public long lastClientFlash;

    public FireflyEntity(EntityType<? extends WildPathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathfindingPenalty(PathNodeType.COCOA, -1.0F);
        this.setPathfindingPenalty(PathNodeType.FENCE, -1.0F);
    }

    protected void initGoals() {
        this.goalSelector.add(2, new FireflyWanderGoal(this));
        this.goalSelector.add(1, new SwimGoal(this));
    }

    public static DefaultAttributeContainer.Builder createFireflyAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.08F).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.08F).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.spawnSet) {
            this.spawnX = this.getBlockX();
            this.spawnY = this.getBlockY();
            this.spawnZ = this.getBlockZ();
            this.spawnSet = true;
        }
    }

    public boolean canEquip(ItemStack stack) {
        return false;
    }

    public boolean hasWings() {
        return true;
    }

    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    public boolean disablesShield() {
        return false;
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("spawnSet", this.spawnSet);
        nbt.putInt("spawnX", this.spawnX);
        nbt.putInt("spawnY", this.spawnY);
        nbt.putInt("spawnZ", this.spawnZ);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.spawnSet = nbt.getBoolean("spawnSet");
        this.spawnX = nbt.getInt("spawnX");
        this.spawnY = nbt.getInt("spawnY");
        this.spawnZ = nbt.getInt("spawnZ");
    }

    public void startMovingTo(BlockPos pos) {
        Vec3d vec3d = Vec3d.ofBottomCenter(pos);
        int i = 0;
        BlockPos blockPos = this.getBlockPos();
        int j = (int) vec3d.y - blockPos.getY();
        if (j > 2) {
            i = 4;
        } else if (j < -2) {
            i = -4;
        }

        int k = 6;
        int l = 8;
        int m = blockPos.getManhattanDistance(pos);
        if (m < 15) {
            k = m / 2;
            l = m / 2;
        }

        Vec3d vec3d2 = NoWaterTargeting.find(this, k, l, i, vec3d, 0.3141592741012573D);
        if (vec3d2 != null) {
            this.navigation.setRangeMultiplier(0.5F);
            this.navigation.startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, 0.05D);
        }
    }

    public BlockPos getSpawnPos() {
        return new BlockPos(this.spawnX, this.spawnY, this.spawnZ);
    }

    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
            if (this.isTouchingWater()) {
                this.updateVelocity(0.01F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.800000011920929));
            } else if (this.isInLava()) {
                this.updateVelocity(0.01F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5));
            } else {
                this.updateVelocity(this.getMovementSpeed(), movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.9100000262260437));
            }
        }

        this.updateLimbs(this, false);
    }

    protected SoundEvent getAmbientSound() {
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return RegisterSounds.ENTITY_FIREFLY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return RegisterSounds.ENTITY_FIREFLY_HURT;
    }

    @Override
    public boolean isInAir() {
        return !this.onGround;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected void pushAway(Entity entity) {
    }

    protected void tickCramming() {
    }

}
