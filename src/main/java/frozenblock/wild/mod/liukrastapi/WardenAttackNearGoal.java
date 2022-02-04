package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.WardenEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

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
        if (this.mob.attackNearCooldown > 0) {
            return false;
        }
        return this.mob.getClosestEntity() != null && this.mob.getSuspicion(this.mob.getClosestEntity())>=7;
    }

    public void start() {
        LivingEntity closeEntity=this.mob.getClosestEntity();
        double d = (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
        double e = this.mob.squaredDistanceTo(closeEntity.getX(), closeEntity.getY(), closeEntity.getZ());
        if (!(e > d)) {
            this.mob.tryAttack(closeEntity);
            this.mob.attackNearCooldown = 100 - (this.mob.trueOverallAnger()/2) - (this.mob.getSuspicion(closeEntity)/4);
            this.mob.addSuspicion(closeEntity, 3);
        }
    }

}
