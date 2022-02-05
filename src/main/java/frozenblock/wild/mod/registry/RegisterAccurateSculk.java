package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.fromAccurateSculk.NewBlockEntityType;
import frozenblock.wild.mod.fromAccurateSculk.WardenPositionSource;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSourceType;

public abstract class RegisterAccurateSculk {
    //FROM LUNADE'S MOD ACCURATE SCULK
    public static final String ACCURATE_SCULK_ID = "accuratesculk";

    public static final Identifier SHRIEKER_SHRIEK_PACKET = new Identifier("shriek_packet");
    public static final Identifier SHRIEKER_GARGLE1_PACKET = new Identifier("gargle1_packet");
    public static final Identifier SHRIEKER_GARGLE2_PACKET = new Identifier("gargle2_packet");
    public static final Identifier CATALYST_PARTICLE_PACKET = new Identifier("catalyst_packet");
    public static final Identifier WARDEN_DIG_PARTICLES = new Identifier("warden_dig_packet");

    public static final DefaultParticleType SCULK_SHRIEK = FabricParticleTypes.simple();
    public static final DefaultParticleType SCULK_SHRIEK2 = FabricParticleTypes.simple();
    public static final DefaultParticleType SCULK_SHRIEKZ = FabricParticleTypes.simple();
    public static final DefaultParticleType SCULK_SHRIEKZ2 = FabricParticleTypes.simple();
    public static final DefaultParticleType SCULK_SHRIEKNX = FabricParticleTypes.simple();
    public static final DefaultParticleType SCULK_SHRIEKNX2 = FabricParticleTypes.simple();
    public static final DefaultParticleType SCULK_SHRIEKX = FabricParticleTypes.simple();
    public static final DefaultParticleType SCULK_SHRIEKX2 = FabricParticleTypes.simple();

    public static final Identifier GARGLE = new Identifier("accuratesculk:gargle");
    public static final GameEvent CLICK = new GameEvent("click", 8);
    public static final GameEvent DEATH = new GameEvent("death", 8);
    public static final PositionSourceType<WardenPositionSource> WARDEN = new WardenPositionSource.Type();
    public static SoundEvent GARGLE_EVENT = new SoundEvent(GARGLE);

    public static void RegisterAccurateSculk() {
        Registry.register(Registry.POSITION_SOURCE_TYPE, new Identifier(WildMod.MOD_ID, "warden_source"), WARDEN);
        Registry.register(Registry.GAME_EVENT, new Identifier(WildMod.MOD_ID, "click"), CLICK);
        Registry.register(Registry.GAME_EVENT, new Identifier(WildMod.MOD_ID, "death"), DEATH);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_shriek"), SCULK_SHRIEK);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_shriek2"), SCULK_SHRIEK2);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_shriekz"), SCULK_SHRIEKZ);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_shriekz2"), SCULK_SHRIEKZ2);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_shrieknx"), SCULK_SHRIEKNX);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_shrieknx2"), SCULK_SHRIEKNX2);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_shriekx"), SCULK_SHRIEKX);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(WildMod.MOD_ID, "sculk_shriekx2"), SCULK_SHRIEKX2);
        Registry.register(Registry.SOUND_EVENT, RegisterAccurateSculk.GARGLE, GARGLE_EVENT);
        NewBlockEntityType.init();
    }
}
