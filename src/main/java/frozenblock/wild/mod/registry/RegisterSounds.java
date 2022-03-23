package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegisterSounds { //Register sound definitions

    /** BLOCKS **/
    //Deepslate Frame
        public static final SoundEvent BLOCK_REINFORCED_DEEPSLATE_STEP = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.reinforced_deepslate.step"));
        public static final SoundEvent BLOCK_REINFORCED_DEEPSLATE_BREAK = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.reinforced_deepslate.break"));
        public static final SoundEvent BLOCK_REINFORCED_DEEPSLATE_PLACE = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.reinforced_deepslate.place"));
        public static final SoundEvent BLOCK_REINFORCED_DEEPSLATE_HIT = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.reinforced_deepslate.hit"));
    //Mud
        public static final SoundEvent BLOCK_MUD_STEP = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mud.step"));
        public static final SoundEvent BLOCK_MUD_BREAK = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mud.break"));
        public static final SoundEvent BLOCK_MUD_PLACE = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mud.place"));
        public static final SoundEvent BLOCK_MUD_HIT = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mud.hit"));
    //Mud Bricks
        public static final SoundEvent BLOCK_MUD_BRICKS_STEP = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mud_bricks.step"));
        public static final SoundEvent BLOCK_MUD_BRICKS_BREAK = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mud_bricks.break"));
        public static final SoundEvent BLOCK_MUD_BRICKS_PLACE = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mud_bricks.place"));
        public static final SoundEvent BLOCK_MUD_BRICKS_HIT = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mud_bricks.hit"));
        public static final SoundEvent BLOCK_MUD_BRICKS_FALL = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mud_bricks.fall"));
    //Mangrove Propagule
        public static final SoundEvent BLOCK_MANGROVE_PROPAGULE_BREAK = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.propagule.break"));
        public static final SoundEvent BLOCK_MANGROVE_PROPAGULE_PLACE = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.propagule.place"));
        public static final SoundEvent BLOCK_MANGROVE_PROPAGULE_STEP = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.propagule.step"));
        public static final SoundEvent BLOCK_MANGROVE_PROPAGULE_HIT = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.propagule.hit"));
        public static final SoundEvent BLOCK_MANGROVE_PROPAGULE_FALL = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.propagule.fall"));
    //Mangrove Roots
        public static final SoundEvent BLOCK_MANGROVE_ROOTS_BREAK = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mangrove_roots.break"));
        public static final SoundEvent BLOCK_MANGROVE_ROOTS_PLACE = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mangrove_roots.place"));
        public static final SoundEvent BLOCK_MANGROVE_ROOTS_STEP = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mangrove_roots.step"));
        public static final SoundEvent BLOCK_MANGROVE_ROOTS_HIT = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mangrove_roots.hit"));
        public static final SoundEvent BLOCK_MANGROVE_ROOTS_FALL = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.mangrove_roots.fall"));
    //Sculk
        public static final SoundEvent BLOCK_SCULK_STEP = new SoundEvent(new Identifier("accuratesculk", "block.sculk.step"));
        public static final SoundEvent BLOCK_SCULK_HIT = new SoundEvent(new Identifier("accuratesculk", "block.sculk.hit"));
        public static final SoundEvent BLOCK_SCULK_FALL = new SoundEvent(new Identifier("accuratesculk", "block.sculk.fall"));
        public static final SoundEvent BLOCK_SCULK_BREAK = new SoundEvent(new Identifier("accuratesculk", "block.sculk.break"));
        public static final SoundEvent BLOCK_SCULK_PLACE = new SoundEvent(new Identifier("accuratesculk", "block.sculk.place"));
    //Sculk Catalyst
        public static final SoundEvent BLOCK_SCULK_CATALYST_BREAK = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_catalyst.break"));
        public static final SoundEvent BLOCK_SCULK_CATALYST_PLACE = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_catalyst.place"));
        public static final SoundEvent BLOCK_SCULK_CATALYST_STEP = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_catalyst.step"));
        public static final SoundEvent BLOCK_SCULK_CATALYST_BLOOM = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_catalyst.bloom"));
    //Sculk Sensor
        public static final SoundEvent BLOCK_SCULK_SENSOR_RECEIVE_RF = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_sensor.receive_rf"));
    //Sculk Shrieker
        public static final SoundEvent BLOCK_SCULK_SHRIEKER_BREAK = new SoundEvent(new Identifier("accuratesculk", "block.sculk_shrieker.break"));
        public static final SoundEvent BLOCK_SCULK_SHRIEKER_PLACE = new SoundEvent(new Identifier("accuratesculk", "block.sculk_shrieker.place"));
        public static final SoundEvent BLOCK_SCULK_SHRIEKER_SHRIEK = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_shrieker.shriek"));
    //Sculk Vein
        public static final SoundEvent BLOCK_SCULK_VEIN_PLACE = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_vein.place"));
        public static final SoundEvent BLOCK_SCULK_VEIN_BREAK = new SoundEvent(new Identifier(WildMod.MOD_ID, "block.sculk_vein.break"));

    /** ENTITIES **/
    //Chest Boat
        public static final SoundEvent ENTITY_CHEST_BOAT_PADDLE_WATER = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.chest_boat.paddle_water"));
        public static final SoundEvent ENTITY_CHEST_BOAT_PADDLE_LAND = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.chest_boat.paddle_land"));
    
    //Firefly
        public static final SoundEvent ENTITY_FIREFLY_LOOP = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.firefly.loop"));
        public static final SoundEvent ENTITY_FIREFLY_HURT = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.firefly.hurt"));
    //Frog
        public static final SoundEvent ENTITY_FROG_AMBIENT = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.ambient"));
        public static final SoundEvent ENTITY_FROG_DEATH = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.death"));
        public static final SoundEvent ENTITY_FROG_EAT = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.eat"));
        public static final SoundEvent ENTITY_FROG_HURT = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.hurt"));
        public static final SoundEvent ENTITY_FROG_LONG_JUMP = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.long_jump"));
        public static final SoundEvent ENTITY_FROG_STEP = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.step"));
        public static final SoundEvent ENTITY_FROG_TONGUE = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.frog.tongue"));
    //Warden
        public static final SoundEvent ENTITY_WARDEN_AMBIENT = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.ambient"));
        public static final SoundEvent ENTITY_WARDEN_AMBIENT_UNDERGROUND = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.ambient_underground"));
        public static final SoundEvent ENTITY_WARDEN_ANGRY = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.warden.angry"));
        public static final SoundEvent ENTITY_WARDEN_ATTACK = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.attack"));
        public static final SoundEvent ENTITY_WARDEN_CLOSE = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.close"));
        public static final SoundEvent ENTITY_WARDEN_CLOSER = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.closer"));
        public static final SoundEvent ENTITY_WARDEN_CLOSEST = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.closest"));
        public static final SoundEvent ENTITY_WARDEN_DEATH = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.death"));
        public static final SoundEvent ENTITY_WARDEN_DIG = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.dig"));
        public static final SoundEvent ENTITY_WARDEN_EMERGE = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.emerge"));
        public static final SoundEvent ENTITY_WARDEN_HEARTBEAT = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.heartbeat"));
        public static final SoundEvent ENTITY_WARDEN_HURT = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.hurt"));
        public static final SoundEvent ENTITY_WARDEN_LISTENING = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.warden.listening"));
        public static final SoundEvent ENTITY_WARDEN_LISTENING_ANGRY = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.warden.listening_angry"));
        public static final SoundEvent ENTITY_WARDEN_ROAR = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.roar"));
        public static final SoundEvent ENTITY_WARDEN_SLIGHTLY_ANGRY = new SoundEvent(new Identifier(WildMod.MOD_ID, "entity.warden.slightly_angry"));
        public static final SoundEvent ENTITY_WARDEN_SNIFF = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.sniff"));
        public static final SoundEvent ENTITY_WARDEN_STEP = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.step"));
        public static final SoundEvent ENTITY_WARDEN_VIBRATION = new SoundEvent(new Identifier(WildMod.MOD_ID,"entity.warden.vibration"));

    /** MUSIC **/
        public static final SoundEvent MUSIC_OVERWORLD_DEEP_DARK = new SoundEvent(new Identifier(WildMod.MOD_ID,"music.overworld.deep_dark"));

    /** AMBIENT **/
        public static final SoundEvent AMBIENT_DEEP_DARK_ADDITIONS = new SoundEvent(new Identifier(WildMod.MOD_ID, "ambient.deep_dark.additions"));
        public static final SoundEvent AMBIENT_DEEP_DARK_LOOP = new SoundEvent(new Identifier(WildMod.MOD_ID, "ambient.deep_dark.loop"));


    private static SoundEvent register(String id) {
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(new Identifier(WildMod.MOD_ID, id)));
    }
}

