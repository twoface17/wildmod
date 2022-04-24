package frozenblock.wild.mod.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.event.EntityPositionSource;
import frozenblock.wild.mod.registry.RegisterEntities;
import frozenblock.wild.mod.registry.RegisterSounds;
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
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class AllayEntity extends PathAwareEntity implements InventoryOwner, GameEventListener {
    private static final int field_38405 = 16;
    private static final Vec3i ITEM_PICKUP_RANGE_EXPANDER = new Vec3i(1, 1, 1);
    private static final int field_38934 = 5;
    protected static final ImmutableList<SensorType<? extends Sensor<? super AllayEntity>>> SENSORS;
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES;
    public static final ImmutableList<Float> field_38937;
    private final frozenblock.wild.mod.event.EntityPositionSource positionSource = new EntityPositionSource(this, this.getStandingEyeHeight());
    private final EntityGameEventHandler gameEventHandler;
    private final SimpleInventory inventory = new SimpleInventory(1);
    private float field_38935;
    private float field_38936;

    public AllayEntity(EntityType<? extends AllayEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.setCanPickUpLoot(this.canPickUpLoot());
        this.gameEventHandler = new EntityGameEventHandler(this);
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
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.10000000149011612D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.10000000149011612D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0D);
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
                this.setVelocity(this.getVelocity().multiply(0.800000011920929D));
            } else if (this.isInLava()) {
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5D));
            } else {
                this.updateVelocity(this.getMovementSpeed(), movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.9100000262260437D));
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
        Entity var4 = source.getAttacker();
        if (var4 instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)var4;
            Optional<UUID> optional = this.getBrain().getOptionalMemory(RegisterEntities.LIKED_PLAYER);
            if (optional.isPresent() && playerEntity.getUuid().equals(optional.get())) {
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
        this.getBrain().tick((ServerWorld)this.world, this);
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
        }

    }

    public boolean canPickUpLoot() {
        return !this.isItemPickupCoolingDown() && this.isHoldingItem();
    }

    public boolean isHoldingItem() {
        return !this.getStackInHand(Hand.MAIN_HAND).isEmpty();
    }

    private boolean isItemPickupCoolingDown() {
        return this.getBrain().isMemoryInState(RegisterEntities.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleState.VALUE_PRESENT);
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
            this.getBrain().remember(RegisterEntities.LIKED_PLAYER, player.getUuid());
            return ActionResult.SUCCESS;
        } else if (!itemStack2.isEmpty() && hand == Hand.MAIN_HAND && itemStack.isEmpty()) {
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.world.playSoundFromEntity(player, this, RegisterSounds.ENTITY_ALLAY_ITEM_TAKEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            this.swingHand(Hand.MAIN_HAND);
            Iterator<ItemStack> var5 = this.getInventory().clearToList().iterator();

            while(var5.hasNext()) {
                ItemStack itemStack4 = var5.next();
                LookTargetUtil.give(this, itemStack4, this.getPos());
            }

            this.getBrain().forget(RegisterEntities.LIKED_PLAYER);
            player.giveItemStack(itemStack2);
            return ActionResult.SUCCESS;
        } else {
            return super.interactMob(player, hand);
        }
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    protected Vec3i getItemPickUpRangeExpander() {
        return ITEM_PICKUP_RANGE_EXPANDER;
    }

    public boolean canGather(ItemStack stack) {
        ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
        return !itemStack.isEmpty() && itemStack.isItemEqual(stack) && this.inventory.canInsert(itemStack);
    }

    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (this.canGather(itemStack)) {
            SimpleInventory simpleInventory = this.getInventory();
            boolean bl = simpleInventory.canInsert(itemStack);
            if (!bl) {
                return;
            }

            this.triggerItemPickedUpByEntityCriteria(item);
            this.sendPickup(item, itemStack.getCount());
            ItemStack itemStack2 = simpleInventory.addStack(itemStack);
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

    public PositionSource getPositionSource() {
        return (PositionSource) this.positionSource;
    }

    public int getRange() {
        return 16;
    }

    @Override
    public boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
        return false;
    }

    public void updateEventHandler(BiConsumer<EntityGameEventHandler, ServerWorld> biConsumer) {
        World var3 = this.world;
        if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;
            biConsumer.accept(this.gameEventHandler, serverWorld);
        }

    }


    public boolean listen(ServerWorld world, GameEvent event, frozenblock.wild.mod.event.GameEvent.Emitter emitter, Vec3d pos) {
        if (event != WildMod.NOTE_BLOCK_PLAY) {
            return false;
        } else {
            AllayBrain.rememberNoteBlock(this, new BlockPos(pos));
            return true;
        }
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

    static {
        SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS);
        MEMORY_MODULES = ImmutableList.of(MemoryModuleType.PATH, MemoryModuleType.LOOK_TARGET, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, RegisterEntities.LIKED_PLAYER, RegisterEntities.LIKED_NOTEBLOCK, RegisterEntities.LIKED_NOTEBLOCK_COOLDOWN_TICKS, RegisterEntities.ITEM_PICKUP_COOLDOWN_TICKS);
        field_38937 = ImmutableList.of(0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F, new Float[]{2.5F, 3.0F, 3.75F, 4.0F});
    }
}