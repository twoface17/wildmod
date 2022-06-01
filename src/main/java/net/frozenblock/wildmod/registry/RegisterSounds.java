package net.frozenblock.wildmod.registry;

import com.google.common.collect.ImmutableList;
import net.frozenblock.wildmod.WildMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.stream.IntStream;

public class RegisterSounds { //Register sound definitions

    /** BLOCKS **/
    //Deepslate Frame
        public static final SoundEvent BLOCK_REINFORCED_DEEPSLATE_STEP = register("block.reinforced_deepslate.step");
        public static final SoundEvent BLOCK_REINFORCED_DEEPSLATE_BREAK = register("block.reinforced_deepslate.break");
        public static final SoundEvent BLOCK_REINFORCED_DEEPSLATE_PLACE = register("block.reinforced_deepslate.place");
        public static final SoundEvent BLOCK_REINFORCED_DEEPSLATE_HIT = register("block.reinforced_deepslate.hit");
    //Frogspawn
        public static final SoundEvent BLOCK_FROGSPAWN_STEP = register("block.frogspawn.step");
        public static final SoundEvent BLOCK_FROGSPAWN_BREAK = register("block.frogspawn.break");
        public static final SoundEvent BLOCK_FROGSPAWN_FALL = register("block.frogspawn.fall");
        public static final SoundEvent BLOCK_FROGSPAWN_HATCH = register("block.frogspawn.hatch");
        public static final SoundEvent BLOCK_FROGSPAWN_HIT = register("block.frogspawn.hit");
        public static final SoundEvent BLOCK_FROGSPAWN_PLACE = register("block.frogspawn.place");
    //Mud
        public static final SoundEvent BLOCK_MUD_STEP = register("block.mud.step");
        public static final SoundEvent BLOCK_MUD_BREAK = register("block.mud.break");
        public static final SoundEvent BLOCK_MUD_PLACE = register("block.mud.place");
        public static final SoundEvent BLOCK_MUD_HIT = register("block.mud.hit");
    //Mud Bricks
        public static final SoundEvent BLOCK_MUD_BRICKS_STEP = register("block.mud_bricks.step");
        public static final SoundEvent BLOCK_MUD_BRICKS_BREAK = register("block.mud_bricks.break");
        public static final SoundEvent BLOCK_MUD_BRICKS_PLACE = register("block.mud_bricks.place");
        public static final SoundEvent BLOCK_MUD_BRICKS_HIT = register("block.mud_bricks.hit");
        public static final SoundEvent BLOCK_MUD_BRICKS_FALL = register("block.mud_bricks.fall");
    //Packed Mud
        public static final SoundEvent BLOCK_PACKED_MUD_STEP = register("block.packed_mud.step");
        public static final SoundEvent BLOCK_PACKED_MUD_BREAK = register("block.packed_mud.break");
        public static final SoundEvent BLOCK_PACKED_MUD_PLACE = register("block.packed_mud.place");
        public static final SoundEvent BLOCK_PACKED_MUD_HIT = register("block.packed_mud.hit");
        public static final SoundEvent BLOCK_PACKED_MUD_FALL = register("block.packed_mud.fall");
    //Mangrove Propagule
        public static final SoundEvent BLOCK_MANGROVE_PROPAGULE_BREAK = register("block.propagule.break");
        public static final SoundEvent BLOCK_MANGROVE_PROPAGULE_PLACE = register("block.propagule.place");
        public static final SoundEvent BLOCK_MANGROVE_PROPAGULE_STEP = register("block.propagule.step");
        public static final SoundEvent BLOCK_MANGROVE_PROPAGULE_HIT = register("block.propagule.hit");
        public static final SoundEvent BLOCK_MANGROVE_PROPAGULE_FALL = register("block.propagule.fall");
    //Mangrove Roots
        public static final SoundEvent BLOCK_MANGROVE_ROOTS_BREAK = register("block.mangrove_roots.break");
        public static final SoundEvent BLOCK_MANGROVE_ROOTS_PLACE = register("block.mangrove_roots.place");
        public static final SoundEvent BLOCK_MANGROVE_ROOTS_STEP = register("block.mangrove_roots.step");
        public static final SoundEvent BLOCK_MANGROVE_ROOTS_HIT = register("block.mangrove_roots.hit");
        public static final SoundEvent BLOCK_MANGROVE_ROOTS_FALL = register("block.mangrove_roots.fall");
    //Muddy Mangrove Roots
        public static final SoundEvent BLOCK_MUDDY_MANGROVE_ROOTS_BREAK = register("block.muddy_mangrove_roots.break");
        public static final SoundEvent BLOCK_MUDDY_MANGROVE_ROOTS_STEP = register("block.muddy_mangrove_roots.step");
        public static final SoundEvent BLOCK_MUDDY_MANGROVE_ROOTS_PLACE = register("block.muddy_mangrove_roots.place");
        public static final SoundEvent BLOCK_MUDDY_MANGROVE_ROOTS_HIT = register("block.muddy_mangrove_roots.hit");
        public static final SoundEvent BLOCK_MUDDY_MANGROVE_ROOTS_FALL = register("block.muddy_mangrove_roots.fall");
    //Sculk
        public static final SoundEvent BLOCK_SCULK_SPREAD = register("block.sculk.spread");
        public static final SoundEvent BLOCK_SCULK_CHARGE = register("block.sculk.charge");
        public static final SoundEvent BLOCK_SCULK_BREAK = register("block.sculk.break");
        public static final SoundEvent BLOCK_SCULK_FALL = register("block.sculk.fall");
        public static final SoundEvent BLOCK_SCULK_HIT = register("block.sculk.hit");
        public static final SoundEvent BLOCK_SCULK_PLACE = register("block.sculk.place");
        public static final SoundEvent BLOCK_SCULK_STEP = register("block.sculk.step");
    //Sculk Catalyst
        public static final SoundEvent BLOCK_SCULK_CATALYST_BLOOM = register("block.sculk_catalyst.bloom");
        public static final SoundEvent BLOCK_SCULK_CATALYST_BREAK = register("block.sculk_catalyst.break");
        public static final SoundEvent BLOCK_SCULK_CATALYST_FALL = register("block.sculk_catalyst.fall");
        public static final SoundEvent BLOCK_SCULK_CATALYST_HIT = register("block.sculk_catalyst.hit");
        public static final SoundEvent BLOCK_SCULK_CATALYST_PLACE = register("block.sculk_catalyst.place");
        public static final SoundEvent BLOCK_SCULK_CATALYST_STEP = register("block.sculk_catalyst.step");
    //Sculk Sensor
        public static final SoundEvent BLOCK_SCULK_SENSOR_RECEIVE_RF = register("block.sculk_sensor.receive_rf");
    //Sculk Shrieker
        public static final SoundEvent BLOCK_SCULK_SHRIEKER_BREAK = register("block.sculk_shrieker.break");
        public static final SoundEvent BLOCK_SCULK_SHRIEKER_FALL = register("block.sculk_shrieker.fall");
        public static final SoundEvent BLOCK_SCULK_SHRIEKER_HIT = register("block.sculk_shrieker.hit");
        public static final SoundEvent BLOCK_SCULK_SHRIEKER_PLACE = register("block.sculk_shrieker.place");
        public static final SoundEvent BLOCK_SCULK_SHRIEKER_SHRIEK = register("block.sculk_shrieker.shriek");
        public static final SoundEvent BLOCK_SCULK_SHRIEKER_STEP = register("block.sculk_shrieker.step");
    //Sculk Vein
        public static final SoundEvent BLOCK_SCULK_VEIN_BREAK = register("block.sculk_vein.break");
        public static final SoundEvent BLOCK_SCULK_VEIN_FALL = register("block.sculk_vein.fall");
        public static final SoundEvent BLOCK_SCULK_VEIN_HIT = register("block.sculk_vein.hit");
        public static final SoundEvent BLOCK_SCULK_VEIN_PLACE = register("block.sculk_vein.place");
        public static final SoundEvent BLOCK_SCULK_VEIN_STEP = register("block.sculk_vein.step");
    /** ENTITIES **/
    //Chest Boat
        public static final SoundEvent ENTITY_CHEST_BOAT_PADDLE_WATER = register("entity.chest_boat.paddle_water");
        public static final SoundEvent ENTITY_CHEST_BOAT_PADDLE_LAND = register("entity.chest_boat.paddle_land");
    
    //Firefly
        public static final SoundEvent ENTITY_FIREFLY_LOOP = register("entity.firefly.loop");
        public static final SoundEvent ENTITY_FIREFLY_HURT = register("entity.firefly.hurt");
    //Frog
        public static final SoundEvent ENTITY_FROG_AMBIENT = register("entity.frog.ambient");
        public static final SoundEvent ENTITY_FROG_DEATH = register("entity.frog.death");
        public static final SoundEvent ENTITY_FROG_EAT = register("entity.frog.eat");
        public static final SoundEvent ENTITY_FROG_HURT = register("entity.frog.hurt");
        public static final SoundEvent ENTITY_FROG_LAY_SPAWN = register("entity.frog.lay_spawn");
        public static final SoundEvent ENTITY_FROG_LONG_JUMP = register("entity.frog.long_jump");
        public static final SoundEvent ENTITY_FROG_STEP = register("entity.frog.step");
        public static final SoundEvent ENTITY_FROG_TOUNGE = register("entity.frog.tongue");
    //Tadpole
        public static final SoundEvent ENTITY_TADPOLE_DEATH = register("entity.tadpole.death");
        public static final SoundEvent ENTITY_TADPOLE_FLOP = register("entity.tadpole.flop");
        public static final SoundEvent ENTITY_TADPOLE_GROW_UP = register("entity.tadpole.grow_up");
        public static final SoundEvent ENTITY_TADPOLE_HURT = register("entity.tadpole.hurt");
        public static final SoundEvent ITEM_BUCKET_EMPTY_TADPOLE = register("item.bucket.empty_tadpole");
        public static final SoundEvent ITEM_BUCKET_FILL_TADPOLE = register("item.bucket.fill_tadpole");
    //Warden
        public static final SoundEvent ENTITY_WARDEN_AGITATED = register("entity.warden.agitated");
        public static final SoundEvent ENTITY_WARDEN_AMBIENT = register("entity.warden.ambient");
        public static final SoundEvent ENTITY_WARDEN_AMBIENT_UNDERGROUND = register("entity.warden.ambient_underground");
        public static final SoundEvent ENTITY_WARDEN_ANGRY = register("entity.warden.angry");
        public static final SoundEvent ENTITY_WARDEN_ATTACK_IMPACT = register("entity.warden.attack_impact");
        public static final SoundEvent ENTITY_WARDEN_NEARBY_CLOSE = register("entity.warden.nearby_close");
        public static final SoundEvent ENTITY_WARDEN_NEARBY_CLOSER = register("entity.warden.nearby_closer");
        public static final SoundEvent ENTITY_WARDEN_NEARBY_CLOSEST = register("entity.warden.nearby_closest");
        public static final SoundEvent ENTITY_WARDEN_DEATH = register("entity.warden.death");
        public static final SoundEvent ENTITY_WARDEN_DIG = register("entity.warden.dig");
        public static final SoundEvent ENTITY_WARDEN_EMERGE = register("entity.warden.emerge");
        public static final SoundEvent ENTITY_WARDEN_HEARTBEAT = register("entity.warden.heartbeat");
        public static final SoundEvent ENTITY_WARDEN_HURT = register("entity.warden.hurt");
        public static final SoundEvent ENTITY_WARDEN_LISTENING = register("entity.warden.listening");
        public static final SoundEvent ENTITY_WARDEN_LISTENING_ANGRY = register("entity.warden.listening_angry");
        public static final SoundEvent ENTITY_WARDEN_ROAR = register("entity.warden.roar");
        public static final SoundEvent ENTITY_WARDEN_SNIFF = register("entity.warden.sniff");
        public static final SoundEvent ENTITY_WARDEN_SONIC_BOOM = register("entity.warden.sonic_boom");
        public static final SoundEvent ENTITY_WARDEN_SONIC_CHARGE = register("entity.warden.sonic_charge");
        public static final SoundEvent ENTITY_WARDEN_STEP = register("entity.warden.step");
        public static final SoundEvent ENTITY_WARDEN_TENDRIL_CLICKS = register("entity.warden.tendril_clicks");

    //Allay
        public static final SoundEvent ENTITY_ALLAY_AMBIENT_WITH_ITEM = register("entity.allay.ambient_with_item");
        public static final SoundEvent ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM = register("entity.allay.ambient_without_item");
        public static final SoundEvent ENTITY_ALLAY_DEATH = register("entity.allay.death");
        public static final SoundEvent ENTITY_ALLAY_HURT = register("entity.allay.hurt");
        public static final SoundEvent ENTITY_ALLAY_ITEM_GIVEN = register("entity.allay.item_given");
        public static final SoundEvent ENTITY_ALLAY_ITEM_TAKEN = register("entity.allay.item_taken");


    /** MUSIC **/
        public static final SoundEvent MUSIC_OVERWORLD_DEEP_DARK = register("music.overworld.deep_dark");
        public static final SoundEvent MUSIC_OVERWORLD_SWAMP = register("music.overworld.swamp");

    /** MUSIC DISCS **/
        public static final SoundEvent MUSIC_DISC_5 = register("music_disc.5");

    /** GOAT HORN **/
        public static final ImmutableList<SoundEvent> GOAT_HORN_SOUNDS = registerGoatHornSounds();
    /** AMBIENT **/
        public static final SoundEvent AMBIENT_DEEP_DARK_ADDITIONS = register("ambient.deep_dark.additions");
        public static final SoundEvent AMBIENT_DEEP_DARK_LOOP = register("ambient.deep_dark.loop");


    private static SoundEvent register(String string) {
        return Registry.register(Registry.SOUND_EVENT, new Identifier(WildMod.MOD_ID, string), new SoundEvent(new Identifier(WildMod.MOD_ID, string)));
    }

    private static ImmutableList<SoundEvent> registerGoatHornSounds() {
        return IntStream.range(0, 8)
                .mapToObj(variant -> register("item.goat_horn.sound." + variant))
                .collect(ImmutableList.toImmutableList());
    }

    public static void init() {
    }

}

