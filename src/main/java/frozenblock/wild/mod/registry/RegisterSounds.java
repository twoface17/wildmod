package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegisterSounds {

    public static SoundEvent BLOCK_SCULK_CATALYST_BREAK;
    public static SoundEvent BLOCK_SCULK_CATALYST_PLACE;
    public static SoundEvent BLOCK_SCULK_CATALYST_STEP;
    public static SoundEvent BLOCK_SCULK_SHRIEKER_SHRIEK;
    public static SoundEvent ENTITY_WARDEN_SLIGHTLY_ANGRY;
    public static SoundEvent EFFECT_NEARBY_CLOSE;
    public static SoundEvent ENTITY_FROG_AMBIENT;
    public static SoundEvent ENTITY_WARDEN_AMBIENT;


    public static void RegisterSounds() {

        BLOCK_SCULK_CATALYST_BREAK = register("block.sculk_catalyst.break");
        BLOCK_SCULK_CATALYST_PLACE = register("block.sculk_catalyst.place");
        BLOCK_SCULK_CATALYST_STEP = register("block.sculk_catalyst.step");
        BLOCK_SCULK_SHRIEKER_SHRIEK = register("block.sculk_shrieker.shriek");
        ENTITY_WARDEN_SLIGHTLY_ANGRY = register("entity.warden.slightly_angry");
        EFFECT_NEARBY_CLOSE = register("effect.nearby_close");
        ENTITY_FROG_AMBIENT = register("entity.frog.ambient");
        ENTITY_WARDEN_AMBIENT = register("entity.warden.ambient");
    }

    private static SoundEvent register(String id) {
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(new Identifier(WildMod.MOD_ID, id)));
    }

}

