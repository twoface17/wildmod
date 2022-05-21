package net.frozenblock.wildmod.registry;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.particle.SculkChargeParticleEffect;
import net.frozenblock.wildmod.particle.ShriekParticleEffect;
import net.frozenblock.wildmod.particle.WildVibrationParticleEffect;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public abstract class RegisterParticles {

    public static final ParticleType<SculkChargeParticleEffect> SCULK_CHARGE = register("sculk_charge", true, SculkChargeParticleEffect.FACTORY, particleType -> SculkChargeParticleEffect.CODEC);
    public static final DefaultParticleType SCULK_CHARGE_POP = FabricParticleTypes.simple(true);
    public static final DefaultParticleType SCULK_SOUL = FabricParticleTypes.simple();
    public static final ParticleType<ShriekParticleEffect> SHRIEK = register("shriek", false, ShriekParticleEffect.FACTORY, (type) -> {
        return ShriekParticleEffect.CODEC;
    });
    public static final DefaultParticleType SONIC_BOOM = FabricParticleTypes.simple();
    public static final ParticleType<WildVibrationParticleEffect> VIBRATION = register("vibration", true, WildVibrationParticleEffect.PARAMETERS_FACTORY, particleType -> WildVibrationParticleEffect.CODEC);

    public static void RegisterParticles() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_charge_pop"), SCULK_CHARGE_POP);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_soul"), SCULK_SOUL);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sonic_boom"), SONIC_BOOM);
    }

    private static <T extends ParticleEffect> ParticleType<T> register(
            String name, boolean alwaysShow, ParticleEffect.Factory<T> factory, Function<ParticleType<T>, Codec<T>> codecGetter
    ) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, name), new ParticleType<T>(alwaysShow, factory) {
            public Codec<T> getCodec() {
                return codecGetter.apply(this);
            }
        });
    }
}
