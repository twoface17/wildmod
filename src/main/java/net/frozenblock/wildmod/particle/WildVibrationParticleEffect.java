package net.frozenblock.wildmod.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.registry.RegisterParticles;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

import java.util.Locale;

public class WildVibrationParticleEffect implements ParticleEffect {
    public static final Codec<WildVibrationParticleEffect> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            PositionSource.CODEC.fieldOf("destination").forGetter(effect -> effect.destination),
                            Codec.INT.fieldOf("arrival_in_ticks").forGetter(vibrationParticleEffect -> vibrationParticleEffect.arrivalInTicks)
                    )
                    .apply(instance, WildVibrationParticleEffect::new)
    );
    public static final ParticleEffect.Factory<WildVibrationParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<WildVibrationParticleEffect>() {
        public WildVibrationParticleEffect read(ParticleType<WildVibrationParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float f = (float) stringReader.readDouble();
            stringReader.expect(' ');
            float g = (float) stringReader.readDouble();
            stringReader.expect(' ');
            float h = (float) stringReader.readDouble();
            stringReader.expect(' ');
            int i = stringReader.readInt();
            BlockPos blockPos = new BlockPos(f, g, h);
            return new WildVibrationParticleEffect(new BlockPositionSource(blockPos), i);
        }

        public WildVibrationParticleEffect read(ParticleType<WildVibrationParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            PositionSource positionSource = PositionSourceType.read(packetByteBuf);
            int i = packetByteBuf.readVarInt();
            return new WildVibrationParticleEffect(positionSource, i);
        }
    };
    private final PositionSource destination;
    private final int arrivalInTicks;

    public WildVibrationParticleEffect(PositionSource positionSource, int i) {
        this.destination = positionSource;
        this.arrivalInTicks = i;
    }

    @Override
    public void write(PacketByteBuf buf) {
        PositionSourceType.write(this.destination, buf);
        buf.writeVarInt(this.arrivalInTicks);
    }

    @Override
    public String asString() {
        Vec3d vec3d = Vec3d.ofCenter(this.destination.getPos(null).get());
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d", Registry.PARTICLE_TYPE.getId(this.getType()), d, e, f, this.arrivalInTicks);
    }

    @Override
    public ParticleType<WildVibrationParticleEffect> getType() {
        return RegisterParticles.VIBRATION;
    }

    public PositionSource getVibration() {
        return this.destination;
    }

    public int getArrivalInTicks() {
        return this.arrivalInTicks;
    }
}
