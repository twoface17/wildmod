package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.VibrationParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Vibration;

public abstract class RegisterParticles {

    // instance of our particle
    public static final DefaultParticleType FIREFLY = FabricParticleTypes.simple();
    public static final DefaultParticleType SHRIEKER = FabricParticleTypes.simple();
    public static final DefaultParticleType SCULK_SOUL = FabricParticleTypes.simple();

    public static void RegisterParticles() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "firefly"), FIREFLY);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "shrieker"), SHRIEKER);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_soul"), SCULK_SOUL);
    }
}
