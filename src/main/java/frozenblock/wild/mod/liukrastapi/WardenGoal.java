package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.WardenEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;


public class WardenGoal extends Goal {
    private int cooldown;

    private double VX;
    private double VY;
    private double VZ;

    private boolean ROAR;

    private final WardenEntity mob;
    private final double speed;

    public WardenGoal(WardenEntity mob, double speed) {
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

           if (this.mob.getAttacker() == null) {
               if (lasteventWorld != null && lasteventpos != null) {
                   if (lasteventWorld == this.mob.getEntityWorld()) {
                       double distancex = Math.pow(this.mob.getBlockX() - lasteventpos.getX(), 2);
                       double distancey = Math.pow(this.mob.getBlockY() - lasteventpos.getY(), 2);
                       double distancez = Math.pow(this.mob.getBlockZ() - lasteventpos.getZ(), 2);
                       if (Math.sqrt(distancex + distancey + distancez) < 25) {
                               exit = true;
                               if (this.mob.getSuspicion(this.mob.lastevententity)>=this.mob.getSuspicion(this.mob.navigationEntity)) {
                                   exit=true;
                               }
                               if (this.mob.getTrackingEntity()!=null && this.mob.lastevententity!=this.mob.getTrackingEntity()) {
                                   exit= false;
                               }
                               if (this.mob.getTrackingEntity()!=null && this.mob.lastevententity==this.mob.getTrackingEntity()) {
                                   exit=true;
                               }
                       }
                   }
               }
           } else {
               BlockPos blockPos = this.mob.getAttacker().getBlockPos();
               if (blockPos != null) {
                   this.VX = this.mob.getAttacker().getX();
                   this.VY = this.mob.getAttacker().getY();
                   this.VZ = this.mob.getAttacker().getZ();
               }
               exit = true;
           }
        int r = this.mob.getRoarTicksLeft1();
        if (r > 0) {
            exit = false;
        }
        int s = this.mob.sniffTicksLeft;
        if (s > 0) {
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
        BlockPos lasteventpos = this.mob.lasteventpos;
        LivingEntity lastevententity = this.mob.lastevententity;
            if (this.mob.getAttacker() != null) {
                LivingEntity target = this.mob.getAttacker();
                this.mob.getNavigation().startMovingTo(this.VX, this.VY, this.VZ, speed + (5 * 0.15) + (this.mob.overallAnger() * 0.004));
                double d = (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
                double e = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
                this.cooldown = Math.max(this.cooldown - 1, 0);
                if (!(e > d)) {
                    if (this.cooldown <= 0) {
                        this.cooldown = 20;
                        this.mob.tryAttack(target);
                    }
                }
                this.mob.lastevententity = null;
                this.mob.lasteventpos = null;
                this.mob.lasteventworld = null;
            } else if (lastevententity != null) {
                    double d = (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
                    double e = this.mob.squaredDistanceTo(lastevententity.getX(), lastevententity.getY(), lastevententity.getZ());
                    this.mob.getNavigation().startMovingTo(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ(), (speed + (MathHelper.clamp(this.mob.getSuspicion(lastevententity), 0, 15) * 0.03) + (this.mob.overallAnger() * 0.004)));
                    if (!(e > d)) {
                        this.mob.tryAttack(lastevententity);
                    }
                    this.mob.lastevententity = null;
                    this.mob.lasteventpos = null;
                    this.mob.lasteventworld = null;
            } else {
                this.mob.getNavigation().startMovingTo(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ(), speed + (this.mob.overallAnger() * 0.009));
            }
        }

    public void stop() {
    }
}
