package net.frozenblock.wildmod.entity.ai;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.registry.RegisterAccurateSculk;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

import java.util.Optional;

public class WardenPositionSource implements PositionSource {

    public static final Codec<WardenPositionSource> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("source_entity_id").forGetter((wardenPositionSource) -> {
            return wardenPositionSource.entityId;
        })).apply(instance, WardenPositionSource::new);
    });
    final int entityId;
    private Optional<Entity> entity = Optional.empty();

    public WardenPositionSource(int entityId) {
        this.entityId = entityId;
    }

    public Optional<BlockPos> getPos(World world) {
        if (!this.entity.isPresent()) {
            this.entity = Optional.ofNullable(world.getEntityById(this.entityId));
        }

        return this.entity.map(Entity::getCameraBlockPos);
    }

    public PositionSourceType<?> getType() {
        return RegisterAccurateSculk.WARDEN;
    }

    public static class Type implements PositionSourceType<WardenPositionSource> {
        public Type() {
        }

        public WardenPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
            return new WardenPositionSource(packetByteBuf.readVarInt());
        }

        public void writeToBuf(PacketByteBuf packetByteBuf, WardenPositionSource wardenPositionSource) {
            packetByteBuf.writeVarInt(wardenPositionSource.entityId);
        }

        public Codec<WardenPositionSource> getCodec() {
            return WardenPositionSource.CODEC;
        }
    }

}
