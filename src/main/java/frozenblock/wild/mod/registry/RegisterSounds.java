package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegisterSounds {

    public static final Identifier SHRIEKER_ID = new Identifier(WildMod.MOD_ID, "shrieker");
    public static SoundEvent SHRIEKER_EVENT = new SoundEvent(SHRIEKER_ID);
    public static final Identifier SLIGHTLY_ANGRY_ID = new Identifier(WildMod.MOD_ID, "slightly_angry");
    public static SoundEvent SLIGHTLY_ANGRY_EVENT = new SoundEvent(SHRIEKER_ID);

    public static void RegisterSounds() {
        Registry.register(Registry.SOUND_EVENT, SHRIEKER_ID, SHRIEKER_EVENT);
        Registry.register(Registry.SOUND_EVENT, SLIGHTLY_ANGRY_ID, SLIGHTLY_ANGRY_EVENT);
    }

}

