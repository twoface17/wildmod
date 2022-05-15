package net.frozenblock.wildmod.registry;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.ai.WardenPositionSource;
import net.frozenblock.wildmod.fromAccurateSculk.WildBlockEntityType;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSourceType;

public abstract class RegisterAccurateSculk {
    //FROM LUNADE'S MOD ACCURATE SCULK
    public static final String ACCURATE_SCULK_ID = "accuratesculk";

    public static final Identifier SHRIEKER_GARGLE1_PACKET = new Identifier("gargle1_packet");
    public static final Identifier SHRIEKER_GARGLE2_PACKET = new Identifier("gargle2_packet");
    public static final Identifier CATALYST_PARTICLE_PACKET = new Identifier("catalyst_packet");

    public static final Identifier GARGLE = new Identifier("accuratesculk:gargle");
    //public static final GameEvent CLICK = new GameEvent("click", 8);
    public static final GameEvent DEATH = new GameEvent("death", 8);
    public static final PositionSourceType<WardenPositionSource> WARDEN = new WardenPositionSource.Type();
    public static final SoundEvent GARGLE_EVENT = new SoundEvent(GARGLE);

    public static void RegisterAccurateSculk() {
        //Registry.register(Registry.GAME_EVENT, new Identifier(WildMod.MOD_ID, "click"), CLICK);
        Registry.register(Registry.GAME_EVENT, new Identifier(WildMod.MOD_ID, "death"), DEATH);
        Registry.register(Registry.SOUND_EVENT, RegisterAccurateSculk.GARGLE, GARGLE_EVENT);
        WildBlockEntityType.init();
    }
}
