package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.liukrastapi.Sphere;
import net.minecraft.block.BigDripleafBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.*;


public class FrogEntity extends PathAwareEntity {

    private static final TrackedData<Integer> VARIANT;
    public static final String VARIANT_KEY = "Variant";
    public static final int SWAMP = 0;
    public static final int COLD = 1;
    public static final int TROPICAL = 2;
    private static final double speed = 0.3D;


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


    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt(VARIANT_KEY, this.getVariant().getId());

    }

    public FrogEntity.Variant getVariant() {
        return FrogEntity.Variant.VARIANTS[(Integer)this.dataTracker.get(VARIANT)];
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
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, speed));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
    }

    public void mobTick() {
        if(this.isOnGround()) {
            if (Math.random() < 0.025) {

                double jumpamount = Math.random() / 2;

                if(jumpamount < 0.25) {
                    jumpamount = 0.25;
                }

                if(Math.random() < 0.2) {

                    // RANDOM JUMP
                    double angle = Math.random() * 360;
                    double radius = Math.random() * 0.3;
                    this.setYaw((float) angle);
                    this.updateVelocity(2F, new Vec3d(-Math.sin(angle) * radius, jumpamount, -Math.cos(angle) * radius));
                } else {
                    // LILYPAD/DRIPLEAF JUMP

                    double result = 0;
                    int radius = 5;
                    BlockPos targetPos = null;
                    ArrayList<BlockPos> targetList = Sphere.checkSpherePos(Blocks.LILY_PAD.getDefaultState(), this.getEntityWorld(), this.getBlockPos(), radius, true);
                    ArrayList<BlockPos> northDripleafList = Sphere.checkSpherePos(Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.NORTH), this.getEntityWorld(), this.getBlockPos(), radius, true);
                    ArrayList<BlockPos> southDripleafList = Sphere.checkSpherePos(Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.SOUTH), this.getEntityWorld(), this.getBlockPos(), radius, true);
                    ArrayList<BlockPos> eastDripleafList = Sphere.checkSpherePos(Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.EAST), this.getEntityWorld(), this.getBlockPos(), radius, true);
                    ArrayList<BlockPos> westDripleafList = Sphere.checkSpherePos(Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.WEST), this.getEntityWorld(), this.getBlockPos(), radius, true);

                    targetList.addAll(northDripleafList);
                    targetList.addAll(southDripleafList);
                    targetList.addAll(eastDripleafList);
                    targetList.addAll(westDripleafList);

                    if(targetList.size() > 0) {
                        result = Math.round(Math.random() * (targetList.size() - 1));
                        targetPos = targetList.get((int)result);
                    }
                    if(!(targetPos == null)) {

                        double dx = targetPos.getX() - this.getBlockPos().getX();
                        double dy = targetPos.getY() - this.getBlockPos().getY();
                        double dz = targetPos.getZ() - this.getBlockPos().getZ();

                        if(dy < 0) {
                            dy = 0;
                        }

                        if(Math.sqrt(dx*dx) > 0 || Math.sqrt(dz*dz) > 0) {
                            this.updateVelocity(2F, new Vec3d(dx / 10, jumpamount + dy, dz / 10));
                        }
                    }

                }
            }
        }
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public static enum Variant {
        SWAMP(FrogEntity.SWAMP, "swamp", false),
        COLD(FrogEntity.COLD, "cold", false),
        TROPICAL(FrogEntity.TROPICAL, "tropical", false);
        public static final FrogEntity.Variant[] VARIANTS = (FrogEntity.Variant[])Arrays.stream(values()).sorted(Comparator.comparingInt(FrogEntity.Variant::getId)).toArray(Variant[]::new);
        private final int id;
        private final String name;
        private final boolean natural;

        private Variant(int id, String name, boolean natural) {
            this.id = id;
            this.name = name;
            this.natural = natural;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }
    }


    public static class FrogData extends PassiveEntity.PassiveData {
        public final int type;

        public FrogData(int type) {
            super(0.0F);
            this.type = type;
        }
    }
}