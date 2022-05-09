//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.frozenblock.wildmod.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.liukrastapi.TickCriterion;
import net.frozenblock.wildmod.liukrastapi.Vec3d;
import net.frozenblock.wildmod.particle.VibrationParticleEffect;
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
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class VibrationListener implements GameEventListener {
    protected final PositionSource positionSource;
    protected final int range;
    protected final Callback callback;
    @Nullable
    protected Vibration vibration;
    protected int distance;
    protected int delay;
    private static final Codec<UUID> UUID = DynamicSerializableUuid.CODEC;

    public static Codec<VibrationListener> createCodec(VibrationListener.Callback callback) {
        return RecordCodecBuilder.create(
                instance -> instance.group(
                                PositionSource.CODEC.fieldOf("source").forGetter(listener -> listener.positionSource),
                                Codecs.NONNEGATIVE_INT.fieldOf("range").forGetter(listener -> listener.range),
                                VibrationListener.Vibration.CODEC.optionalFieldOf("event").forGetter(listener -> Optional.ofNullable(listener.vibration)),
                                Codecs.NONNEGATIVE_INT.fieldOf("event_distance").orElse(0).forGetter(listener -> listener.distance),
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

    public VibrationListener(
            PositionSource positionSource, int range, VibrationListener.Callback callback, @Nullable VibrationListener.Vibration vibration, int distance, int delay
    ) {
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
                                (Entity)this.vibration.getEntity(serverWorld).orElse(null),
                                (Entity)this.vibration.getOwner(serverWorld).orElse(null),
                                this.distance
                        );
                this.vibration = null;
            }
        }

    }

    public net.minecraft.world.event.PositionSource getPositionSource() {
        return (net.minecraft.world.event.PositionSource) this.positionSource;
    }

    public int getRange() {
        return this.range;
    }

    public boolean listen(ServerWorld world, GameEvent.class_7447 arg) {
        if (this.vibration != null) {
            return false;
        } else {
            GameEvent gameEvent = arg.method_43724();
            net.frozenblock.wildmod.event.GameEvent.Emitter emitter = arg.method_43727();
            if (!this.callback.canAccept(gameEvent, emitter)) {
                return false;
            } else {
                Optional<net.minecraft.util.math.Vec3d> optional = this.positionSource.getPos(world);
                if (optional.isEmpty()) {
                    return false;
                } else {
                    Vec3d vec3d = arg.method_43726();
                    net.minecraft.util.math.Vec3d vec3d2 = optional.get();
                    if (!this.callback.accepts(world, this, new BlockPos(vec3d), gameEvent, emitter)) {
                        return false;
                    } else if (isOccluded( world, vec3d, (Vec3d) vec3d2)) {
                        return false;
                    } else {
                        this.listen(world, gameEvent, emitter, vec3d, (Vec3d)vec3d2);
                        return true;
                    }
                }
            }
        }
    }

    private void listen(ServerWorld world, GameEvent gameEvent, net.frozenblock.wildmod.event.GameEvent.Emitter emitter, Vec3d start, Vec3d end) {
        this.distance = MathHelper.floor(start.distanceTo(end));
        this.vibration = new VibrationListener.Vibration(gameEvent, this.distance, start, emitter.sourceEntity());
        this.delay = this.distance;
        world.spawnParticles(new VibrationParticleEffect((net.minecraft.world.event.PositionSource) this.positionSource, this.delay), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
        this.callback.onListen();
    }

    private static boolean isOccluded(World world, net.frozenblock.wildmod.liukrastapi.Vec3d start, net.frozenblock.wildmod.liukrastapi.Vec3d end) {
        net.frozenblock.wildmod.liukrastapi.Vec3d vec3d = new net.frozenblock.wildmod.liukrastapi.Vec3d((double)MathHelper.floor(start.x) + 0.5, (double)MathHelper.floor(start.y) + 0.5, (double)MathHelper.floor(start.z) + 0.5);
        net.frozenblock.wildmod.liukrastapi.Vec3d vec3d2 = new net.frozenblock.wildmod.liukrastapi.Vec3d((double)MathHelper.floor(end.x) + 0.5, (double)MathHelper.floor(end.y) + 0.5, (double)MathHelper.floor(end.z) + 0.5);

        for(Direction direction : Direction.values()) {
            net.frozenblock.wildmod.liukrastapi.Vec3d vec3d3 = vec3d.withBias(direction, 1.0E-5F);
            if (world.raycast(new BlockStateRaycastContext(vec3d3, vec3d2, state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() != Type.BLOCK) {
                return false;
            }
        }

        return true;
    }

    public interface Callback {
        default TagKey<net.minecraft.world.event.GameEvent> getTag() {
            return GameEventTags.VIBRATIONS;
        }

        default boolean canAvoidVibrations() {
            return false;
        }

        default boolean canAccept(net.minecraft.world.event.GameEvent gameEvent, GameEvent.Emitter emitter) {
            if (!gameEvent.isIn(this.getTag())) {
                return false;
            } else {
                Entity entity = emitter.sourceEntity();
                if (entity != null) {
                    if (entity.isSpectator()) {
                        return false;
                    }

                    if (entity.bypassesSteppingEffects() && gameEvent.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                        if (this.canAvoidVibrations() && entity instanceof ServerPlayerEntity serverPlayerEntity) {
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

        boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Emitter emitter);

        void accept(
                ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, int delay
        );

        default void onListen() {
        }
    }

    public static record Vibration(
            GameEvent gameEvent, int distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity
    ) {
        public static final Codec<VibrationListener.Vibration> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Registry.GAME_EVENT.getCodec().fieldOf("game_event").forGetter(VibrationListener.Vibration::gameEvent),
                                Codecs.NONNEGATIVE_INT.fieldOf("distance").forGetter(VibrationListener.Vibration::distance),
                                Vec3d.CODEC.fieldOf("pos").forGetter(VibrationListener.Vibration::pos),
                                UUID.optionalFieldOf("source").forGetter(vibration -> Optional.ofNullable(vibration.uuid())),
                                UUID.optionalFieldOf("projectile_owner").forGetter(vibration -> Optional.ofNullable(vibration.projectileOwnerUuid()))
                        )
                        .apply(
                                instance,
                                (gameEvent, integer, vec3d, optional, optional2) -> new VibrationListener.Vibration(
                                        (GameEvent)gameEvent, integer, vec3d, optional.orElse(null), optional2.orElse(null)
                                )
                        )
        );

        public Vibration(GameEvent gameEvent, int distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID sourceUuid) {
            this(gameEvent, distance, pos, uuid, sourceUuid, null);
        }

        public Vibration(GameEvent gameEvent, int distance, Vec3d pos, @Nullable Entity entity) {
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
                    .map(entity -> (ProjectileEntity)entity)
                    .map(ProjectileEntity::getOwner)
                    .or(() -> Optional.ofNullable(this.projectileOwnerUuid).map(world::getEntity));
        }
    }
}
