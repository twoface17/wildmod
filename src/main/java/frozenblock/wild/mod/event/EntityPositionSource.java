package frozenblock.wild.mod.event;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class EntityPositionSource implements PositionSource {
    private static final Codec<UUID> UUID = DynamicSerializableUuid.CODEC;
    public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(UUID.fieldOf("source_entity").forGetter(EntityPositionSource::getUuid), Codec.FLOAT.fieldOf("y_offset").orElse(0.0F).forGetter((entityPositionSource) -> {
            return entityPositionSource.yOffset;
        })).apply(instance, (uUID, float_) -> {
            return new EntityPositionSource(Either.right(Either.left(uUID)), float_);
        });
    });
    private Either<Entity, Either<UUID, Integer>> source;
    final float yOffset;

    public EntityPositionSource(Entity entity, float yOffset) {
        this(Either.left(entity), yOffset);
    }

    EntityPositionSource(Either<Entity, Either<UUID, Integer>> source, float yOffset) {
        this.source = source;
        this.yOffset = yOffset;
    }

    public Optional<Vec3d> getPos(World world) {
        if (this.source.left().isEmpty()) {
            this.findEntityInWorld(world);
        }

        return this.source.left().map((entity) -> {
            return entity.getPos().add(0.0, (double)this.yOffset, 0.0);
        });
    }

    private void findEntityInWorld(World world) {
        (this.source.map(Optional::of, (either) -> {
            Function var10001 = (uuid) -> {
                Entity var10000;
                if (world instanceof ServerWorld serverWorld) {
                    var10000 = serverWorld.getEntity((UUID) uuid);
                } else {
                    var10000 = null;
                }

                return var10000;
            };
            Objects.requireNonNull(world);
            return Optional.ofNullable(either.map(var10001, world::getEntityById));
        })).ifPresent((entity) -> {
            this.source = Either.left(entity);
        });
    }

    private UUID getUuid() {
        return (UUID)this.source.map(Entity::getUuid, (either) -> {
            return (UUID)either.map(Function.identity(), (integer) -> {
                throw new RuntimeException("Unable to get entityId from uuid");
            });
        });
    }

    int getEntityId() {
        return (Integer)this.source.map(Entity::getId, (either) -> {
            return (Integer)either.map((uUID) -> {
                throw new IllegalStateException("Unable to get entityId from uuid");
            }, Function.identity());
        });
    }

    public PositionSourceType<?> getType() {
        return PositionSourceType.ENTITY;
    }

    public static class Type implements PositionSourceType<EntityPositionSource> {
        public Type() {
        }

        public net.minecraft.world.event.PositionSource readFromBuf(PacketByteBuf packetByteBuf) {
            return (net.minecraft.world.event.PositionSource) new EntityPositionSource(Either.right(Either.right(packetByteBuf.readVarInt())), packetByteBuf.readFloat());
        }

        public void writeToBuf(PacketByteBuf buf, net.minecraft.world.event.PositionSource positionSource) {

        }

        public void writeToBuf(PacketByteBuf packetByteBuf, EntityPositionSource entityPositionSource) {
            packetByteBuf.writeVarInt(entityPositionSource.getEntityId());
            packetByteBuf.writeFloat(entityPositionSource.yOffset);
        }

        public Codec<EntityPositionSource> getCodec() {
            return EntityPositionSource.CODEC;
        }
    }
}
