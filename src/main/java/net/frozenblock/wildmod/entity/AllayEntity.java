package net.frozenblock.wildmod.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.frozenblock.wildmod.entity.ai.AllayBrain;
import net.frozenblock.wildmod.event.VibrationListener;
import net.frozenblock.wildmod.event.WildEntityPositionSource;
import net.frozenblock.wildmod.event.WildGameEvent;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.frozenblock.wildmod.registry.RegisterTags;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class AllayEntity extends WildPathAwareEntity implements InventoryOwner, VibrationListener.Callback {
    private static final Logger field_39045 = LogUtils.getLogger();
    private static final int field_38405 = 16;
    private static final Vec3i ITEM_PICKUP_RANGE_EXPANDER = new Vec3i(1, 1, 1);
    private static final int field_38934 = 5;
    protected static final ImmutableList<SensorType<? extends Sensor<? super AllayEntity>>> SENSORS = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS
    );
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.PATH,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.VISIBLE_MOBS,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
            RegisterMemoryModules.LIKED_PLAYER,
            RegisterMemoryModules.LIKED_NOTEBLOCK,
            RegisterMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS,
            RegisterMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS
    );
    public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of(
            0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F,
            2.5F, 3.0F, 3.75F, 4.0F);
    private final EntityGameEventHandler gameEventHandler;
    private VibrationListener vibrationListener;
    private final SimpleInventory inventory = new SimpleInventory(1);
    private float field_38935;
    private float field_38936;

    public AllayEntity(EntityType<? extends AllayEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.setCanPickUpLoot(this.canPickUpLoot());
        this.vibrationListener = new VibrationListener(new WildEntityPositionSource(this, this.getStandingEyeHeight()), 16, this, null, 0, 0);
        this.gameEventHandler = new EntityGameEventHandler(this.vibrationListener);
    }

    protected Brain.Profile<AllayEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return AllayBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<AllayEntity> getBrain() {
        return (Brain<AllayEntity>) super.getBrain();
    }

    public static DefaultAttributeContainer.Builder createAllayAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.1F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
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
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.8F));
            } else if (this.isInLava()) {
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5));
            } else {
                this.updateVelocity(this.getMovementSpeed(), movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.91F));
            }
        }

        this.updateLimbs(this, false);
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.6F;
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public boolean damage(DamageSource source, float amount) {
        Entity optional = source.getAttacker();
        if (optional instanceof PlayerEntity playerEntity) {
            Optional<UUID> optionalUUID = this.getBrain().getOptionalMemory(RegisterMemoryModules.LIKED_PLAYER);
            if (optionalUUID.isPresent() && playerEntity.getUuid().equals(optionalUUID.get())) {
                return false;
            }
        }

        return super.damage(source, amount);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    protected SoundEvent getAmbientSound() {
        return this.hasStackEquipped(EquipmentSlot.MAINHAND) ? RegisterSounds.ENTITY_ALLAY_AMBIENT_WITH_ITEM : RegisterSounds.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return RegisterSounds.ENTITY_ALLAY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return RegisterSounds.ENTITY_ALLAY_DEATH;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    protected void mobTick() {
        this.world.getProfiler().push("allayBrain");
        this.getBrain().tick((ServerWorld) this.world, this);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("allayActivityUpdate");
        AllayBrain.updateActivities(this);
        this.world.getProfiler().pop();
        super.mobTick();
    }

    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient && this.isAlive() && this.age % 10 == 0) {
            this.heal(1.0F);
        }

    }

    public void tick() {
        super.tick();
        if (this.world.isClient) {
            this.field_38936 = this.field_38935;
            if (this.isHoldingItem()) {
                this.field_38935 = MathHelper.clamp(this.field_38935 + 1.0F, 0.0F, 5.0F);
            } else {
                this.field_38935 = MathHelper.clamp(this.field_38935 - 1.0F, 0.0F, 5.0F);
            }
        } else {
            this.vibrationListener.tick(this.world);
        }

    }

    public boolean canPickUpLoot() {
        return !this.isItemPickupCoolingDown() && this.isHoldingItem();
    }

    public boolean isHoldingItem() {
        return !this.getStackInHand(Hand.MAIN_HAND).isEmpty();
    }

    public boolean canEquip(ItemStack stack) {
        return false;
    }

    private boolean isItemPickupCoolingDown() {
        return this.getBrain().isMemoryInState(RegisterMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleState.VALUE_PRESENT);
    }

    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        ItemStack itemStack2 = this.getStackInHand(Hand.MAIN_HAND);
        if (itemStack2.isEmpty() && !itemStack.isEmpty()) {
            ItemStack itemStack3 = itemStack.copy();
            itemStack3.setCount(1);
            this.setStackInHand(Hand.MAIN_HAND, itemStack3);
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            this.world.playSoundFromEntity(player, this, RegisterSounds.ENTITY_ALLAY_ITEM_GIVEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            this.getBrain().remember(RegisterMemoryModules.LIKED_PLAYER, player.getUuid());
            return ActionResult.SUCCESS;
        } else if (!itemStack2.isEmpty() && hand == Hand.MAIN_HAND && itemStack.isEmpty()) {
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.world.playSoundFromEntity(player, this, RegisterSounds.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            this.swingHand(Hand.MAIN_HAND);

            for (ItemStack itemStack4 : this.getInventory().clearToList()) {
                LookTargetUtil.give(this, itemStack4, this.getPos());
            }

            this.getBrain().forget(RegisterMemoryModules.LIKED_PLAYER);
            player.giveItemStack(itemStack2);
            return ActionResult.SUCCESS;
        } else {
            return super.interactMob(player, hand);
        }
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    public Vec3i getItemPickUpRangeExpander() {
        return ITEM_PICKUP_RANGE_EXPANDER;
    }

    public boolean canGather(ItemStack stack) {
        ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
        return !itemStack.isEmpty() && itemStack.isItemEqual(stack) && this.inventory.canInsert(stack);
    }

    protected void loot(ItemEntity item) {
        pickUpItem(this, this, item);
    }

    static void pickUpItem(MobEntity entity, InventoryOwner inventoryOwner, ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (entity.canGather(itemStack)) {
            SimpleInventory simpleInventory = (SimpleInventory) inventoryOwner.getInventory();
            boolean bl = simpleInventory.canInsert(itemStack);
            if (!bl) {
                return;
            }

            entity.triggerItemPickedUpByEntityCriteria(item);
            int i = itemStack.getCount();
            ItemStack itemStack2 = simpleInventory.addStack(itemStack);
            entity.sendPickup(item, i - itemStack2.getCount());
            if (itemStack2.isEmpty()) {
                item.discard();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }
        }

    }

    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public boolean hasWings() {
        return !this.isOnGround();
    }

    public boolean method_43395() {
        return this.limbDistance > 0.3F;
    }

    public float method_43397(float f) {
        return MathHelper.lerp(f, this.field_38936, this.field_38935) / 5.0F;
    }

    protected void dropInventory() {
        super.dropInventory();
        this.inventory.clearToList().forEach(this::dropStack);
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
            this.dropStack(itemStack);
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity) {
        if (this.world != world || this.isRemoved() || this.isAiDisabled()) {
            return false;
        } else if (!this.brain.hasMemoryModule(RegisterMemoryModules.LIKED_NOTEBLOCK)) {
            return true;
        } else {
            Optional<GlobalPos> optional = this.brain.getOptionalMemory(RegisterMemoryModules.LIKED_NOTEBLOCK);
            return optional.isPresent()
                    && optional.get().getDimension() == world.getRegistryKey()
                    && optional.get().getPos().equals(pos);
        }
    }

    public void accept(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance) {
        if (event == WildGameEvent.NOTE_BLOCK_PLAY) {
            AllayBrain.rememberNoteBlock(this, new BlockPos(pos));
        }

    }

    public TagKey<net.minecraft.world.event.GameEvent> getTag() {
        return RegisterTags.ALLAY_CAN_LISTEN;
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Inventory", this.inventory.toNbtList());
        VibrationListener.createCodec(this)
                .encodeStart(NbtOps.INSTANCE, this.vibrationListener)
                .resultOrPartial(field_39045::error)
                .ifPresent(nbtElement -> nbt.put("listener", nbtElement));
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.inventory.readNbtList(nbt.getList("Inventory", 10));
        if (nbt.contains("listener", 10)) {
            VibrationListener.createCodec(this)
                    .parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")))
                    .resultOrPartial(field_39045::error)
                    .ifPresent(vibrationListener -> this.vibrationListener = vibrationListener);
        }

    }

    protected boolean method_43689() {
        return false;
    }
}
