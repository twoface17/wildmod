package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.liukrastapi.WildGoat;
import net.frozenblock.wildmod.registry.RegisterTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.RamImpactTask;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

@Mixin(RamImpactTask.class)
public abstract class RamImpactTaskMixin<E extends PathAwareEntity> {

    @Shadow @Final private TargetPredicate targetPredicate;

    @Shadow @Final private ToDoubleFunction<E> strengthMultiplierFactory;

    protected RamImpactTaskMixin(Function<E, SoundEvent> hornBreakSoundFactory) {
        this.hornBreakSoundFactory = hornBreakSoundFactory;
    }

    @Shadow protected abstract void finishRam(ServerWorld world, E entity);

    @Shadow @Final private Function<E, SoundEvent> soundFactory;

    @Shadow private Vec3d direction;
    private final Function<E, SoundEvent> hornBreakSoundFactory;

    /**
     * @author FrozenBlock
     * @reason goat horn drops
     */
    @Overwrite
    public void keepRunning(ServerWorld serverWorld, E goatEntity, long l) {
        List<LivingEntity> list = serverWorld.getTargets(LivingEntity.class, this.targetPredicate, goatEntity, goatEntity.getBoundingBox());
        Brain<?> brain = goatEntity.getBrain();
        if (!list.isEmpty()) {
            LivingEntity livingEntity = list.get(0);
            livingEntity.damage(DamageSource.mob(goatEntity).setNeutral(), (float)goatEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
            int i = goatEntity.hasStatusEffect(StatusEffects.SPEED) ? goatEntity.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1 : 0;
            int j = goatEntity.hasStatusEffect(StatusEffects.SLOWNESS) ? goatEntity.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier() + 1 : 0;
            float f = 0.25F * (float)(i - j);
            float g = MathHelper.clamp(goatEntity.getMovementSpeed() * 1.65F, 0.2F, 3.0F) + f;
            float h = livingEntity.blockedByShield(DamageSource.mob(goatEntity)) ? 0.5F : 1.0F;
            livingEntity.takeKnockback((double)(h * g) * this.strengthMultiplierFactory.applyAsDouble(goatEntity), this.direction.getX(), this.direction.getZ());
            this.finishRam(serverWorld, goatEntity);
            serverWorld.playSoundFromEntity(null, goatEntity, (SoundEvent)this.soundFactory.apply(goatEntity), SoundCategory.HOSTILE, 1.0F, 1.0F);
        } else if (this.shouldSnapHorn(serverWorld, goatEntity)) {
            serverWorld.playSoundFromEntity(null, goatEntity, (SoundEvent)this.soundFactory.apply(goatEntity), SoundCategory.HOSTILE, 1.0F, 1.0F);
            boolean bl = ((WildGoat)goatEntity).dropHorn();
            if (bl) {
                serverWorld.playSoundFromEntity(null, goatEntity, (SoundEvent)this.hornBreakSoundFactory.apply(goatEntity), SoundCategory.HOSTILE, 1.0F, 1.0F);
            }

            this.finishRam(serverWorld, goatEntity);
        } else {
            Optional<WalkTarget> optional = brain.getOptionalMemory(MemoryModuleType.WALK_TARGET);
            Optional<Vec3d> optional2 = brain.getOptionalMemory(MemoryModuleType.RAM_TARGET);
            boolean bl2 = !optional.isPresent()
                    || !optional2.isPresent()
                    || optional.get().getLookTarget().getPos().isInRange((Position)optional2.get(), 0.25);
            if (bl2) {
                this.finishRam(serverWorld, goatEntity);
            }
        }

    }

    private boolean shouldSnapHorn(ServerWorld world, E goat) {
        Vec3d vec3d = goat.getVelocity().multiply(1.0, 0.0, 1.0).normalize();
        BlockPos blockPos = new BlockPos(goat.getPos().add(vec3d));
        return world.getBlockState(blockPos).isIn(RegisterTags.SNAPS_GOAT_HORN) || world.getBlockState(blockPos.up()).isIn(RegisterTags.SNAPS_GOAT_HORN);
    }
}
