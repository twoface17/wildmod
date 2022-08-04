package net.frozenblock.wildmod.entity.chestboat;

import com.google.common.collect.Lists;
import net.frozenblock.wildmod.misc.VehicleInventory;
import net.frozenblock.wildmod.registry.MangroveWoods;
import net.frozenblock.wildmod.registry.RegisterEntities;
import net.frozenblock.wildmod.registry.RegisterItems;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

import static net.frozenblock.wildmod.misc.WildBoatType.MANGROVE;

public class ChestBoatEntity extends BoatEntity implements Inventory, VehicleInventory {
    private static final int INVENTORY_SIZE = 27;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private Identifier lootTableId;
    private long lootTableSeed;

    public ChestBoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
    }

    public ChestBoatEntity(World world, double x, double y, double z) {
        this(RegisterEntities.CHEST_BOAT, world);
        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    protected float getPassengerHorizontalOffset() {
        return 0.15F;
    }

    protected int getMaxPassengers() {
        return 1;
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            float f = this.getPassengerHorizontalOffset();
            float g = (float)((this.isRemoved() ? 0.01F : this.getMountedHeightOffset()) + passenger.getHeightOffset());
            if (this.getPassengerList().size() > 1) {
                int i = this.getPassengerList().indexOf(passenger);
                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (passenger instanceof AnimalEntity) {
                    f += 0.2F;
                }
            }

            Vec3d vec3d = new Vec3d((double)f, 0.0, 0.0).rotateY(-this.getYaw() * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
            passenger.setPosition(this.getX() + vec3d.x, this.getY() + (double)g, this.getZ() + vec3d.z);
            passenger.setYaw(passenger.getYaw() + this.yawVelocity);
            passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
            this.copyEntityData(passenger);
            if (passenger instanceof AnimalEntity && this.getPassengerList().size() == this.getMaxPassengers()) {
                int j = passenger.getId() % 2 == 0 ? 90 : 270;
                passenger.setBodyYaw(((AnimalEntity)passenger).bodyYaw + (float)j);
                passenger.setHeadYaw(passenger.getHeadYaw() + (float)j);
            }

        }
    }

    @Override
    public void tick() {
        this.lastLocation = this.location;
        this.location = this.checkLocation();
        if (this.location != BoatEntity.Location.UNDER_WATER && this.location != BoatEntity.Location.UNDER_FLOWING_WATER) {
            this.ticksUnderwater = 0.0F;
        } else {
            ++this.ticksUnderwater;
        }

        if (!this.world.isClient && this.ticksUnderwater >= 60.0F) {
            this.removeAllPassengers();
        }

        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }

        if (this.getDamageWobbleStrength() > 0.0F) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
        }

        this.baseTick();
        this.method_7555();
        if (this.isLogicalSideForUpdatingMovement()) {
            if (!(this.getFirstPassenger() instanceof PlayerEntity)) {
                this.setPaddleMovings(false, false);
            }

            this.updateVelocity();
            if (this.world.isClient) {
                this.updatePaddles();
                this.world.sendPacket(new BoatPaddleStateC2SPacket(this.isPaddleMoving(0), this.isPaddleMoving(1)));
            }

            this.move(MovementType.SELF, this.getVelocity());
        } else {
            this.setVelocity(Vec3d.ZERO);
        }

        this.handleBubbleColumn();

        for(int i = 0; i <= 1; ++i) {
            if (this.isPaddleMoving(i)) {
                if (!this.isSilent()
                        && (double)(this.paddlePhases[i] % (float) (Math.PI * 2)) <= (float) (Math.PI / 4)
                        && (double)((this.paddlePhases[i] + (float) (Math.PI / 8)) % (float) (Math.PI * 2)) >= (float) (Math.PI / 4)) {
                    SoundEvent soundEvent = this.getPaddleSoundEvent();
                    if (soundEvent != null) {
                        Vec3d vec3d = this.getRotationVec(1.0F);
                        double d = i == 1 ? -vec3d.z : vec3d.z;
                        double e = i == 1 ? vec3d.x : -vec3d.x;
                        this.world
                                .playSound(null, this.getX() + d, this.getY(), this.getZ() + e, soundEvent, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
                    }
                }

                this.paddlePhases[i] += (float) (Math.PI / 8);
            } else {
                this.paddlePhases[i] = 0.0F;
            }
        }

        this.checkBlockCollision();
        List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox().expand(0.2F, -0.01F, 0.2F), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty()) {
            boolean bl = !this.world.isClient && !(this.getPrimaryPassenger() instanceof PlayerEntity);

            for (Entity entity : list) {
                if (!entity.hasPassenger(this)) {
                    if (bl
                            && this.getPassengerList().size() < this.getMaxPassengers()
                            && !entity.hasVehicle()
                            && entity.getWidth() < this.getWidth()
                            && entity instanceof LivingEntity
                            && !(entity instanceof WaterCreatureEntity)
                            && !(entity instanceof PlayerEntity)) {
                        entity.startRiding(this);
                    } else {
                        this.pushAwayFrom(entity);
                    }
                }
            }
        }

    }

    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    public boolean collidesWith(Entity other) {
        return canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
    }

    public Item asItem() {
        if (this.getBoatType() == Type.SPRUCE) {
            return RegisterItems.SPRUCE_CHEST_BOAT;
        } else if (this.getBoatType() == Type.BIRCH) {
            return RegisterItems.BIRCH_CHEST_BOAT;
        } else if (this.getBoatType() == Type.JUNGLE) {
            return RegisterItems.JUNGLE_CHEST_BOAT;
        } else if (this.getBoatType() == Type.ACACIA) {
            return RegisterItems.ACACIA_CHEST_BOAT;
        } else if (this.getBoatType() == Type.DARK_OAK) {
            return RegisterItems.DARK_OAK_CHEST_BOAT;
        } else if (this.getBoatType() == MANGROVE) {
            return RegisterItems.MANGROVE_CHEST_BOAT;
        } else {
            return RegisterItems.OAK_CHEST_BOAT;
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeInventoryToNbt(nbt);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readInventoryFromNbt(nbt);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return this.canAddPassenger(player) && !player.shouldCancelInteraction() ? super.interact(player, hand) : this.open(this::emitGameEvent, player);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < this.getMaxPassengers() && !this.isSubmergedIn(FluidTags.WATER);
    }

    @Override
    public int size() {
        return INVENTORY_SIZE;
    }

    @Nullable
    @Override
    public Identifier getLootTableId() {
        return this.lootTableId;
    }

    @Override
    public void setLootTableId(@Nullable Identifier lootTableId) {
        this.lootTableId = lootTableId;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public void resetInventory() {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }


    @Override
    public ItemStack getStack(int slot) {
        return this.getInventoryStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.removeInventoryStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.removeInventoryStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.setInventoryStack(slot, stack);
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        return this.getInventoryStackReference(mappedIndex);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.canPlayerAccess(player);
    }

    @Override
    public void clear() {
        this.clearInventory();
    }

    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.lootTableId != null && playerEntity.isSpectator()) {
            return null;
        } else {
            this.generateLoot(playerInventory.player);
            return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, this);
        }
    }

    public void generateLoot(@Nullable PlayerEntity player) {
        this.generateInventoryLoot(player);
    }
}
