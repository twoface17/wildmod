package net.frozenblock.wildmod.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record WildVibration(@Nullable UUID uuid, @Nullable UUID sourceUuid, @Nullable Entity entity) {
    public WildVibration(@Nullable Entity entity) {
        this(entity == null ? null : entity.getUuid(), getOwnerUuid(entity), entity);
    }

    @Nullable
    private static UUID getOwnerUuid(@Nullable Entity entity) {
        if (entity instanceof ProjectileEntity projectile && projectile.getOwner() != null) {
            return projectile.getOwner().getUuid();
        }

        return null;
    }

    public Optional<Entity> getEntity(ServerWorld level) {
        return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map(level::getEntity));
    }

    public Optional<Entity> getOwner(ServerWorld level) {
        return Optional.ofNullable(this.entity)
                .filter(entity -> entity instanceof ProjectileEntity)
                .map(entity -> (ProjectileEntity) entity)
                .map(ProjectileEntity::getOwner)
                .or(() -> Optional.ofNullable(this.sourceUuid).map(level::getEntity));
    }

    public interface Instance {
        static WildVibration.Instance of(SculkSensorListener listener) {
            return (WildVibration.Instance) listener;
        }

        void setPos(BlockPos var1);

        BlockPos getPos();

        void setEntity(Entity var1);

        Entity getEntity();

        void setSource(Entity var1);

        Entity getSource();

        void setVibration(WildVibration var1);
    }
}
