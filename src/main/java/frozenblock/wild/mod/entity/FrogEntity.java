package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.blocks.FrogSpawnBlock;
import frozenblock.wild.mod.liukrastapi.FrogGoal;
import frozenblock.wild.mod.liukrastapi.FrogMateGoal;
import frozenblock.wild.mod.liukrastapi.FrogWanderInWaterGoal;
import frozenblock.wild.mod.liukrastapi.LayFrogSpawnGoal;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;


public class FrogEntity extends AnimalEntity {

    static final int BREEDING_COOLDOWN = 6000;
    private static final TrackedData<Integer> VARIANT;
    public static final String VARIANT_KEY = "Variant";
    public static final int TEMPERATE = 0;
    public static final int COLD = 1;
    public static final int WARM = 2;
    private static final double speed = 0.4D;
    public static final TrackedData<Boolean> PREGNANT;
    public static final TrackedData<BlockPos> TRAVEL_POS;
    public static final TrackedData<Boolean> ACTIVELY_TRAVELLING;
    public static final Ingredient SLIME_BALL;
    private int loveTicks;

    private int tongue;

    public long eatTimer = 0;
    public int targetRemoveTimer;
    public int targetID;

    //ANIMATION
    public boolean canEatAnim; //Status 4
    public float eatAnimStartTime=-200;

    public FrogEntity(EntityType<? extends FrogEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.moveControl = new FrogMoveControl(this);
    }

    static {
        PREGNANT = DataTracker.registerData(FrogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        TRAVEL_POS = DataTracker.registerData(FrogEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
        ACTIVELY_TRAVELLING = DataTracker.registerData(FrogEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        SLIME_BALL = Ingredient.ofItems(Items.SLIME_BALL.asItem());
    }

    public static DefaultAttributeContainer.Builder createFrogAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed);
    }

    public static boolean canColdSpawn(World world, BlockPos pos) {
        RegistryEntry<Biome> reBiome = world.getBiome(pos);
        Biome.Category category = Biome.getCategory(reBiome);
        Optional<Biome> biomeOpt = world.getBiome(pos).getKeyOrValue().right();
        if (biomeOpt.isPresent()) {
            Biome biome = biomeOpt.get();
            return biome.isCold(pos) || category.equals(Biome.Category.ICY) || category.equals(Biome.Category.THEEND);
        }
        return false;
    }
    public static boolean canTemperateSpawn(World world, BlockPos pos) {
        return world.getBiome(pos).getKeyOrValue().right().equals(Biome.Category.JUNGLE) || world.getBiome(pos).getKeyOrValue().right().equals(Biome.Category.DESERT) || world.getBiome(pos).getKeyOrValue().right().equals(Biome.Category.NETHER);
    }

    void setTravelPos(BlockPos pos) {
        this.dataTracker.set(TRAVEL_POS, pos);
    }

    BlockPos getTravelPos() {
        return (BlockPos) this.dataTracker.get(TRAVEL_POS);
    }

    public boolean pregnant() {return (Boolean) this.dataTracker.get(PREGNANT);}

    public void becomePregnant(boolean pregnant) {
        this.dataTracker.set(PREGNANT, pregnant);
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public int getTongue() {
        return this.tongue;
    }

    public void tickMovement() {
        super.tickMovement();
        if (this.tongue > 0) {
            --this.tongue;
        }
        if (this.targetRemoveTimer > 0) {
            --this.targetRemoveTimer;
        }
        if (this.getBreedingAge() != 0) {
            this.loveTicks = 0;
        }
        if (this.loveTicks > 0) {
            --this.loveTicks;
            if (this.loveTicks % 10 == 0) {
                double d = this.random.nextGaussian() * 0.02D;
                double e = this.random.nextGaussian() * 0.02D;
                double f = this.random.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.HEART, this.getParticleX(1.0D), this.getRandomBodyY() + 0.5D, this.getParticleZ(1.0D), d, e, f);
            }
        }
        if (this.pregnant() && canPlace(this.world, this.getBlockPos())) {
            World world = this.world;
            world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
            world.setBlockState(this.getBlockPos().up(), RegisterBlocks.FROG_SPAWN.getDefaultState(), 3);
            world.syncWorldEvent(2005, this.getBlockPos().up(), 0);
            world.createAndScheduleBlockTick(this.getBlockPos(), world.getBlockState(this.getBlockPos()).getBlock(), UniformIntProvider.create(400, 1800).get(world.getRandom()));
            this.becomePregnant(false);
            this.setLoveTicks(600);
        }
        if (this.targetRemoveTimer == 0 && this.getTarget() != null) {
            this.dropItems(this.world, this.getTarget());
            this.getTarget().remove(RemovalReason.KILLED);
        }
    }

    @Nullable
    public LivingEntity getTarget() {
        if (world.getEntityById(this.targetID)!=null) {
            if (world.getEntityById(this.targetID) instanceof LivingEntity) {
                return (LivingEntity) world.getEntityById(this.targetID);
            }
        }
        return null;
    }

    @Override
    public boolean isSubmergedInWater() {
        if (this.getEntityWorld().getBlockState(new BlockPos(this.getPos().x, this.getPos().y - 1 / 16f, this.getPos().z)) == Blocks.WATER.getDefaultState()) {
            return true;
        } else {
            return this.submergedInWater && this.isTouchingWater();
        }
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.DISPENSER) {
            if (canColdSpawn(this.getEntityWorld(), this.getBlockPos())) {
                this.setVariant(Variant.COLD);
            } else if (canTemperateSpawn(this.getEntityWorld(), this.getBlockPos())) {
                this.setVariant(Variant.WARM);
            }
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }


    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(PREGNANT, false);
        this.dataTracker.startTracking(ACTIVELY_TRAVELLING, false);
        this.dataTracker.startTracking(TRAVEL_POS, this.getBlockPos());
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt(VARIANT_KEY, this.getVariant().getId());
        nbt.putLong("eatTimer", this.eatTimer);
        nbt.putBoolean("IsPregnant", this.pregnant());
        nbt.putInt("TravelPosX", this.getTravelPos().getX());
        nbt.putInt("TravelPosY", this.getTravelPos().getY());
        nbt.putInt("TravelPosZ", this.getTravelPos().getZ());
        nbt.putInt("targetID", this.targetID);
        nbt.putInt("targetRemoveTimer", this.targetRemoveTimer);
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
        this.becomePregnant(nbt.getBoolean("IsPregnant"));
        int l = nbt.getInt("TravelPosX");
        int m = nbt.getInt("TravelPosY");
        int n = nbt.getInt("TravelPosZ");
        this.targetID = nbt.getInt("targetID");
        this.targetRemoveTimer = nbt.getInt("targetRemoveTimer");
        this.setTravelPos(new BlockPos(l, m, n));
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(5, new LayFrogSpawnGoal(this, 1.0D));
        this.goalSelector.add(5, new FrogMateGoal(this, 1.0D));
        this.goalSelector.add(2, new WanderAroundGoal(this, speed));
        this.goalSelector.add(3, new FrogWanderInWaterGoal(this, 1.0D));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, speed));
        this.goalSelector.add(2, new TemptGoal(this, 1.1D, SLIME_BALL, false));
        this.goalSelector.add(3, new FrogGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.SLIME_BALL.asItem());
    }

    protected SoundEvent getAmbientSound() {
        return RegisterSounds.ENTITY_FROG_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return RegisterSounds.ENTITY_FROG_HURT;
    }

    protected SoundEvent getDeathSound() {
        return RegisterSounds.ENTITY_FROG_DEATH;
    }

    protected SoundEvent getStepSound() {
        return RegisterSounds.ENTITY_FROG_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.5F, 1.0F);
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public void setLoveTicks(int loveTicks) {
        this.loveTicks = loveTicks;
    }

    public int getLoveTicks() {
        return this.loveTicks;
    }

    public enum Variant {
        TEMPERATE(FrogEntity.TEMPERATE, "temperate"),
        COLD(FrogEntity.COLD, "cold"),
        WARM(FrogEntity.WARM, "warm");
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
            this.canEatAnim=true;
            this.tongue = 10;
        } else if (status == 18) {
            for (int i = 0; i < 7; ++i) {
                double d = this.random.nextGaussian() * 0.02D;
                double e = this.random.nextGaussian() * 0.02D;
                double f = this.random.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.HEART, this.getParticleX(1.0D), this.getRandomBodyY() + 0.5D, this.getParticleZ(1.0D), d, e, f);
            }
        } else {
            super.handleStatus(status);
        }
    }

    protected boolean canPlace(World world, BlockPos pos) {
        return world.isAir(pos.up()) && FrogSpawnBlock.isWater(world, pos);
    }

    public void dropItems(World world, LivingEntity entity) {
        if (entity.getType() == EntityType.MAGMA_CUBE) {
            if (this.getVariant() == FrogEntity.Variant.TEMPERATE) {
                this.dropItem(RegisterBlocks.OCHRE_FROGLIGHT.asItem(), 0);
            } else if (this.getVariant() == FrogEntity.Variant.COLD) {
                this.dropItem(RegisterBlocks.VERDANT_FROGLIGHT.asItem(), 0);
            } else if (this.getVariant() == FrogEntity.Variant.WARM) {
                this.dropItem(RegisterBlocks.PEARLESCENT_FROGLIGHT.asItem(), 0);
            }
        }
        if (entity.getType() == EntityType.SLIME) {
            for (int i = 0; i < UniformIntProvider.create(0, 2).get(world.getRandom()); i++) {
                this.dropItem(Items.SLIME_BALL);
            }
        }
    }

     static class FrogMoveControl extends MoveControl {
        private final FrogEntity frog;

        FrogMoveControl(FrogEntity frog) {
            super(frog);
            this.frog = frog;
        }

        private void updateVelocity() {
            if (this.frog.isTouchingWater()) {
                this.frog.setVelocity(this.frog.getVelocity().add(0.0D, 0.005D, 0.0D));
                if (!this.frog.getBlockPos().isWithinDistance(this.frog.getPos(), 16.0D)) {
                    this.frog.setMovementSpeed(Math.max(this.frog.getMovementSpeed() / 2.0F, 0.08F));
                }
            } else if (this.frog.isOnGround()) {
                this.frog.setMovementSpeed(Math.max(this.frog.getMovementSpeed() / 2.0F, 0.08F));
            }

        }

        public void tick() {
            this.updateVelocity();
            if (this.state == State.MOVE_TO && !this.frog.getNavigation().isIdle()) {
                double d = this.targetX - this.frog.getX();
                double e = this.targetY - this.frog.getY();
                double f = this.targetZ - this.frog.getZ();
                double g = Math.sqrt(d * d + e * e + f * f);
                e /= g;
                float h = (float) (MathHelper.atan2(f, d) * 57.2957763671875D) - 90.0F;
                this.frog.setYaw(this.wrapDegrees(this.frog.getYaw(), h, 90.0F));
                this.frog.bodyYaw = this.frog.getYaw();
                float i = (float) (this.speed * this.frog.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                this.frog.setMovementSpeed(MathHelper.lerp(0.125F, this.frog.getMovementSpeed(), i));
                this.frog.setVelocity(this.frog.getVelocity().add(0.0D, (double) this.frog.getMovementSpeed() * e * 0.2D, 0.0D));
            } else {
                this.frog.setMovementSpeed(0.0F);
            }
        }
    }
}
