package net.frozenblock.wildmod.registry;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.WildMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.frozenblock.wildmod.particle.SculkChargeParticleEffect;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public abstract class RegisterParticles {
    
    public static final DefaultParticleType SCULK_SOUL = FabricParticleTypes.simple();
    public static final ParticleType<SculkChargeParticleEffect> SCULK_CHARGE = register(
            "sculk_charge", true, SculkChargeParticleEffect.FACTORY, particleType -> SculkChargeParticleEffect.CODEC
    );
    public static final DefaultParticleType SCULK_CHARGE_POP = FabricParticleTypes.simple(true);
    public static final DefaultParticleType SONIC_BOOM = FabricParticleTypes.simple();

    public static void RegisterParticles() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_soul"), SCULK_SOUL);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_charge_pop"), SCULK_CHARGE_POP);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sonic_boom"), SONIC_BOOM);
    }

    private static <T extends ParticleEffect> ParticleType<T> register(
            String name, boolean alwaysShow, ParticleEffect.Factory<T> factory, Function<ParticleType<T>, Codec<T>> codecGetter
    ) {
        return (ParticleType<T>)Registry.register(Registry.PARTICLE_TYPE, name, new ParticleType<T>(alwaysShow, factory) {
            public Codec<T> getCodec() {
                return (Codec<T>)codecGetter.apply(this);
            }
        });
    }
}
