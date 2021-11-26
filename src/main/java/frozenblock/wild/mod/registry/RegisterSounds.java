package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.system.CallbackI;

public class RegisterSounds {

    public static SoundEvent BLOCK_SCULK_CATALYST_BREAK = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_catalyst.break"));
    public static SoundEvent BLOCK_SCULK_CATALYST_PLACE = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_catalyst.place"));
    public static SoundEvent BLOCK_SCULK_CATALYST_STEP = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_catalyst.step"));
    public static SoundEvent BLOCK_SCULK_SHRIEKER_SHRIEK = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_shrieker.shriek"));
    public static SoundEvent ENTITY_WARDEN_SLIGHTLY_ANGRY = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.warden.slightly_angry"));
    public static SoundEvent EFFECT_NEARBY_CLOSE = new SoundEvent(new Identifier(WildMod.MOD_ID, "effect.nearby_close"));
    public static SoundEvent ENTITY_FROG_AMBIENT = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.ambient"));
    public static SoundEvent ENTITY_WARDEN_AMBIENT = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.ambient"));


    public static void RegisterSounds() {

        BLOCK_SCULK_CATALYST_BREAK = register(BLOCK_SCULK_CATALYST_BREAK.getId());
        BLOCK_SCULK_CATALYST_PLACE = register(BLOCK_SCULK_CATALYST_PLACE.getId());
        BLOCK_SCULK_CATALYST_STEP = register(BLOCK_SCULK_CATALYST_STEP.getId());
        BLOCK_SCULK_SHRIEKER_SHRIEK = register(BLOCK_SCULK_SHRIEKER_SHRIEK.getId());
        ENTITY_WARDEN_SLIGHTLY_ANGRY = register(ENTITY_WARDEN_SLIGHTLY_ANGRY.getId());
        EFFECT_NEARBY_CLOSE = register(EFFECT_NEARBY_CLOSE.getId());
        ENTITY_FROG_AMBIENT = register(ENTITY_FROG_AMBIENT.getId());
        ENTITY_WARDEN_AMBIENT = register(ENTITY_WARDEN_AMBIENT.getId());

    }

    private static SoundEvent register(Identifier id) {
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

}

