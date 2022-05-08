package net.frozenblock.wildmod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.FrogEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class FrogEatEntityTask extends Task<FrogEntity> {
    public static final int RUN_TIME = 100;
    public static final int CATCH_DURATION = 6;
    public static final int EAT_DURATION = 10;
    private static final float MAX_DISTANCE = 1.75F;
    private static final float VELOCITY_MULTIPLIER = 0.75F;
    private int eatTick;
    private int moveToTargetTick;
    private final SoundEvent tongueSound;
    private final SoundEvent eatSound;
    private Vec3d targetPos;
    private FrogEatEntityTask.Phase phase = FrogEatEntityTask.Phase.DONE;

    public FrogEatEntityTask(SoundEvent tongueSound, SoundEvent eatSound) {
        super(
                ImmutableMap.of(
                        MemoryModuleType.WALK_TARGET,
                        MemoryModuleState.VALUE_ABSENT,
                        MemoryModuleType.LOOK_TARGET,
                        MemoryModuleState.REGISTERED,
                        MemoryModuleType.ATTACK_TARGET,
                        MemoryModuleState.VALUE_PRESENT
                ),
                100
        );
        this.tongueSound = tongueSound;
        this.eatSound = eatSound;
    }

    protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
        return super.shouldRun(serverWorld, frogEntity)
                && FrogEntity.isValidFrogFood((LivingEntity)frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get())
                && frogEntity.getPose() != WildMod.CROAKING;
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        return frogEntity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.phase != FrogEatEntityTask.Phase.DONE;
    }

    protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        LivingEntity livingEntity = (LivingEntity)frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        LookTargetUtil.lookAt(frogEntity, livingEntity);
        frogEntity.setFrogTarget(livingEntity);
        frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0F, 0));
        this.moveToTargetTick = 10;
        this.phase = FrogEatEntityTask.Phase.MOVE_TO_TARGET;
    }

    protected void finishRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        frogEntity.setPose(EntityPose.STANDING);
    }

    private void eat(ServerWorld world, FrogEntity frog) {
        world.playSoundFromEntity(null, frog, this.eatSound, SoundCategory.NEUTRAL, 2.0F, 1.0F);
        Optional<Entity> optional = frog.getFrogTarget();
        if (optional.isPresent()) {
            Entity entity = (Entity)optional.get();
            if (entity.isAlive()) {
                frog.tryAttack(entity);
                if (!entity.isAlive()) {
                    entity.remove(Entity.RemovalReason.KILLED);
                }
            }
        }

        frog.clearFrogTarget();
    }

    protected void keepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        LivingEntity livingEntity = frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        frogEntity.setFrogTarget(livingEntity);
        switch(this.phase) {
            case MOVE_TO_TARGET:
                if (livingEntity.distanceTo(frogEntity) < 1.75F) {
                    serverWorld.playSoundFromEntity(null, frogEntity, this.tongueSound, SoundCategory.NEUTRAL, 2.0F, 1.0F);
                    frogEntity.setPose(WildMod.USING_TONGUE);
                    livingEntity.setVelocity(livingEntity.getPos().relativize(frogEntity.getPos()).normalize().multiply(0.75));
                    this.targetPos = livingEntity.getPos();
                    this.eatTick = 0;
                    this.phase = FrogEatEntityTask.Phase.CATCH_ANIMATION;
                } else if (this.moveToTargetTick <= 0) {
                    frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0F, 0));
                    this.moveToTargetTick = 10;
                } else {
                    --this.moveToTargetTick;
                }
                break;
            case CATCH_ANIMATION:
                if (this.eatTick++ >= 6) {
                    this.phase = FrogEatEntityTask.Phase.EAT_ANIMATION;
                    this.eat(serverWorld, frogEntity);
                }
                break;
            case EAT_ANIMATION:
                if (this.eatTick >= 10) {
                    this.phase = FrogEatEntityTask.Phase.DONE;
                } else {
                    ++this.eatTick;
                }
            case DONE:
        }

    }

    static enum Phase {
        MOVE_TO_TARGET,
        CATCH_ANIMATION,
        EAT_ANIMATION,
        DONE;

        private Phase() {
        }
    }
}
