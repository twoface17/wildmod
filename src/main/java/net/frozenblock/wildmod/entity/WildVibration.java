package net.frozenblock.wildmod.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.misc.WildVec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record WildVibration(GameEvent gameEvent, float distance, WildVec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {
    public static final Codec<UUID> UUID = DynamicSerializableUuid.CODEC;
    //final GameEvent gameEvent;
    //final Vec3d pos;
    public static final Codec<WildVibration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Registry.GAME_EVENT.getCodec().fieldOf("game_event").forGetter(WildVibration::gameEvent),
                            Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(WildVibration::distance),
                            WildVec3d.CODEC.fieldOf("pos").forGetter(WildVibration::pos),
                            UUID.optionalFieldOf("source").forGetter(vibration -> Optional.ofNullable(vibration.uuid())),
                            UUID.optionalFieldOf("projectile_owner").forGetter(vibration -> Optional.ofNullable(vibration.projectileOwnerUuid()))
                    )
                    .apply(
                            instance,
                            (gameEvent, float_, vec3d, optional, optional2) -> new WildVibration(
                                    gameEvent, float_, vec3d, optional.orElse(null), optional2.orElse(null)
                            )
                    )
    );

    public WildVibration(GameEvent gameEvent, float f, WildVec3d pos, @Nullable UUID uuid, @Nullable UUID sourceUuid) {
        this(gameEvent, f, pos, uuid, sourceUuid, null);
    }

    public WildVibration(GameEvent gameEvent, float f, WildVec3d pos, @Nullable Entity entity) {
        this(gameEvent, f, pos, entity == null ? null : entity.getUuid(), getOwnerUuid(entity), entity);
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