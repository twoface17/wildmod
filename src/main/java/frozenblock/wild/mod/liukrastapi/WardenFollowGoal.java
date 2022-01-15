package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.WardenEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Objects;


public class WardenFollowGoal extends Goal {

    private final WardenEntity mob;
    private final double speed;

    public WardenFollowGoal(WardenEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    public boolean canStart() {
        boolean exit = false;
        BlockPos lasteventpos = this.mob.lasteventpos;
        World lasteventWorld = this.mob.lasteventworld;
        LivingEntity lasteventEntity = this.mob.lastevententity;

       if (this.mob.emergeTicksLeft>0) {
            return false;
       }
       if (this.mob.followTicksLeft<=0) {
            return false;
        }
       if (this.mob.followTicksLeft>0) {
           exit = true;
       }
       if (this.mob.getWorld().getEntityById(this.mob.followingEntity)==null) {
           return false;
       }
       if (Objects.requireNonNull(this.mob.getWorld().getEntityById(this.mob.followingEntity)).isAlive() && this.mob.canFollow(this.mob.getWorld().getEntityById(this.mob.followingEntity), true) && !(this.mob.sniffTicksLeft>0)) {
           exit = true;
       }
        int r = this.mob.getRoarTicksLeft1();
        if (r > 0) {
            exit = false;
        }
        if (exit && this.mob.getAttacker() == null) {
            this.mob.navigationEntity = this.mob.lastevententity;
        }
        return exit;
    }

    public boolean shouldContinue() {
        return false;
    }

    public void start() {
        if (this.mob.getAttacker()==null) {
            if (this.mob.getWorld().getEntityById(this.mob.followingEntity)!=null) {
                Entity entity = this.mob.getWorld().getEntityById(this.mob.followingEntity);
                assert entity != null;
                if (entity.isAlive() && this.mob.canFollow(entity, true) && !(this.mob.sniffTicksLeft>0)) {
                    this.mob.getNavigation().startMovingTo(entity, (speed + (MathHelper.clamp(this.mob.getSuspicion(entity), 0, 15) * 0.03) + (this.mob.overallAnger() * 0.003)));
                    this.mob.getNavigation().recalculatePath();
                    double d = (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
                    double e = this.mob.squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ());
                    if (!(e > d) && this.mob.getAttackTicksLeft1()<=0) {
                        this.mob.tryAttack(entity);
                    }
                } else {
                    this.mob.followTicksLeft=0;
                }
            }
        }
        }

    public void stop() {
    }
}
