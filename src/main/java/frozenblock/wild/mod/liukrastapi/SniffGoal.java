package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.WardenEntity;
import frozenblock.wild.mod.registry.RegisterEntities;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.List;


public class SniffGoal extends Goal {
    private int cooldown;

    private double VX;
    private double VY;
    private double VZ;

    private boolean ROAR;

    private final WardenEntity mob;
    private final double speed;

    public SniffGoal(WardenEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }


    public boolean canStart() {
        boolean exit = false;
        LivingEntity sniffEntity = null;
        if (this.mob.mostSuspiciousAround()!=null) {
            sniffEntity=this.mob.mostSuspiciousAround();
        }
        if (this.mob.getTrackingEntity()!=null) {
            sniffEntity=this.mob.getTrackingEntity();
        }
        if (sniffEntity==null) {
            LivingEntity closestPlayer = this.mob.getWorld().getClosestPlayer(this.mob, 16);
            sniffEntity=closestPlayer;
            if (closestPlayer==null) {
                Box box = new Box(this.mob.getBlockPos().add(-16,-16,-16), this.mob.getBlockPos().add(16,16,16));
                List<LivingEntity> entities = this.mob.getWorld().getNonSpectatingEntities(LivingEntity.class, box);
                LivingEntity chosen = this.mob.getWorld().getClosestEntity(entities, TargetPredicate.DEFAULT, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
                if (chosen!=null && MathAddon.distance(chosen.getX(), chosen.getY(), chosen.getZ(), this.mob.getX(), this.mob.getY(), this.mob.getZ())<=16) {
                    sniffEntity=chosen;
                }
            }
        }
        if (this.mob.emergeTicksLeft>0) {
            return false;
        }
        if (this.mob.getWorld().getTime()-this.mob.vibrationTimer>160 && sniffEntity!=null) {
            exit = true;
        }

        int r = this.mob.getRoarTicksLeft1();
        if (r > 0) {
            exit = false;
        }

        return exit && UniformIntProvider.create(0,3).get(this.mob.getRandom())>0;
    }

    public boolean shouldContinue() {
        boolean exit = false;
        LivingEntity sniffEntity = null;
        if (this.mob.mostSuspiciousAround()!=null) {
            sniffEntity=this.mob.mostSuspiciousAround();
        }
        if (this.mob.getTrackingEntity()!=null) {
            sniffEntity=this.mob.getTrackingEntity();
        }
        if (sniffEntity==null) {
            LivingEntity closestPlayer = this.mob.getWorld().getClosestPlayer(this.mob, 16);
            sniffEntity=closestPlayer;
            if (closestPlayer==null) {
                Box box = new Box(this.mob.getBlockPos().add(-16,-16,-16), this.mob.getBlockPos().add(16,16,16));
                List<LivingEntity> entities = this.mob.getWorld().getNonSpectatingEntities(LivingEntity.class, box);
                LivingEntity chosen = this.mob.getWorld().getClosestEntity(entities, TargetPredicate.DEFAULT, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
                if (chosen!=null && MathAddon.distance(chosen.getX(), chosen.getY(), chosen.getZ(), this.mob.getX(), this.mob.getY(), this.mob.getZ())<=16) {
                    sniffEntity=chosen;
                }
            }
        }
        if (this.mob.emergeTicksLeft>0) {
            return false;
        }
        if (this.mob.getWorld().getTime()-this.mob.vibrationTimer>160 && sniffEntity!=null) {
            exit = true;
        }

        int r = this.mob.getRoarTicksLeft1();
        if (r > 0) {
            exit = false;
        }

        return exit && UniformIntProvider.create(0,3).get(this.mob.getRandom())>0;
    }

    public void start() {
        LivingEntity sniffEntity = null;
        if (this.mob.mostSuspiciousAround()!=null) {
            sniffEntity=this.mob.mostSuspiciousAround();
        }
        if (this.mob.getTrackingEntity()!=null) {
            sniffEntity=this.mob.getTrackingEntity();
        }
        if (sniffEntity==null) {
            LivingEntity closestPlayer = this.mob.getWorld().getClosestPlayer(this.mob, 16);
            sniffEntity=closestPlayer;
            if (closestPlayer==null) {
                Box box = new Box(this.mob.getBlockPos().add(-16,-16,-16), this.mob.getBlockPos().add(16,16,16));
                List<LivingEntity> entities = this.mob.getWorld().getNonSpectatingEntities(LivingEntity.class, box);
                LivingEntity chosen = this.mob.getWorld().getClosestEntity(entities, TargetPredicate.DEFAULT, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
                if (chosen!=null && MathAddon.distance(chosen.getX(), chosen.getY(), chosen.getZ(), this.mob.getX(), this.mob.getY(), this.mob.getZ())<=16) {
                    sniffEntity=chosen;
                }
            }
        }

        if (sniffEntity!=null) {
            if (MathAddon.distance(sniffEntity.getX(), sniffEntity.getY(), sniffEntity.getZ(), this.mob.getX(), this.mob.getY(), this.mob.getZ()) <= 16) {
                int extraSuspicion = 0;
                this.mob.sniffTicksLeft = 34;
                this.mob.sniffCooldown = 134;
                this.mob.sniffX = sniffEntity.getBlockPos().getX();
                this.mob.sniffY = sniffEntity.getBlockPos().getY();
                this.mob.sniffZ = sniffEntity.getBlockPos().getZ();
                this.mob.vibrationTimer = this.mob.getWorld().getTime();
                this.mob.getWorld().playSound(null, this.mob.getCameraBlockPos(), RegisterSounds.ENTITY_WARDEN_SNIFF, SoundCategory.HOSTILE, 1F, 1F);
                if (this.mob.getBlockPos().getSquaredDistance(sniffEntity.getBlockPos(), true) <= 8) {
                    extraSuspicion = extraSuspicion + 1;
                }
                this.mob.addSuspicion(sniffEntity, UniformIntProvider.create(1, 2).get(this.mob.getRandom()) + extraSuspicion);
                this.mob.leaveTime = this.mob.getWorld().getTime() + 1200;
            }
        }
    }

    public void stop() {
    }
}
