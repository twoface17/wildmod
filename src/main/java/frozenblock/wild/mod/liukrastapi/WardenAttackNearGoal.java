package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.WardenEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class WardenAttackNearGoal extends Goal {

    private final WardenEntity mob;

    public WardenAttackNearGoal(WardenEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (this.mob.emergeTicksLeft > 0) {
            return false;
        }
        if (this.mob.roarTicksLeft1 > 0) {
            return false;
        }
        if (this.mob.sniffTicksLeft > 0) {
            return false;
        }
        if (this.mob.attackCooldown > 0) {
            return false;
        }
        if (this.mob.hasDug || this.mob.emergeTicksLeft==-5) { return false; }
        return this.mob.getClosestEntity() != null && this.mob.getSuspicion(this.mob.getClosestEntity())>=7;
    }

    public void start() {
        LivingEntity closeEntity=this.mob.getClosestEntity();
        double d = (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
        double e = this.mob.squaredDistanceTo(closeEntity.getX(), closeEntity.getY(), closeEntity.getZ());
        double f = (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
        double g = this.mob.squaredDistanceTo(closeEntity.getX(), closeEntity.getY(), closeEntity.getZ());
        if (!(e > d)) {
            this.mob.addSuspicion(closeEntity,50);
            if (this.mob.world.getDifficulty().getId() != 0 && !(closeEntity instanceof WardenEntity)) {
                if (!this.mob.entityList.isEmpty()) {
                    if (this.mob.entityList.contains(closeEntity.getUuid().hashCode())) {
                        int slot = this.mob.entityList.indexOf(closeEntity.getUuid().hashCode());
                        this.mob.susList.set(slot, this.mob.susList.getInt(slot) + this.mob.getSuspicion(closeEntity));
                        if (this.mob.susList.getInt(slot) >= 45 && this.mob.getTrackingEntity() == null) {
                            this.mob.trackingEntity = closeEntity.getUuidAsString();
                        }}}}}
        if (this.mob.getTrackingEntityForRoarNavigation()!=null && this.mob.roarTicksLeft1 <= 0 && !(g > f)) {
                        this.mob.tryAttack(closeEntity);
                        this.mob.attackNearCooldown = 100 - (this.mob.trueOverallAnger() / 2) - (this.mob.getSuspicion(closeEntity) / 4);
                    }}}
