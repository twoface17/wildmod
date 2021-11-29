package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.entity.WardenEntity;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class WardenGoal extends Goal {

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
        return !this.mob.getNavigation().isIdle();
    }

    public void start() {
        BlockPos lasteventpos = this.mob.lasteventpos;
        World lasteventWorld = this.mob.lasteventworld;
        LivingEntity lastevententity = this.mob.lastevententity;

        if(this.mob.getAttacker() != null) {
            double distance = MathAddon.distance(this.VX, this.VY, this.VZ, this.mob.getX(), this.mob.getY(), this.mob.getZ()) / 2;
            if(distance > 4) {distance = 1;}
            LivingEntity TARGET = this.mob.getAttacker();
            this.mob.getNavigation().startMovingTo(this.VX, this.VY, this.VZ, speed);
            this.mob.getLookControl().lookAt(this.VX, this.VY, this.VZ);
            attack(TARGET);
        } else {
            double distance = MathAddon.distance(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ(), this.mob.getX(), this.mob.getY(), this.mob.getZ()) / 2;
            if(distance > 4) {distance = 1;}
            this.mob.getNavigation().startMovingTo(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ(), speed);
            this.mob.getLookControl().lookAt(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ());

            if(lastevententity != null) {
                attack(lastevententity);
            }
        }
    }

    public void stop() {
    }

    public void attack(LivingEntity target) {
        double distance = MathAddon.distance(target.getX(), target.getY(), target.getZ(), this.mob.getX(), this.mob.getY(), this.mob.getZ());
        if(distance <= 1.5) {
            this.mob.tryAttack(target);
            System.out.println(target);
        }
        if(!target.isAlive()) {
            this.mob.playSound(RegisterSounds.ENTITY_WARDEN_SLIGHTLY_ANGRY, 1f, 1f);
        }
    }
}
