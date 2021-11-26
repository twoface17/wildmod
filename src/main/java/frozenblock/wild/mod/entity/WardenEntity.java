package frozenblock.wild.mod.entity;


import frozenblock.wild.mod.liukrastapi.WardenGetAttackedGoal;
import frozenblock.wild.mod.liukrastapi.WardenGoal;
import frozenblock.wild.mod.registry.RegisterSounds;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.EnumSet;

public class WardenEntity extends HostileEntity {
    private static final double speed = 0.5D;
    protected boolean roar;
//    public static final Object2IntMap<GameEvent> FREQUENCIES = Object2IntMaps.unmodifiable((Object2IntMap) Util.make(new Object2IntOpenHashMap(), (map) -> {
//        map.put(GameEvent.STEP, 1);
//        map.put(GameEvent.FLAP, 2);
//        map.put(GameEvent.SWIM, 3);
//        map.put(GameEvent.ELYTRA_FREE_FALL, 4);
//        map.put(GameEvent.HIT_GROUND, 5);
//        map.put(GameEvent.SPLASH, 6);
//        map.put(GameEvent.WOLF_SHAKING, 6);
//        map.put(GameEvent.MINECART_MOVING, 6);
//        map.put(GameEvent.RING_BELL, 6);
//        map.put(GameEvent.BLOCK_CHANGE, 6);
//        map.put(GameEvent.PROJECTILE_SHOOT, 7);
//        map.put(GameEvent.DRINKING_FINISH, 7);
//        map.put(GameEvent.PRIME_FUSE, 7);
//        map.put(GameEvent.PROJECTILE_LAND, 8);
//        map.put(GameEvent.EAT, 8);
//        map.put(GameEvent.MOB_INTERACT, 8);
//        map.put(GameEvent.ENTITY_DAMAGED, 8);
//        map.put(GameEvent.EQUIP, 9);
//        map.put(GameEvent.SHEAR, 9);
//        map.put(GameEvent.RAVAGER_ROAR, 9);
//        map.put(GameEvent.BLOCK_CLOSE, 10);
//        map.put(GameEvent.BLOCK_UNSWITCH, 10);
//        map.put(GameEvent.BLOCK_UNPRESS, 10);
//        map.put(GameEvent.BLOCK_DETACH, 10);
//        map.put(GameEvent.DISPENSE_FAIL, 10);
//        map.put(GameEvent.BLOCK_OPEN, 11);
//        map.put(GameEvent.BLOCK_SWITCH, 11);
//        map.put(GameEvent.BLOCK_PRESS, 11);
//        map.put(GameEvent.BLOCK_ATTACH, 11);
//        map.put(GameEvent.ENTITY_PLACE, 12);
//        map.put(GameEvent.BLOCK_PLACE, 12);
//        map.put(GameEvent.FLUID_PLACE, 12);
//        map.put(GameEvent.ENTITY_KILLED, 13);
//        map.put(GameEvent.BLOCK_DESTROY, 13);
//        map.put(GameEvent.FLUID_PICKUP, 13);
//        map.put(GameEvent.FISHING_ROD_REEL_IN, 14);
//        map.put(GameEvent.CONTAINER_CLOSE, 14);
//        map.put(GameEvent.PISTON_CONTRACT, 14);
//        map.put(GameEvent.SHULKER_CLOSE, 14);
//        map.put(GameEvent.PISTON_EXTEND, 15);
//        map.put(GameEvent.CONTAINER_OPEN, 15);
//        map.put(GameEvent.FISHING_ROD_CAST, 15);
//        map.put(GameEvent.EXPLODE, 15);
//        map.put(GameEvent.LIGHTNING_STRIKE, 15);
//        map.put(GameEvent.SHULKER_OPEN, 15);
//    })); CURRENTLY UNUSED

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
        this.targetSelector.add(1, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(4, new WardenGoal());
//        this.goalSelector.add(7, new LookAtTargetGoal(this));
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



//            private static class LookAtTargetGoal extends Goal {
//                private final WardenEntity wardenEntity;
//
//            	        	public LookAtTargetGoal(WardenEntity wardenEntity) {
//                 this.wardenEntity = wardenEntity;
//                    this.setControls(EnumSet.of(Goal.Control.LOOK));
//            }
//
//            public boolean canStart() {
//                return true;
//            }
//
//            public void tick() {
//                if (this.wardenEntity.getTarget() == null) {
//                    Vec3d vec3d = this.wardenEntity.getVelocity();
//                    this.wardenEntity.setYaw(-((float) MathHelper.atan2(vec3d.x, vec3d.z)) * 57.295776F);
//                    this.wardenEntity.bodyYaw = this.wardenEntity.getYaw();
//                } else {
//                    LivingEntity livingEntity = this.wardenEntity.getTarget();
//                    double d = 64.0D;
//                    if (livingEntity.squaredDistanceTo(this.wardenEntity) < 4096.0D) {
//                        double e = livingEntity.getX() - this.wardenEntity.getX();
//                        double f = livingEntity.getZ() - this.wardenEntity.getZ();
//                        this.wardenEntity.setYaw(-((float)MathHelper.atan2(e, f)) * 57.295776F);
//                        this.wardenEntity.bodyYaw = this.wardenEntity.getYaw();
//                    }
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
