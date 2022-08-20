package net.frozenblock.wildmod.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.misc.WildVec3d;
import net.frozenblock.wildmod.particle.WildVibrationParticleEffect;
import net.frozenblock.wildmod.registry.RegisterCriteria;
import net.frozenblock.wildmod.registry.RegisterTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class VibrationListener implements GameEventListener {
    protected final PositionSource positionSource;
    protected final int range;
    protected final VibrationListener.Callback callback;
    @Nullable
    protected VibrationListener.Vibration vibration;
    protected float distance;
    protected int delay;

    public static Codec<VibrationListener> createCodec(VibrationListener.Callback callback) {
        return RecordCodecBuilder.create(
                instance -> instance.group(
                                PositionSource.CODEC.fieldOf("source").forGetter(listener -> listener.positionSource),
                                Codecs.NONNEGATIVE_INT.fieldOf("range").forGetter(listener -> listener.range),
                                VibrationListener.Vibration.CODEC.optionalFieldOf("event").forGetter(listener -> Optional.ofNullable(listener.vibration)),
                                Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("event_distance").orElse(0.0F).forGetter(listener -> listener.distance),
                                Codecs.NONNEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter(listener -> listener.delay)
                        )
                        .apply(
                                instance,
                                (positionSource, range, vibration, distance, delay) -> new VibrationListener(
                                        positionSource, range, callback, vibration.orElse(null), distance, delay
                                )
                        )
        );
    }

    public VibrationListener(PositionSource positionSource, int range, VibrationListener.Callback callback, @Nullable VibrationListener.Vibration vibration, float distance, int delay) {
        this.positionSource = positionSource;
        this.range = range;
        this.callback = callback;
        this.vibration = vibration;
        this.distance = distance;
        this.delay = delay;
    }

    public void tick(World world) {
        if (world instanceof ServerWorld serverWorld && this.vibration != null) {
            --this.delay;
            if (this.delay <= 0) {
                this.delay = 0;
                this.callback
                        .accept(
                                serverWorld,
                                this,
                                new BlockPos(this.vibration.pos),
                                this.vibration.gameEvent,
                                this.vibration.getEntity(serverWorld).orElse(null),
                                this.vibration.getOwner(serverWorld).orElse(null),
                                this.distance
                        );
                this.vibration = null;
            }
        }

    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
        if (this.vibration != null) {
            return false;
        } else {
            if (!this.callback.canAccept(event, entity)) {
                return false;
            } else {
                Optional<BlockPos> optional = this.positionSource.getPos(world);
                if (optional.isEmpty()) {
                    return false;
                } else {
                    Vec3d vec3d = Vec3d.ofCenter(pos);
                    Vec3d vec3d2 = Vec3d.ofCenter(optional.get());
                    if (!this.callback.accepts((ServerWorld) world, this, new BlockPos(vec3d), event, entity)) {
                        return false;
                    } else if (isOccluded(world, vec3d, vec3d2)) {
                        return false;
                    } else {
                        this.listen((ServerWorld) world, event, entity, vec3d, vec3d2);
                        return true;
                    }
                }
            }
        }
    }

    private void listen(ServerWorld world, GameEvent gameEvent, @Nullable Entity entity, Vec3d start, Vec3d end) {
        this.distance = (float) start.distanceTo(end);
        this.vibration = new VibrationListener.Vibration(gameEvent, this.distance, start, entity);
        this.delay = MathHelper.floor(this.distance);
        world.sendVibrationPacket(new net.minecraft.world.Vibration(new BlockPos(start), this.positionSource, this.delay));
        world.spawnParticles(new WildVibrationParticleEffect(this.positionSource, this.delay), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
        this.callback.onListen();
    }

    private static boolean isOccluded(World world, Vec3d start, Vec3d end) {
        Vec3d vec3d = new Vec3d((double) MathHelper.floor(start.x) + 0.5, (double) MathHelper.floor(start.y) + 0.5, (double) MathHelper.floor(start.z) + 0.5);
        Vec3d vec3d2 = new Vec3d((double) MathHelper.floor(end.x) + 0.5, (double) MathHelper.floor(end.y) + 0.5, (double) MathHelper.floor(end.z) + 0.5);

        for (Direction direction : Direction.values()) {
            Vec3d vec3d3 = WildVec3d.withBias(vec3d, direction, 1.0E-5F);
            if (world.raycast(new BlockStateRaycastContext(vec3d3, vec3d2, state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType()
                    != HitResult.Type.BLOCK) {
                return false;
            }
        }

        return true;
    }

    public interface Callback {
        default TagKey<GameEvent> getTag() {
            return GameEventTags.VIBRATIONS;
        }

        default boolean triggersAvoidCriterion() {
            return false;
        }

        default boolean canAccept(GameEvent gameEvent, @Nullable Entity entity) {
            WildGameEvent.Emitter emitter = WildGameEvent.Emitter.of(entity);
            if (!gameEvent.isIn(this.getTag())) {
                return false;
            } else {
                if (entity != null) {
                    if (entity.isSpectator()) {
                        return false;
                    }

                    if (entity.bypassesSteppingEffects() && gameEvent.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                        if (this.triggersAvoidCriterion() && entity instanceof ServerPlayerEntity serverPlayerEntity) {
                            RegisterCriteria.AVOID_VIBRATION.trigger(serverPlayerEntity);
                        }

                        return false;
                    }

                    if (entity.occludeVibrationSignals()) {
                        return false;
                    }
                }

                if (emitter.affectedState() != null) {
                    return !emitter.affectedState().isIn(RegisterTags.DAMPENS_VIBRATIONS);
                } else {
                    return true;
                }
            }
        }

        /**
         * Returns whether the callback wants to accept this event.
         */
        boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity);

        /**
         * Accepts a game event after delay.
         */
        void accept(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance);

        default void onListen() {
        }
    }

    public record Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable UUID uuid,
                            @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {

        public static final Codec<VibrationListener.Vibration> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Registry.GAME_EVENT.getCodec().fieldOf("game_event").forGetter(VibrationListener.Vibration::gameEvent),
                                Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(VibrationListener.Vibration::distance),
                                WildVec3d.CODEC.fieldOf("pos").forGetter(VibrationListener.Vibration::pos),
                                DynamicSerializableUuid.CODEC.optionalFieldOf("source").forGetter(vibration -> Optional.ofNullable(vibration.uuid())),
                                DynamicSerializableUuid.CODEC.optionalFieldOf("projectile_owner").forGetter(vibration -> Optional.ofNullable(vibration.projectileOwnerUuid()))
                        )
                        .apply(
                                instance,
                                (event, distance, pos, uuid, projectileOwnerUuid) -> new VibrationListener.Vibration(
                                        event, distance, pos, uuid.orElse(null), projectileOwnerUuid.orElse(null)
                                )
                        )
        );

        public Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid) {
            this(gameEvent, distance, pos, uuid, projectileOwnerUuid, null);
        }

        public Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable Entity entity) {
            this(gameEvent, distance, pos, entity == null ? null : entity.getUuid(), getOwnerUuid(entity), entity);
        }

        @Nullable
        private static UUID getOwnerUuid(@Nullable Entity entity) {
            if (entity instanceof ProjectileEntity projectileEntity && projectileEntity.getOwner() != null) {
                return projectileEntity.getOwner().getUuid();
            }

            return null;
        }

        public Optional<Entity> getEntity(ServerWorld world) {
            return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map(world::getEntity));
        }

        public Optional<Entity> getOwner(ServerWorld world) {
            return this.getEntity(world)
                    .filter(entity -> entity instanceof ProjectileEntity)
                    .map(entity -> (ProjectileEntity) entity)
                    .map(ProjectileEntity::getOwner)
                    .or(() -> Optional.ofNullable(this.projectileOwnerUuid).map(world::getEntity));
        }
    }
}
