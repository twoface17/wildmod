package frozenblock.wild.mod.liukrastapi;

import com.google.common.collect.ImmutableMap;
import frozenblock.wild.mod.entity.FrogEntity;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class FrogEatEntityTask extends Task<FrogEntity> {
    public static final int field_37479 = 100;
    public static final int field_37480 = 6;
    private static final float field_37481 = 1.75F;
    private static final float field_37482 = 0.75F;
    private int field_37483;
    private int field_37484;
    private final SoundEvent field_37485;
    private final SoundEvent field_37486;
    private Vec3d field_37487;
    private boolean field_37488;
    private FrogEatEntityTask.class_7109 field_37489;

    public FrogEatEntityTask(SoundEvent soundEvent, SoundEvent soundEvent2) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT), 100);
        this.field_37489 = FrogEatEntityTask.class_7109.DONE;
        this.field_37485 = soundEvent;
        this.field_37486 = soundEvent2;
    }

    protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
        return super.shouldRun(serverWorld, frogEntity) && FrogEntity.isValidFrogTarget((LivingEntity)frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get());
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        return frogEntity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.field_37489 != FrogEatEntityTask.class_7109.DONE;
    }

    protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        LivingEntity livingEntity = (LivingEntity)frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        LookTargetUtil.lookAt(frogEntity, livingEntity);
        frogEntity.setFrogTarget(livingEntity);
        frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0F, 0));
        this.field_37484 = 10;
        this.field_37489 = FrogEatEntityTask.class_7109.MOVE_TO_TARGET;
    }

    protected void finishRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        frogEntity.setPose(EntityPose.STANDING);
        serverWorld.playSoundFromEntity((PlayerEntity)null, frogEntity, this.field_37486, SoundCategory.NEUTRAL, 2.0F, 1.0F);
        Optional<Entity> optional = frogEntity.getFrogTarget();
        if (optional.isPresent()) {
            Entity entity = (Entity)optional.get();
            if (this.field_37488 && entity.isAlive()) {
                entity.remove(Entity.RemovalReason.KILLED);
                ItemStack itemStack = createDroppedStack(frogEntity, entity);
                serverWorld.spawnEntity(new ItemEntity(serverWorld, this.field_37487.getX(), this.field_37487.getY(), this.field_37487.getZ(), itemStack));
            }
        }

        frogEntity.clearFrogTarget();
        this.field_37488 = false;
    }

   private static ItemStack createDroppedStack(FrogEntity frog, Entity eatenEntity) {
        if (eatenEntity instanceof MagmaCubeEntity) {
            Item var10002 = switch (frog.getVariant()) {
                case TEMPERATE -> RegisterBlocks.OCHRE_FROGLIGHT.asItem();
                case WARM -> RegisterBlocks.PEARLESCENT_FROGLIGHT.asItem();
                case COLD -> RegisterBlocks.VERDANT_FROGLIGHT.asItem();
                default -> throw new IncompatibleClassChangeError();

            };
            return new ItemStack(var10002);
        } else {
            return new ItemStack(Items.SLIME_BALL);
        }
    }

    protected void keepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        LivingEntity livingEntity = (LivingEntity)frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        frogEntity.setFrogTarget(livingEntity);
        switch(this.field_37489) {
            case MOVE_TO_TARGET:
                if (livingEntity.distanceTo(frogEntity) < 1.75F) {
                    serverWorld.playSoundFromEntity((PlayerEntity)null, frogEntity, this.field_37485, SoundCategory.NEUTRAL, 2.0F, 1.0F);
                    //frogEntity.setPose(EntityPose.USING_TONGUE);
                    livingEntity.setVelocity(livingEntity.getPos().relativize(frogEntity.getPos()).normalize().multiply(0.75D));
                    this.field_37487 = livingEntity.getPos();
                    this.field_37483 = 6;
                    this.field_37489 = FrogEatEntityTask.class_7109.EAT_ANIMATION;
                } else if (this.field_37484 <= 0) {
                    frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0F, 0));
                    this.field_37484 = 10;
                } else {
                    --this.field_37484;
                }
                break;
            case EAT_ANIMATION:
                if (this.field_37483 <= 0) {
                    this.field_37488 = true;
                    this.field_37489 = FrogEatEntityTask.class_7109.DONE;
                } else {
                    --this.field_37483;
                }
            case DONE:
        }

    }

    private static enum class_7109 {
        MOVE_TO_TARGET,
        EAT_ANIMATION,
        DONE;

        private class_7109() {
        }
    }
}
