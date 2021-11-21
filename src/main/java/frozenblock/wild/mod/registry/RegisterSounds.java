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
    public static final Identifier CATALYST_BREAK_ID = new Identifier(WildMod.MOD_ID, "block.sculk_catalyst.break");
    public static SoundEvent CATALYST_BREAK_EVENT = new SoundEvent(CATALYST_BREAK_ID);
    public static final Identifier CATALYST_PLACE_ID = new Identifier(WildMod.MOD_ID, "block.sculk_catalyst.place");
    public static SoundEvent CATALYST_PLACE_EVENT = new SoundEvent(CATALYST_PLACE_ID);
    public static final Identifier CATALYST_STEP_ID = new Identifier(WildMod.MOD_ID, "block.sculk_catalyst.step");
    public static SoundEvent CATALYST_STEP_EVENT = new SoundEvent(CATALYST_STEP_ID);
    public static final Identifier NEARBY_CLOSE_ID = new Identifier(WildMod.MOD_ID, "effect.nearby_close");
    public static SoundEvent NEARBY_CLOSE_EVENT = new SoundEvent(NEARBY_CLOSE_ID);

    public static void RegisterSounds() {
        Registry.register(Registry.SOUND_EVENT, SHRIEKER_ID, SHRIEKER_EVENT);
        Registry.register(Registry.SOUND_EVENT, SLIGHTLY_ANGRY_ID, SLIGHTLY_ANGRY_EVENT);
        Registry.register(Registry.SOUND_EVENT, WARDEN_AMBIENT_ID, WARDEN_AMBIENT_EVENT);
        Registry.register(Registry.SOUND_EVENT, CATALYST_BREAK_ID, CATALYST_BREAK_EVENT);
        Registry.register(Registry.SOUND_EVENT, CATALYST_PLACE_ID, CATALYST_PLACE_EVENT);
        Registry.register(Registry.SOUND_EVENT, CATALYST_STEP_ID,  CATALYST_STEP_EVENT);
        Registry.register(Registry.SOUND_EVENT, NEARBY_CLOSE_ID, NEARBY_CLOSE_EVENT);
    }

}

