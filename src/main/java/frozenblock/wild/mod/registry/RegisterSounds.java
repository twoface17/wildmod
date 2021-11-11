package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegisterSounds {

    public static final Identifier SHRIEKER_ID = new Identifier(WildMod.MOD_ID, "block.shrieker.shriek");
    public static SoundEvent SHRIEKER_EVENT = new SoundEvent(SHRIEKER_ID);
    public static final Identifier SLIGHTLY_ANGRY_ID = new Identifier(WildMod.MOD_ID, "entity.warden.slightly_angry");
    public static SoundEvent SLIGHTLY_ANGRY_EVENT = new SoundEvent(SLIGHTLY_ANGRY_ID);
    public static final Identifier WARDEN_AMBIENT_ID = new Identifier(WildMod.MOD_ID, "entity.warden.ambient");
    public static SoundEvent WARDEN_AMBIENT_EVENT = new SoundEvent(WARDEN_AMBIENT_ID);

    public static void RegisterSounds() {
        Registry.register(Registry.SOUND_EVENT, SHRIEKER_ID, SHRIEKER_EVENT);
        Registry.register(Registry.SOUND_EVENT, SLIGHTLY_ANGRY_ID, SLIGHTLY_ANGRY_EVENT);
        Registry.register(Registry.SOUND_EVENT, WARDEN_AMBIENT_ID, WARDEN_AMBIENT_EVENT);
    }

}

