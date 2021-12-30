package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.entity.WardenEntity;
import frozenblock.wild.mod.registry.RegisterSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Vibration;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

import java.util.Optional;

public class WardenGoal extends Goal {
    private int cooldown;

    private double VX;
    private double VY;
    private double VZ;

    private boolean ROAR;

    private final WardenEntity mob;
    private final double speed;
    protected int delay = 0;
    protected int distance;

    public WardenGoal(WardenEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }
        public void CreateVibration(World world, BlockPos blockPos, PositionSource positionSource, BlockPos blockPos2) {
        this.delay = this.distance = (int)Math.floor(Math.sqrt(blockPos.getSquaredDistance(blockPos2, false))) * 2 ;
        ((ServerWorld)world).sendVibrationPacket(new Vibration(blockPos, positionSource, this.delay));
    }

    public boolean canStart() {
        boolean exit = false;
        BlockPos lasteventpos = this.mob.lasteventpos;
        World lasteventWorld = this.mob.lasteventworld;
        int eventIdentifier = this.mob.eventIdentifier;

        if(this.mob.getAttacker() == null) {
            if(lasteventWorld != null && lasteventpos != null) {
                if(lasteventWorld == this.mob.getEntityWorld()) {
                    double distancex = Math.pow(this.mob.getBlockX() - lasteventpos.getX(), 2);
                    double distancey = Math.pow(this.mob.getBlockY() - lasteventpos.getY(), 2);
                    double distancez = Math.pow(this.mob.getBlockZ() - lasteventpos.getZ(), 2);
                    if(Math.sqrt(distancex + distancey + distancez) < 15) {
                    BlockPos WardenHead = this.mob.getBlockPos().up((int) 3);
                    PositionSource wardenPositionSource = new PositionSource() {
                            @Override
                            public Optional<BlockPos> getPos(World world) {
                                return Optional.of(WardenHead);
                            }

                            @Override
                            public PositionSourceType<?> getType() {
                                return null;
                            }
                        };
                        this.mob.vibrationIdentifier=this.mob.vibrationIdentifier+1;
                        if(this.mob.vibrationIdentifier==this.mob.prevVibrationIdentifier+1) {
                            CreateVibration(lasteventWorld, lasteventpos, wardenPositionSource, WardenHead);
                            //And there you go! Vibrations! If it's too high, just change BlockPos WardenHead to .up((int) 2) instead of 3.
                            this.mob.eventIdentifier = eventIdentifier + 1;
                        }
                        exit = true;
                    }
                }
            }
        } else {
            BlockPos blockPos = this.mob.getAttacker().getBlockPos();
            if(blockPos != null) {
                this.VX = this.mob.getAttacker().getX();
                this.VY = this.mob.getAttacker().getY();
                this.VZ = this.mob.getAttacker().getZ();
            }
            exit = true;
        }

        if(this.mob.getNavigation().isIdle()) {
            if(Math.random() > 0.5) {
                this.mob.roar();
                exit = false;
            }
        }

        int r = this.mob.getRoarTicksLeft1();
        if(r > 0) {
            exit = false;
        }

        return exit;
    }

    public boolean shouldContinue() {
        boolean exit;
        exit = !this.mob.getNavigation().isIdle();

        return exit;
    }

    public void start() {
        BlockPos lasteventpos = this.mob.lasteventpos;
        LivingEntity lastevententity = this.mob.lastevententity;
        if(this.mob.getAttacker() != null) {
            double distance = MathAddon.distance(this.VX, this.VY, this.VZ, this.mob.getX(), this.mob.getY(), this.mob.getZ()) / 2;
            if(distance < 2) {distance = 2;}
            double finalspeed = (speed * 2) / (distance -1);
            if(finalspeed < speed) {finalspeed = speed;}

            LivingEntity target = this.mob.getAttacker();
            this.mob.getNavigation().startMovingTo(this.VX, this.VY, this.VZ, finalspeed);
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
        } else {

            //Calculating a good speed for the warden depending on his distance from the event.
            double distance = MathAddon.distance(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ(), this.mob.getX(), this.mob.getY(), this.mob.getZ()) / 2;
            if(distance < 2) {distance = 2;}
            double finalspeed = (speed * 2) / (distance - 1);
            if(finalspeed < speed) {finalspeed = speed;}

            //start moving the warden to the location

            this.mob.getNavigation().startMovingTo(lasteventpos.getX(), lasteventpos.getY(), lasteventpos.getZ(), finalspeed);

            if(lastevententity != null) {
                double d = (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F);
                double e = this.mob.squaredDistanceTo(lastevententity.getX(), lastevententity.getY(), lastevententity.getZ());
                this.cooldown = Math.max(this.cooldown - 1, 0);
                if (!(e > d)) {
                    /*if (this.cooldown <= 0) {
                        this.cooldown = 20;
                        this.mob.tryAttack(lastevententity);
                    }*/
                    this.mob.tryAttack(lastevententity);
                }

                this.mob.lastevententity = null;
                this.mob.lasteventpos = null;
                this.mob.lasteventworld = null;
            }

        }
    }

    public void stop() {
    }
}
