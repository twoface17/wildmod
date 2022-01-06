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
    public static SoundEvent BLOCK_SCULK_SHRIEKER_EFFECT = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_shrieker.effect"));
    public static SoundEvent ENTITY_WARDEN_SLIGHTLY_ANGRY = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.warden.slightly_angry"));
    public static SoundEvent EFFECT_NEARBY_CLOSE = new SoundEvent(new Identifier(WildMod.MOD_ID, "effect.nearby_close"));
    public static SoundEvent ENTITY_FROG_AMBIENT = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.ambient"));
    public static SoundEvent ENTITY_FROG_DEATH = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.death"));
    public static SoundEvent ENTITY_FROG_EAT = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.eat"));
    public static SoundEvent ENTITY_FROG_HURT = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.hurt"));
    public static SoundEvent ENTITY_FROG_LONG_JUMP = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.long_jump"));
    public static SoundEvent ENTITY_FROG_TONGUE = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.tongue"));
    public static SoundEvent ENTITY_WARDEN_AMBIENT = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.ambient"));
    public static SoundEvent ENTITY_WARDEN_EMERGE = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.emerge"));
    public static SoundEvent MUSIC_OVERWORLD_DEEPDARK = new SoundEvent(new Identifier(WildMod.MOD_ID,"music.overworld.deepdark"));
    public static SoundEvent BLOCK_SCULK_STEP = new SoundEvent(new Identifier("accuratesculk", "block.sculk.step"));
    public static SoundEvent BLOCK_SCULK_HIT = new SoundEvent(new Identifier("accuratesculk", "block.sculk.hit"));
    public static SoundEvent BLOCK_SCULK_FALL = new SoundEvent(new Identifier("accuratesculk", "block.sculk.fall"));
    public static SoundEvent BLOCK_SCULK_BREAK = new SoundEvent(new Identifier("accuratesculk", "block.sculk.break"));
    public static SoundEvent BLOCK_SCULK_PLACE = new SoundEvent(new Identifier("accuratesculk", "block.sculk.place"));
    public static SoundEvent BLOCK_SCULK_SHRIEKER_BREAK = new SoundEvent(new Identifier("accuratesculk", "block.sculk_shrieker.break"));
    public static SoundEvent BLOCK_SCULK_SHRIEKER_PLACE = new SoundEvent(new Identifier("accuratesculk", "block.sculk_shrieker.place"));


    public static void RegisterSounds() {

        BLOCK_SCULK_STEP = register(BLOCK_SCULK_STEP.getId());
        BLOCK_SCULK_HIT = register(BLOCK_SCULK_HIT.getId());
        BLOCK_SCULK_FALL = register(BLOCK_SCULK_FALL.getId());
        BLOCK_SCULK_BREAK = register(BLOCK_SCULK_BREAK.getId());
        BLOCK_SCULK_PLACE = register(BLOCK_SCULK_PLACE.getId());
        BLOCK_SCULK_CATALYST_BREAK = register(BLOCK_SCULK_CATALYST_BREAK.getId());
        BLOCK_SCULK_CATALYST_PLACE = register(BLOCK_SCULK_CATALYST_PLACE.getId());
        BLOCK_SCULK_CATALYST_STEP = register(BLOCK_SCULK_CATALYST_STEP.getId());
        BLOCK_SCULK_SHRIEKER_SHRIEK = register(BLOCK_SCULK_SHRIEKER_SHRIEK.getId());
        BLOCK_SCULK_SHRIEKER_EFFECT = register(BLOCK_SCULK_SHRIEKER_EFFECT.getId());
        ENTITY_WARDEN_SLIGHTLY_ANGRY = register(ENTITY_WARDEN_SLIGHTLY_ANGRY.getId());
        EFFECT_NEARBY_CLOSE = register(EFFECT_NEARBY_CLOSE.getId());
        ENTITY_FROG_AMBIENT = register(ENTITY_FROG_AMBIENT.getId());
        ENTITY_FROG_DEATH = register(ENTITY_FROG_DEATH.getId());
        ENTITY_FROG_EAT = register(ENTITY_FROG_EAT.getId());
        ENTITY_FROG_HURT = register(ENTITY_FROG_HURT.getId());
        ENTITY_FROG_LONG_JUMP = register(ENTITY_FROG_LONG_JUMP.getId());
        ENTITY_FROG_TONGUE = register(ENTITY_FROG_TONGUE.getId());
        ENTITY_WARDEN_AMBIENT = register(ENTITY_WARDEN_AMBIENT.getId());
        ENTITY_WARDEN_EMERGE = register(ENTITY_WARDEN_EMERGE.getId());
        MUSIC_OVERWORLD_DEEPDARK = register(MUSIC_OVERWORLD_DEEPDARK.getId());

    }

    private static SoundEvent register(Identifier id) {
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

}

