package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.entity.AllayEntity;
import net.frozenblock.wildmod.entity.WildPathAwareEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    @Shadow @Nullable public abstract Entity getHoldingEntity();

    @Final
    @Shadow
    private static final TrackedData<Byte> MOB_FLAGS = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
    @Final
    @Shadow private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
    @Final
    @Shadow
    private final DefaultedList<ItemStack> handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
    @Shadow private boolean canPickUpLoot;

    @Final
    @Shadow protected final float[] handDropChances = new float[2];
    @Final
    @Shadow
    protected final float[] armorDropChances = new float[4];

    @Shadow private boolean persistent;

    @Shadow
    public boolean canPickUpLoot() {
        return this.canPickUpLoot;
    }

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    public void tickMovement(CallbackInfo ci) {
        super.tickMovement();
        this.world.getProfiler().push("looting");
        if (!this.world.isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            MobEntity mobEntity = (MobEntity) ((ServerWorld)this.world).getEntity(uuid);
            WildPathAwareEntity wildPathAwareEntity = (WildPathAwareEntity)mobEntity;
            assert wildPathAwareEntity != null;
            Vec3i vec3i = wildPathAwareEntity.getItemPickUpRangeExpander();

            for(ItemEntity itemEntity : this.world
                    .getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ()))) {
                if (!itemEntity.isRemoved() && !itemEntity.getStack().isEmpty() && !itemEntity.cannotPickup() && this.canGather(itemEntity.getStack())) {
                    this.loot(itemEntity);
                }
            }
        }

        this.world.getProfiler().pop();
    }

    @Shadow
    public boolean canPickupItem(ItemStack stack) {
        return true;
    }

    @Shadow
    public boolean canGather(ItemStack stack) {
        return this.canPickupItem(stack);
    }

    @Shadow
    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (this.tryEquip(itemStack)) {
            this.triggerItemPickedUpByEntityCriteria(item);
            this.sendPickup(item, itemStack.getCount());
            item.discard();
        }

    }

    @Shadow
    public boolean tryEquip(ItemStack equipment) {
        EquipmentSlot equipmentSlot = getPreferredEquipmentSlot(equipment);
        ItemStack itemStack = this.getEquippedStack(equipmentSlot);
        boolean bl = this.prefersNewEquipment(equipment, itemStack);
        if (bl && this.canPickupItem(equipment)) {
            double d = (double)this.getDropChance(equipmentSlot);
            if (!itemStack.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d) {
                this.dropStack(itemStack);
            }

            this.equipLootStack(equipmentSlot, equipment);
            return true;
        } else {
            return false;
        }
    }

    @Shadow
    protected void equipLootStack(EquipmentSlot slot, ItemStack stack) {
        this.equipStack(slot, stack);
        this.updateDropChances(slot);
        this.persistent = true;
    }

    @Shadow
    public void updateDropChances(EquipmentSlot slot) {
        switch(slot.getType()) {
            case HAND:
                this.handDropChances[slot.getEntitySlotId()] = 2.0F;
                break;
            case ARMOR:
                this.armorDropChances[slot.getEntitySlotId()] = 2.0F;
        }

    }

    @Shadow
    protected float getDropChance(EquipmentSlot slot) {
        return switch(slot.getType()) {
            case HAND -> this.handDropChances[slot.getEntitySlotId()];
            case ARMOR -> this.armorDropChances[slot.getEntitySlotId()];
            default -> 0.0F;
        };
    }

    @Shadow
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    @Shadow
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        switch(slot.getType()) {
            case HAND:
                return (ItemStack)this.handItems.get(slot.getEntitySlotId());
            case ARMOR:
                return (ItemStack)this.armorItems.get(slot.getEntitySlotId());
            default:
                return ItemStack.EMPTY;
        }
    }

    @Shadow
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        this.processEquippedStack(stack);
        this.onEquipStack(stack);
        switch(slot.getType()) {
            case HAND:
                this.handItems.set(slot.getEntitySlotId(), stack);
                break;
            case ARMOR:
                this.armorItems.set(slot.getEntitySlotId(), stack);
        }

    }

    @Shadow
    public Arm getMainArm() {
        return this.isLeftHanded() ? Arm.LEFT : Arm.RIGHT;
    }

    @Shadow
    public boolean isLeftHanded() {
        return (this.dataTracker.get(MOB_FLAGS) & 2) != 0;
    }

    @Shadow
    protected boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack) {
        if (oldStack.isEmpty()) {
            return true;
        } else if (newStack.getItem() instanceof SwordItem) {
            if (!(oldStack.getItem() instanceof SwordItem)) {
                return true;
            } else {
                SwordItem swordItem = (SwordItem)newStack.getItem();
                SwordItem swordItem2 = (SwordItem)oldStack.getItem();
                if (swordItem.getAttackDamage() != swordItem2.getAttackDamage()) {
                    return swordItem.getAttackDamage() > swordItem2.getAttackDamage();
                } else {
                    return this.prefersNewDamageableItem(newStack, oldStack);
                }
            }
        } else if (newStack.getItem() instanceof BowItem && oldStack.getItem() instanceof BowItem) {
            return this.prefersNewDamageableItem(newStack, oldStack);
        } else if (newStack.getItem() instanceof CrossbowItem && oldStack.getItem() instanceof CrossbowItem) {
            return this.prefersNewDamageableItem(newStack, oldStack);
        } else if (newStack.getItem() instanceof ArmorItem) {
            if (EnchantmentHelper.hasBindingCurse(oldStack)) {
                return false;
            } else if (!(oldStack.getItem() instanceof ArmorItem)) {
                return true;
            } else {
                ArmorItem armorItem = (ArmorItem)newStack.getItem();
                ArmorItem armorItem2 = (ArmorItem)oldStack.getItem();
                if (armorItem.getProtection() != armorItem2.getProtection()) {
                    return armorItem.getProtection() > armorItem2.getProtection();
                } else if (armorItem.getToughness() != armorItem2.getToughness()) {
                    return armorItem.getToughness() > armorItem2.getToughness();
                } else {
                    return this.prefersNewDamageableItem(newStack, oldStack);
                }
            }
        } else {
            if (newStack.getItem() instanceof MiningToolItem) {
                if (oldStack.getItem() instanceof BlockItem) {
                    return true;
                }

                if (oldStack.getItem() instanceof MiningToolItem) {
                    MiningToolItem miningToolItem = (MiningToolItem)newStack.getItem();
                    MiningToolItem miningToolItem2 = (MiningToolItem)oldStack.getItem();
                    if (miningToolItem.getAttackDamage() != miningToolItem2.getAttackDamage()) {
                        return miningToolItem.getAttackDamage() > miningToolItem2.getAttackDamage();
                    }

                    return this.prefersNewDamageableItem(newStack, oldStack);
                }
            }

            return false;
        }
    }

    @Shadow
    public boolean prefersNewDamageableItem(ItemStack newStack, ItemStack oldStack) {
        if (newStack.getDamage() >= oldStack.getDamage() && (!newStack.hasNbt() || oldStack.hasNbt())) {
            if (newStack.hasNbt() && oldStack.hasNbt()) {
                return newStack.getNbt().getKeys().stream().anyMatch(key -> !key.equals("Damage"))
                        && !oldStack.getNbt().getKeys().stream().anyMatch(key -> !key.equals("Damage"));
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


}
