package frozenblock.wild.mod.entity;


import frozenblock.wild.mod.liukrastapi.WardenGetAttackedGoal;
import frozenblock.wild.mod.liukrastapi.WardenGoal;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class WardenEntity extends HostileEntity {
    private static final double speed = 0.5D;
    protected boolean roar;

    protected SoundEvent getAmbientSound() {
        return RegisterSounds.ENTITY_WARDEN_AMBIENT;
    }

    public WardenEntity(EntityType<? extends WardenEntity> entityType, World world) {
        super(entityType, world);
    }


    public static DefaultAttributeContainer.Builder createWardenAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 84.0D);
    }

    protected void initGoals() {
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.4D));
        this.goalSelector.add(2, new WardenGetAttackedGoal(this, speed*1.5));
    }

    public void tickMovement() {
        if (this.isAlive()) {
            boolean bl = this.burnsInDaylight() && this.isAffectedByDaylight();
            if (bl) {
                ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageable()) {
                        itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
                        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                            this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    bl = false;
                }

                if (bl) {
                    this.setOnFireFor(8);
                }
            }
        }



        super.tickMovement();
    }

    public boolean damage(DamageSource source, float amount) {
        boolean bl = super.damage(source, amount);
        this.playSound(SoundEvents.BLOCK_SCULK_SENSOR_HIT, 1.0F, 1.0F);
        return bl;
    }

    protected boolean burnsInDaylight() {
        return true;
    }



}
