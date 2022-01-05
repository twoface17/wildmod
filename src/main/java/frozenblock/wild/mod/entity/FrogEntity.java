package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.liukrastapi.FrogGoal;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class FrogEntity extends PathAwareEntity {

    private static final TrackedData<Integer> VARIANT;
    public static final String VARIANT_KEY = "Variant";
    public static final int SWAMP = 0;
    public static final int COLD = 1;
    public static final int TROPICAL = 2;
    private static final double speed = 0.3D;
    private int tongue;

    public long eatTimer = 0;

    public FrogEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createFrogAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed);
    }

    public static boolean canColdSpawn(World world, BlockPos pos) {
        return world.getBiome(pos).isCold(pos) || world.getBiome(pos).getCategory().equals(Biome.Category.ICY);
    }
    public static boolean canTropicalSpawn(World world, BlockPos pos) {
        return world.getBiome(pos).getCategory().equals(Biome.Category.JUNGLE) || world.getBiome(pos).getCategory().equals(Biome.Category.DESERT);
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public int getTongue() {
        return this.tongue;
    }

    public void tickMovement() {
        if(this.tongue > 0) {
            --this.tongue;
            System.out.println(this.tongue);
        }
        super.tickMovement();
    }

    @Override
    public boolean isSubmergedInWater() {
        if(this.getEntityWorld().getBlockState(new BlockPos(this.getPos().x, this.getPos().y - 1/16f, this.getPos().z)) == Blocks.WATER.getDefaultState()) {
            return true;
        } else {
            return this.submergedInWater && this.isTouchingWater();
        }
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.DISPENSER) {
            if(canColdSpawn(this.getEntityWorld(), this.getBlockPos())) {
                this.setVariant(Variant.COLD);
            } else if(canTropicalSpawn(this.getEntityWorld(), this.getBlockPos())) {
                this.setVariant(Variant.TROPICAL);
            }
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }



    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt(VARIANT_KEY, this.getVariant().getId());
        nbt.putLong("eatTimer", this.eatTimer);
    }

    public FrogEntity.Variant getVariant() {
        return FrogEntity.Variant.VARIANTS[this.dataTracker.get(VARIANT)];
    }

    public void setVariant(FrogEntity.Variant variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }


    static {
        VARIANT = DataTracker.registerData(FrogEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(FrogEntity.Variant.VARIANTS[nbt.getInt(VARIANT_KEY)]);
        this.eatTimer = nbt.getLong("eatTimer");
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, speed));
        this.goalSelector.add(3, new FrogGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
    }


    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public enum Variant {
        SWAMP(FrogEntity.SWAMP, "swamp"),
        COLD(FrogEntity.COLD, "cold"),
        TROPICAL(FrogEntity.TROPICAL, "tropical");
        public static final FrogEntity.Variant[] VARIANTS = Arrays.stream(values()).sorted(Comparator.comparingInt(Variant::getId)).toArray(Variant[]::new);
        private final int id;
        private final String name;

        Variant(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }
    }

    public void handleStatus(byte status) {
        if (status == 4) {
            this.tongue = 10;
        } else {
            super.handleStatus(status);
        }
    }


}
