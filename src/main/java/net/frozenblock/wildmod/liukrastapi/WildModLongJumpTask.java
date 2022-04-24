package net.frozenblock.wildmod.liukrastapi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WildModLongJumpTask<E extends MobEntity> extends Task<E> {
    protected static final int MAX_COOLDOWN = 20;
    private static final int TARGET_RETAIN_TIME = 40;
    protected static final int PATHING_DISTANCE = 8;
    private static final int RUN_TIME = 200;
    private static final List<Integer> field_37431 = Lists.newArrayList(65, 70, 75, 80);
    private final UniformIntProvider cooldownRange;
    protected final int verticalRange;
    protected final int horizontalRange;
    protected final float maxRange;
    protected List targets;
    protected Optional<Vec3d> lastPos;
    @Nullable
    protected Vec3d lastTarget;
    protected int cooldown;
    protected long targetTime;
    private final Function<E, SoundEvent> entityToSound;
    private final Predicate<BlockState> jumpToPredicate;

    public WildModLongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function<E, SoundEvent> entityToSound) {
        this(cooldownRange, verticalRange, horizontalRange, maxRange, entityToSound, (state) -> false);
    }

    public WildModLongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function<E, SoundEvent> entityToSound, Predicate<BlockState> jumpToPredicate) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LONG_JUMP_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT), 200);
        this.targets = Lists.newArrayList();
        this.lastPos = Optional.empty();
        this.cooldownRange = cooldownRange;
        this.verticalRange = verticalRange;
        this.horizontalRange = horizontalRange;
        this.maxRange = maxRange;
        this.entityToSound = entityToSound;
        this.jumpToPredicate = jumpToPredicate;
    }

    protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
        boolean bl = mobEntity.isOnGround() && !mobEntity.isTouchingWater() && !mobEntity.isInLava() && !serverWorld.getBlockState(mobEntity.getBlockPos()).isOf(Blocks.HONEY_BLOCK);
        if (!bl) {
            mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(serverWorld.random) / 2);
        }

        return bl;
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        boolean bl = this.lastPos.isPresent() && this.lastPos.get().equals(mobEntity.getPos()) && this.cooldown > 0 && !mobEntity.isInsideWaterOrBubbleColumn() && (this.lastTarget != null || !this.targets.isEmpty());
        if (!bl && mobEntity.getBrain().getOptionalMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
            mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(serverWorld.random) / 2);
            mobEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        }

        return bl;
    }

    protected void run(ServerWorld serverWorld, E mobEntity, long l) {
        this.lastTarget = null;
        this.cooldown = 20;
        this.lastPos = Optional.of(mobEntity.getPos());
        BlockPos blockPos = mobEntity.getBlockPos();
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        this.targets = BlockPos.stream(i - this.horizontalRange, j - this.verticalRange, k - this.horizontalRange, i + this.horizontalRange, j + this.verticalRange, k + this.horizontalRange).filter((blockPos2) -> !blockPos2.equals(blockPos)).map((blockPos2) -> new Target(blockPos2.toImmutable(), MathHelper.ceil(blockPos.getSquaredDistance(blockPos2)))).collect(Collectors.toCollection(Lists::newArrayList));
    }

    protected void keepRunning(ServerWorld serverWorld, E mobEntity, long l) {
        if (this.lastTarget != null) {
            if (l - this.targetTime >= 40L) {
                mobEntity.setYaw(mobEntity.bodyYaw);
                mobEntity.setNoDrag(true);
                double d = this.lastTarget.length();
                double e = d + mobEntity.getJumpBoostVelocityModifier();
                mobEntity.setVelocity(this.lastTarget.multiply(e / d));
                mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_MID_JUMP, true);
                serverWorld.playSoundFromEntity(null, mobEntity, this.entityToSound.apply(mobEntity), SoundCategory.NEUTRAL, 1.0F, 1.0F);
            }
        } else {
            --this.cooldown;
            this.method_41342(serverWorld, mobEntity, l);
        }

    }

    protected void method_41342(ServerWorld serverWorld, E mobEntity, long l) {
        while(true) {
            if (!this.targets.isEmpty()) {
                Optional<Target> optional = this.getTarget(serverWorld);
                if (optional.isEmpty()) {
                    continue;
                }

                WildModLongJumpTask.Target target = optional.get();
                BlockPos blockPos = target.getPos();
                if (!this.canJumpTo(serverWorld, mobEntity, blockPos)) {
                    continue;
                }

                Vec3d vec3d = Vec3d.ofCenter(blockPos);
                Vec3d vec3d2 = this.getRammingVelocity(mobEntity, vec3d);
                if (vec3d2 == null) {
                    continue;
                }

                mobEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(blockPos));
                EntityNavigation entityNavigation = mobEntity.getNavigation();
                Path path = entityNavigation.findPathTo(blockPos, 0, 8);
                if (path != null && path.reachesTarget()) {
                    continue;
                }

                this.lastTarget = vec3d2;
                this.targetTime = l;
                return;
            }

            return;
        }
    }

    protected Optional<Target> getTarget(ServerWorld world) {
        Optional<Target> optional = Weighting.getRandom(world.random, this.targets);
        List var10001 = this.targets;
        Objects.requireNonNull(var10001);
        optional.ifPresent(var10001::remove);
        return optional;
    }

    protected boolean canJumpTo(ServerWorld world, E entity, BlockPos pos) {
        BlockPos blockPos = entity.getBlockPos();
        int i = blockPos.getX();
        int j = blockPos.getZ();
        if (i == pos.getX() && j == pos.getZ()) {
            return false;
        } else if (!entity.getNavigation().isValidPosition(pos) && !this.jumpToPredicate.test(world.getBlockState(pos.down()))) {
            return false;
        } else {
            return entity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(entity.world, pos.mutableCopy())) == 0.0F;
        }
    }

    @Nullable
    protected Vec3d getRammingVelocity(MobEntity entity, Vec3d pos) {
        List<Integer> list = Lists.newArrayList(field_37431);
        Collections.shuffle(list);
        Iterator<Integer> var4 = list.iterator();

        Vec3d vec3d;
        do {
            if (!var4.hasNext()) {
                return null;
            }

            int i = var4.next();
            vec3d = this.getRammingVelocity(entity, pos, i);
        } while(vec3d == null);

        return vec3d;
    }

    @Nullable
    private Vec3d getRammingVelocity(MobEntity entity, Vec3d pos, int range) {
        Vec3d vec3d = entity.getPos();
        Vec3d vec3d2 = (new Vec3d(pos.x - vec3d.x, 0.0D, pos.z - vec3d.z)).normalize().multiply(0.5D);
        pos = pos.subtract(vec3d2);
        Vec3d vec3d3 = pos.subtract(vec3d);
        float f = (float)range * 3.1415927F / 180.0F;
        double d = Math.atan2(vec3d3.z, vec3d3.x);
        double e = vec3d3.subtract(0.0D, vec3d3.y, 0.0D).lengthSquared();
        double g = Math.sqrt(e);
        double h = vec3d3.y;
        double i = Math.sin(2.0F * f);
        double j = 0.08D;
        double k = Math.pow(Math.cos(f), 2.0D);
        double l = Math.sin(f);
        double m = Math.cos(f);
        double n = Math.sin(d);
        double o = Math.cos(d);
        double p = e * 0.08D / (g * i - 2.0D * h * k);
        if (p < 0.0D) {
            return null;
        } else {
            double q = Math.sqrt(p);
            if (q > (double)this.maxRange) {
                return null;
            } else {
                double r = q * m;
                double s = q * l;
                int t = MathHelper.ceil(g / r) * 2;
                double u = 0.0D;
                Vec3d vec3d4 = null;

                for(int v = 0; v < t - 1; ++v) {
                    u += g / (double)t;
                    double w = l / m * u - Math.pow(u, 2.0D) * 0.08D / (2.0D * p * Math.pow(m, 2.0D));
                    double x = u * o;
                    double y = u * n;
                    Vec3d vec3d5 = new Vec3d(vec3d.x + x, vec3d.y + w, vec3d.z + y);
                    if (vec3d4 != null && !this.canReach(entity, vec3d4, vec3d5)) {
                        return null;
                    }

                    vec3d4 = vec3d5;
                }

                return (new Vec3d(r * o, s, r * n)).multiply(0.949999988079071D);
            }
        }
    }

    private boolean canReach(MobEntity entity, Vec3d startPos, Vec3d endPos) {
        EntityDimensions entityDimensions = entity.getDimensions(EntityPose.LONG_JUMPING);
        Vec3d vec3d = endPos.subtract(startPos);
        double d = Math.min(entityDimensions.width, entityDimensions.height);
        int i = MathHelper.ceil(vec3d.length() / d);
        Vec3d vec3d2 = vec3d.normalize();
        Vec3d vec3d3 = startPos;

        for(int j = 0; j < i; ++j) {
            vec3d3 = j == i - 1 ? endPos : vec3d3.add(vec3d2.multiply(d * 0.8999999761581421D));
            Box box = entityDimensions.getBoxAt(vec3d3);
            if (!entity.world.isSpaceEmpty(entity, box)) {
                return false;
            }
        }

        return true;
    }

    public static class Target extends Weighted.Absent {
        private final BlockPos pos;

        public Target(BlockPos pos, int weight) {
            super(weight);
            this.pos = pos;
        }

        public BlockPos getPos() {
            return this.pos;
        }
    }
}

