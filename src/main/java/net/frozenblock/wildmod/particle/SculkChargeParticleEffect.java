package net.frozenblock.wildmod.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.registry.RegisterParticles;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public record SculkChargeParticleEffect(float roll) implements ParticleEffect {
    public static final Codec<SculkChargeParticleEffect> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.FLOAT.fieldOf("roll").forGetter(particleEffect -> particleEffect.roll))
                    .apply(instance, SculkChargeParticleEffect::new)
    );
    public static final Factory<SculkChargeParticleEffect> FACTORY = new Factory<SculkChargeParticleEffect>() {
        public SculkChargeParticleEffect read(ParticleType<SculkChargeParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float f = stringReader.readFloat();
            return new SculkChargeParticleEffect(f);
        }

        public SculkChargeParticleEffect read(ParticleType<SculkChargeParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new SculkChargeParticleEffect(packetByteBuf.readFloat());
        }
    };

    public ParticleType<SculkChargeParticleEffect> getType() {
        return RegisterParticles.SCULK_CHARGE;
    }

    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.roll);
    }

    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.roll);
    }
}
