package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.WardenEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
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
        BlockPos lasteventpos = this.mob.lasteventpos;
        World lasteventWorld = this.mob.lasteventworld;

        if(this.mob.getAttacker() == null) {
            if(lasteventWorld != null && lasteventpos != null) {
                if(lasteventWorld == this.mob.getEntityWorld()) {
                    double distancex = Math.pow(this.mob.getBlockX() - lasteventpos.getX(), 2);
                    double distancey = Math.pow(this.mob.getBlockY() - lasteventpos.getY(), 2);
                    double distancez = Math.pow(this.mob.getBlockZ() - lasteventpos.getZ(), 2);
                    if(Math.sqrt(distancex + distancey + distancez) < 15) {
                        return true;
                    } else {return false;}
                } else {return false;}
            } else {return false;}
        } else {
            BlockPos blockPos = this.mob.getAttacker().getBlockPos();
            if(blockPos != null) {
                this.VX = this.mob.getAttacker().getX();
                this.VY = this.mob.getAttacker().getY();
                this.VZ = this.mob.getAttacker().getZ();
            }
            return true;
        }
    }

    public boolean shouldContinue() {
        boolean exit;
        exit = !this.mob.getNavigation().isIdle();
        if(Math.random() < 0.1) {


            exit = false;
        }

        return exit;
    }

    public void start() {
        BlockPos lasteventpos = this.mob.lasteventpos;
        World lasteventWorld = this.mob.lasteventworld;
        LivingEntity lastevententity = this.mob.lastevententity;

        if(this.mob.getAttacker() != null) {
            double distance = MathAddon.distance(this.VX, this.VY, this.VZ, this.mob.getX(), this.mob.getY(), this.mob.getZ()) / 2;
            if(distance > 4) {distance = 1;}
            LivingEntity target = this.mob.getAttacker();
            this.mob.getNavigation().startMovingTo(this.VX, this.VY, this.VZ, speed);
            //this.mob.getLookControl().lookAt(this.VX, this.VY, this.VZ);
            double d = (double)(this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
            double e = this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
            this.cooldown = Math.max(this.cooldown - 1, 0);
            if (!(e > d)) {
                if (this.cooldown <= 0) {
                    this.cooldown = 20;
                    this.mob.tryAttack(target);
                }
            }
        } else {
            double distance = MathAddon.distance(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ(), this.mob.getX(), this.mob.getY(), this.mob.getZ()) / 2;
            if(distance > 4) {distance = 1;}
            this.mob.getNavigation().startMovingTo(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ(), speed);
            //this.mob.getLookControl().lookAt(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ());

            if(lastevententity != null) {
                double d = (double)(this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
                double e = this.mob.squaredDistanceTo(lastevententity.getX(), lastevententity.getY(), lastevententity.getZ());
                this.cooldown = Math.max(this.cooldown - 1, 0);
                if (!(e > d)) {
                    if (this.cooldown <= 0) {
                        this.cooldown = 20;
                        this.mob.tryAttack(lastevententity);
                    }
                }
            }

        }
    }

    public void stop() {
    }
}
