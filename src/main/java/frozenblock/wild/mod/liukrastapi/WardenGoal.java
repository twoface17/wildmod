package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.entity.WardenEntity;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class WardenGoal extends Goal {
    public static World lasteventWorld = null;
    public static BlockPos lasteventpos = null;

    public static World lasteventWorld2 = null;
    public static BlockPos lasteventpos2 = null;

    private double VX;
    private double VY;
    private double VZ;

    private boolean ROAR;

    private final MobEntity mob;
    private final double speed;

    public WardenGoal(MobEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    public boolean canStart() {
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

        if(this.mob.getAttacker() != null) {
            LivingEntity TARGET = this.mob.getAttacker();
            this.mob.getNavigation().startMovingTo(VX, VY, VZ, this.speed);
            this.mob.getLookControl().lookAt(VX, VY, VZ);
            attack(TARGET);
        } else {
            this.mob.getNavigation().startMovingTo(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ(), speed);
            this.mob.getLookControl().lookAt(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ());

            LivingEntity TARGET = this.mob.getEntityWorld().getClosestPlayer(this.mob.getX(), this.mob.getY(), this.mob.getZ(), 100, true);
            attack(TARGET);
        }
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
            this.mob.playSound(RegisterSounds.ENTITY_WARDEN_SLIGHTLY_ANGRY, 1f, 1f);
        }
    }
}
