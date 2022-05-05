package net.frozenblock.wildmod.registry;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.frozenblock.wildmod.WildMod;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RegisterParticles {
    
    public static final DefaultParticleType SCULK_SOUL = FabricParticleTypes.simple();
    public static final DefaultParticleType SONIC_BOOM = FabricParticleTypes.simple();

    public static void RegisterParticles() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_soul"), SCULK_SOUL);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sonic_boom"), SONIC_BOOM);
    }
}
