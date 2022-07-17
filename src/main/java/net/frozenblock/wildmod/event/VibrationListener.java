//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.frozenblock.wildmod.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.liukrastapi.TickCriterion;
import net.frozenblock.wildmod.liukrastapi.WildVec3d;
import net.frozenblock.wildmod.particle.WildVibrationParticleEffect;
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
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class VibrationListener implements net.frozenblock.wildmod.event.GameEventListener {
    protected final PositionSource positionSource;
    protected final int range;
    protected final Callback callback;
    @Nullable
    protected Vibration vibration;
    protected float distance;
    protected int delay;

    public static Codec<VibrationListener> createCodec(Callback callback) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(PositionSource.CODEC.fieldOf("source").forGetter((listener) -> {
                return listener.positionSource;
            }), Codecs.NONNEGATIVE_INT.fieldOf("range").forGetter((listener) -> {
                return listener.range;
            }), VibrationListener.Vibration.CODEC.optionalFieldOf("event").forGetter((listener) -> {
                return Optional.ofNullable(listener.vibration);
            }), Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("event_distance").orElse(0.0F).forGetter((listener) -> {
                return listener.distance;
            }), Codecs.NONNEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter((listener) -> {
                return listener.delay;
            })).apply(instance, ((positionSource, range, vibration, distance, delay) -> {
                return new VibrationListener(positionSource, range, callback, vibration.orElse(null), distance, delay);
            }));
        });
    }

    public VibrationListener(PositionSource positionSource, int range, Callback callback, @Nullable Vibration vibration, float distance, int delay) {
        this.positionSource = positionSource;
        this.range = range;
        this.callback = callback;
        this.vibration = vibration;
        this.distance = distance;
        this.delay = delay;
    }

    public void tick(World world) {
        if (world instanceof ServerWorld serverWorld) {
            if (this.vibration != null) {
                --this.delay;
                if (this.delay <= 0) {
                    this.delay = 0;
                    this.callback.accept(serverWorld, this, new BlockPos(this.vibration.pos), this.vibration.gameEvent, this.vibration.getEntity(serverWorld).orElse(null), this.vibration.getOwner(serverWorld).orElse(null), this.distance);
                    this.vibration = null;
                }
            }
        }

    }

    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    public int getRange() {
        return this.range;
    }

    public boolean listen(ServerWorld world, WildGameEvents.Message event) {
        if (this.vibration != null) {
            return false;
        } else {
            WildGameEvents gameEvent = event.getEvent();
            WildGameEvents.Emitter emitter = event.getEmitter();
            if (!this.callback.canAccept(gameEvent, emitter)) {
                return false;
            } else {
                Optional<BlockPos> optional = this.positionSource.getPos(world);
                if (optional.isEmpty()) {
                    return false;
                } else {
                    WildVec3d vec3d = event.getEmitterPos();
                    WildVec3d vec3d2 = WildVec3d.ofCenter(optional.get());
                    if (!this.callback.accepts(world, this, new BlockPos(vec3d), gameEvent, emitter)) {
                        return false;
                    } else if (isOccluded(world, vec3d, vec3d2)) {
                        return false;
                    } else {
                        this.listen(world, gameEvent, emitter, vec3d, vec3d2);
                        return true;
                    }
                }
            }
        }
    }

    private void listen(ServerWorld world, GameEvent gameEvent, WildGameEvents.Emitter emitter, WildVec3d start, Vec3d end) {
        this.distance = (float) start.distanceTo(end);
        this.vibration = new Vibration(gameEvent, this.distance, start, emitter.sourceEntity());
        this.delay = MathHelper.floor(this.distance);
        world.spawnParticles(new WildVibrationParticleEffect(this.positionSource, this.delay), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
        this.callback.onListen();
    }

    private static boolean isOccluded(World world, WildVec3d start, WildVec3d end) {
        WildVec3d vec3d = new WildVec3d((double) MathHelper.floor(start.x) + 0.5, (double) MathHelper.floor(start.y) + 0.5, (double) MathHelper.floor(start.z) + 0.5);
        WildVec3d vec3d2 = new WildVec3d((double) MathHelper.floor(end.x) + 0.5, (double) MathHelper.floor(end.y) + 0.5, (double) MathHelper.floor(end.z) + 0.5);
        Direction[] var5 = Direction.values();
        int var6 = var5.length;

        for (int var7 = 0; var7 < var6; ++var7) {
            Direction direction = var5[var7];
            Vec3d vec3d3 = vec3d.withBias(direction, 9.999999747378752E-6);
            if (world.raycast(new BlockStateRaycastContext(vec3d3, vec3d2, (state) -> {
                return state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS);
            })).getType() != HitResult.Type.BLOCK) {
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

        default boolean canAccept(GameEvent gameEvent, WildGameEvents.Emitter emitter) {
            if (!gameEvent.isIn(this.getTag())) {
                return false;
            } else {
                Entity entity = emitter.sourceEntity();
                if (entity != null) {
                    if (entity.isSpectator()) {
                        return false;
                    }

                    if (entity.bypassesSteppingEffects() && gameEvent.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                        if (this.triggersAvoidCriterion() && entity instanceof ServerPlayerEntity) {
                            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
                            TickCriterion.AVOID_VIBRATION.trigger(serverPlayerEntity);
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
        boolean accepts(ServerWorld world, net.frozenblock.wildmod.event.GameEventListener listener, BlockPos pos, GameEvent event, WildGameEvents.Emitter emitter);

        /**
         * Accepts a game event after delay.
         */
        void accept(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance);

        default void onListen() {
        }
    }

    public record Vibration(GameEvent gameEvent, float distance, WildVec3d pos, @Nullable UUID uuid,
                            @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {

        public static final Codec<Vibration> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Registry.GAME_EVENT.getCodec().fieldOf("game_event").forGetter(Vibration::gameEvent), Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(Vibration::distance), WildVec3d.CODEC.fieldOf("pos").forGetter(Vibration::pos), DynamicSerializableUuid.CODEC.optionalFieldOf("source").forGetter((vibration) -> {
                return Optional.ofNullable(vibration.uuid());
            }), DynamicSerializableUuid.CODEC.optionalFieldOf("projectile_owner").forGetter((vibration) -> {
                return Optional.ofNullable(vibration.projectileOwnerUuid());
            })).apply(instance, ((event, distance, pos, uuid, projectileOwnerUuid) -> {
                return new Vibration(event, distance, pos, uuid.orElse(null), projectileOwnerUuid.orElse(null));
            }));
        });

        public Vibration(GameEvent gameEvent, float distance, WildVec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid) {
            this(gameEvent, distance, pos, uuid, projectileOwnerUuid, null);
        }

        public Vibration(GameEvent gameEvent, float distance, WildVec3d pos, @Nullable Entity entity) {
            this(gameEvent, distance, pos, entity == null ? null : entity.getUuid(), getOwnerUuid(entity), entity);
        }

        public Vibration(GameEvent gameEvent, float distance, WildVec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {
            this.gameEvent = gameEvent;
            this.distance = distance;
            this.pos = pos;
            this.uuid = uuid;
            this.projectileOwnerUuid = projectileOwnerUuid;
            this.entity = entity;
        }

        @Nullable
        private static UUID getOwnerUuid(@Nullable Entity entity) {
            if (entity instanceof ProjectileEntity projectileEntity) {
                if (projectileEntity.getOwner() != null) {
                    return projectileEntity.getOwner().getUuid();
                }
            }

            return null;
        }

        public Optional<Entity> getEntity(ServerWorld world) {
            return Optional.ofNullable(this.entity).or(() -> {
                Optional<UUID> var10000 = Optional.ofNullable(this.uuid);
                Objects.requireNonNull(world);
                return var10000.map(world::getEntity);
            });
        }

        public Optional<Entity> getOwner(ServerWorld world) {
            return this.getEntity(world).filter((entity) -> {
                return entity instanceof ProjectileEntity;
            }).map((entity) -> {
                return (ProjectileEntity) entity;
            }).map(ProjectileEntity::getOwner).or(() -> {
                Optional<UUID> var10000 = Optional.ofNullable(this.projectileOwnerUuid);
                Objects.requireNonNull(world);
                return var10000.map(world::getEntity);
            });
        }

        public GameEvent gameEvent() {
            return this.gameEvent;
        }

        public float distance() {
            return this.distance;
        }

        public WildVec3d pos() {
            return this.pos;
        }

        @Nullable
        public UUID uuid() {
            return this.uuid;
        }

        @Nullable
        public UUID projectileOwnerUuid() {
            return this.projectileOwnerUuid;
        }

        @Nullable
        public Entity entity() {
            return this.entity;
        }
    }
}
