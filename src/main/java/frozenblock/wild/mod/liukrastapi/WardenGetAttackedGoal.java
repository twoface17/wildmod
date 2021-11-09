package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WardenGetAttackedGoal extends Goal {

    private double VX;
    private double VY;
    private double VZ;

    private boolean ROAR;

    private final MobEntity mob;
    private final double speed;

    public WardenGetAttackedGoal(MobEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    public boolean canStart() {
        if(this.mob.getAttacker() == null) {
            return false;
        } else {
            BlockPos blockPos = this.mob.getAttacker().getBlockPos();
            if(blockPos != null) {
                this.VX = this.mob.getAttacker().getX();
                this.VY = this.mob.getAttacker().getY();
                this.VZ = this.mob.getAttacker().getZ();
            }
            if(this.mob.getAttacker().isSneaking()) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    public void start() {
        LivingEntity TARGET = this.mob.getAttacker();
        this.mob.getNavigation().startMovingTo(VX, VY, VZ, this.speed);
        this.mob.getLookControl().lookAt(VX, VY, VZ);
        attack(TARGET);
    }

    public void stop() {
    }

    public void attack(LivingEntity target) {
        World world = this.mob.getEntityWorld();
        BlockPos pos = this.mob.getBlockPos();
        double distance = this.mob.squaredDistanceTo(VX, VY, VZ);
        if(distance <= 10) {
            this.mob.tryAttack(target);
        }
        if(!target.isAlive()) {
            this.mob.playSound(RegisterSounds.SHRIEKER_EVENT, 1f, 1f);
        }
    }


}
