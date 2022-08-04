package net.frozenblock.wildmod.event;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.WildMod;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class WildEntityPositionSource implements PositionSource {
    private static final Codec<UUID> UUID = DynamicSerializableUuid.CODEC;
    public static final Codec<WildEntityPositionSource> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(UUID.fieldOf("source_entity").forGetter(WildEntityPositionSource::getUuid), Codec.FLOAT.fieldOf("y_offset").orElse(0.0F).forGetter((entityPositionSource) -> {
            return entityPositionSource.yOffset;
        })).apply(instance, (uUID, float_) -> {
            return new WildEntityPositionSource(Either.right(Either.left(uUID)), float_);
        });
    });
    private Either<Entity, Either<UUID, Integer>> source;
    final float yOffset;

    public WildEntityPositionSource(Entity entity, float yOffset) {
        this(Either.left(entity), yOffset);
    }

    public WildEntityPositionSource(Either<Entity, Either<UUID, Integer>> source, float yOffset) {
        this.source = source;
        this.yOffset = yOffset;
    }

    public Optional<BlockPos> getPos(World world) {
        if (this.source.left().isEmpty()) {
            this.findEntityInWorld(world);
        }

        return this.source.left().map(entity -> entity.getBlockPos().add(0.0, this.yOffset, 0.0));
    }

    private void findEntityInWorld(World world) {
        (this.source.map(Optional::of, either -> Optional.ofNullable(either.map(uuid -> {
            Entity entity;
            if (world instanceof ServerWorld serverLevel) {
                entity = serverLevel.getEntity(uuid);
            } else {
                entity = null;
            }

            return entity;
        }, world::getEntityById)))).ifPresent(entity -> this.source = Either.left(entity));
    }

    public PositionSourceType<?> getType() {
        return WildMod.ENTITY;
    }

    private UUID getUuid() {
        return this.source.map(Entity::getUuid, (either) -> either.map(Function.identity(), (integer) -> {
            throw new RuntimeException("Unable to get entityId from uuid");
        }));
    }

    int getEntityId() {
        return this.source.map(Entity::getId, (either) -> either.map(uUID -> {
            throw new IllegalStateException("Unable to get entityId from uuid");
        }, Function.identity()));
    }

    public static class Type implements PositionSourceType<WildEntityPositionSource> {
        public Type() {
        }

        public WildEntityPositionSource read(PacketByteBuf buf) {
            return new WildEntityPositionSource(Either.right(Either.right(buf.readVarInt())), buf.readFloat());
        }

        public void write(PacketByteBuf buf, WildEntityPositionSource source) {
            buf.writeVarInt(source.getEntityId());
            buf.writeFloat(source.yOffset);
        }

        @Override
        public WildEntityPositionSource readFromBuf(PacketByteBuf buf) {
            return null;
        }

        @Override
        public void writeToBuf(PacketByteBuf buf, WildEntityPositionSource positionSource) {

        }

        public Codec<WildEntityPositionSource> getCodec() {
            return WildEntityPositionSource.CODEC;
        }
    }
}
