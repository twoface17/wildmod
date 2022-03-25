package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.WardenEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class WardenWanderGoal
        extends Goal {
    protected final WardenEntity mob;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected final double speed;
    public static final Random random = new Random();


    public WardenWanderGoal(WardenEntity entity, double speed) {
        this.mob = entity;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        Vec3d vec3d;
        if (this.mob.roarTicksLeft1 > 0) {return false;}
        if (this.mob.hasDug || this.mob.emergeTicksLeft==-5) { return false; }
        if (this.mob.emergeTicksLeft>0) {return false;}
        if (this.mob.movementPriority<=1 && this.mob.ticksToWander<=0) {

            if (this.mob.hasPassengers()) { return false; }

            if ((vec3d = this.getWanderTarget()) == null) {return false;}

            this.targetX = vec3d.x;
            this.targetY = vec3d.y;
            this.targetZ = vec3d.z;

            return true;
        }
        return false;
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        return NoPenaltyTargeting.find(this.mob, 10, 7);
    }

    @Override
    public void start() {
        this.mob.wanderTicksLeft=random.nextInt(50,80);
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        this.mob.movementPriority=1;
        this.mob.canSniff=true;
    }

    @Override
    public boolean shouldContinue() {
        return this.mob.wanderTicksLeft>0;
    }

}

