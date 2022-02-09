package frozenblock.wild.mod.liukrastapi;

import java.util.EnumSet;

import frozenblock.wild.mod.entity.WardenEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class WardenWanderGoal
        extends Goal {
    protected final WardenEntity mob;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected final double speed;
    protected int chance;
    protected boolean ignoringChance;
    private final boolean canDespawn;

    public WardenWanderGoal(WardenEntity mob, double speed) {
        this(mob, speed, 120);
    }

    public WardenWanderGoal(WardenEntity mob, double speed, int chance) {
        this(mob, speed, chance, true);
    }

    public WardenWanderGoal(WardenEntity entity, double speed, int chance, boolean canDespawn) {
        this.mob = entity;
        this.speed = speed;
        this.chance = chance;
        this.canDespawn = canDespawn;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        Vec3d vec3d;
        if (this.mob.roarTicksLeft1 > 0) {
            return false;
        }
        if (this.mob.hasDug || this.mob.emergeTicksLeft==-5) { return false; }
        if (this.mob.getNavigation().isIdle() && this.mob.getWorld().getTime()-this.mob.vibrationTimer>400 && this.mob.world.getTime()-this.mob.clientSniffStart>400) {
            if (this.mob.hasPassengers()) {
                return false;
            }
            if (!this.ignoringChance) {
                if (this.canDespawn && this.mob.getDespawnCounter() >= 100) {
                    return false;
                }
                if (this.mob.getRandom().nextInt(WardenWanderGoal.toGoalTicks(this.chance)) != 0) {
                    return false;
                }
            }
            if ((vec3d = this.getWanderTarget()) == null) {
                return false;
            }
            this.targetX = vec3d.x;
            this.targetY = vec3d.y;
            this.targetZ = vec3d.z;
            this.ignoringChance = false;
            return true;
        }
        return false;
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        return NoPenaltyTargeting.find(this.mob, 10, 7);
    }

    @Override
    public boolean shouldContinue() {
        if (this.mob.roarTicksLeft1 > 0) {
            return false;
        }
        return this.mob.getWorld().getTime()-this.mob.vibrationTimer>400 && this.mob.world.getTime()-this.mob.clientSniffStart>400;
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }
}

