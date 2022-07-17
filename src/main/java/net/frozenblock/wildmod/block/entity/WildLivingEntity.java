package net.frozenblock.wildmod.block.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class WildLivingEntity extends LivingEntity {
    private boolean experienceDroppingDisabled;

    public WildLivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }


    public void disableExperienceDropping() {
        this.experienceDroppingDisabled = true;
    }

    public boolean isExperienceDroppingDisabled() {
        return this.experienceDroppingDisabled;
    }

    public int getXpToDrop() {
        return 0;
    }

    public boolean shouldDropXp() {
        return !this.isBaby();
    }

    public void dropXp() {
        if (this.world instanceof ServerWorld
                && !this.isExperienceDroppingDisabled()
                && (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.shouldDropXp() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
            ExperienceOrbEntity.spawn((ServerWorld) this.world, this.getPos(), this.getXpToDrop());
        }

    }

    public abstract Iterable<ItemStack> getArmorItems();

    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

    public abstract Arm getMainArm();
}
