package frozenblock.wild.mod.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.*;


public class FrogEntity extends PathAwareEntity {

    private static final TrackedData<Integer> VARIANT;
    public static final String VARIANT_KEY = "Variant";
    public static final int SWAMP = 0;
    public static final int COLD = 1;
    public static final int TROPICAL = 2;


    public FrogEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createFrogAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.50D);
    }

    public static boolean canColdSpawn(World world, BlockPos pos) {
        return world.getBiome(pos).isCold(pos);
    }

    public static boolean canTropicalSpawn(World world, BlockPos pos) {
        return world.getBiome(pos).getCategory().equals(Biome.Category.JUNGLE);
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

        this.goalSelector.add(9, new DiveJumpingGoal() {
            @Override
            public boolean canStart() {
                return false;
            }
        });
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.4D));
    }

    public static enum Variant {
        SWAMP(FrogEntity.SWAMP, "swamp", false),
        COLD(FrogEntity.COLD, "cold", false),
        TROPICAL(FrogEntity.TROPICAL, "tropical", false);
        public static final FrogEntity.Variant[] VARIANTS = (FrogEntity.Variant[])Arrays.stream(values()).sorted(Comparator.comparingInt(FrogEntity.Variant::getId)).toArray((i) -> {
            return new FrogEntity.Variant[i];
        });
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