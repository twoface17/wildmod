package net.frozenblock.wildmod.event;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.tag.GameEventTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GameEventTagProvider extends AbstractTagProvider<GameEvent> {

    private static final GameEvent[] wildVibrations = new GameEvent[]{
            GameEvent.BLOCK_ATTACH,
            GameEvent.BLOCK_CHANGE,
            GameEvent.BLOCK_CLOSE,
            GameEvent.BLOCK_DESTROY,
            GameEvent.BLOCK_DETACH,
            GameEvent.BLOCK_OPEN,
            GameEvent.BLOCK_PLACE,
            GameEvent.BLOCK_SWITCH,
            GameEvent.BLOCK_UNSWITCH,
            GameEvent.CONTAINER_CLOSE,
            GameEvent.CONTAINER_OPEN,
            GameEvent.DISPENSE_FAIL,
            GameEvent.DRINKING_FINISH,
            GameEvent.EAT,
            GameEvent.ELYTRA_FREE_FALL,
            GameEvent.ENTITY_DAMAGED,
            GameEvent.ENTITY_KILLED,
            GameEvent.MOB_INTERACT,
            GameEvent.ENTITY_PLACE,
            GameEvent.RAVAGER_ROAR,
            GameEvent.WOLF_SHAKING,
            GameEvent.EQUIP,
            GameEvent.EXPLODE,
            GameEvent.FLAP,
            GameEvent.FLUID_PICKUP,
            GameEvent.FLUID_PLACE,
            GameEvent.HIT_GROUND,
            GameEvent.SHULKER_CLOSE,
            GameEvent.SHULKER_OPEN,
            GameEvent.FISHING_ROD_CAST,
            GameEvent.FISHING_ROD_REEL_IN,
            GameEvent.RING_BELL,
            GameEvent.LIGHTNING_STRIKE,
            GameEvent.PISTON_CONTRACT,
            GameEvent.PISTON_EXTEND,
            GameEvent.PRIME_FUSE,
            GameEvent.PROJECTILE_LAND,
            GameEvent.PROJECTILE_SHOOT,
            GameEvent.SHEAR,
            GameEvent.SPLASH,
            GameEvent.STEP,
            GameEvent.SWIM,
            GameEvent.BLOCK_ATTACH,
            GameEvent.BLOCK_CHANGE,
            GameEvent.BLOCK_CLOSE,
            GameEvent.BLOCK_DESTROY,
            GameEvent.BLOCK_DETACH,
            GameEvent.BLOCK_OPEN,
            GameEvent.BLOCK_PLACE,
            GameEvent.BLOCK_PRESS,
            GameEvent.BLOCK_SWITCH,
            GameEvent.BLOCK_UNPRESS,
            GameEvent.BLOCK_UNSWITCH,
            GameEvent.CONTAINER_CLOSE,
            GameEvent.CONTAINER_OPEN,
            GameEvent.DISPENSE_FAIL,
            GameEvent.DRINKING_FINISH,
            GameEvent.EAT,
            GameEvent.ELYTRA_FREE_FALL,
            GameEvent.ENTITY_DAMAGED,
            GameEvent.ENTITY_KILLED,
            GameEvent.ENTITY_PLACE,
            GameEvent.EQUIP,
            GameEvent.EXPLODE,
            GameEvent.FISHING_ROD_CAST,
            GameEvent.FISHING_ROD_REEL_IN,
            GameEvent.FLAP,
            GameEvent.FLUID_PICKUP,
            GameEvent.FLUID_PLACE,
            GameEvent.HIT_GROUND,
            GameEvent.MOB_INTERACT,
            GameEvent.LIGHTNING_STRIKE,
            GameEvent.MINECART_MOVING,
            GameEvent.PISTON_CONTRACT,
            GameEvent.PISTON_EXTEND,
            GameEvent.PRIME_FUSE,
            GameEvent.PROJECTILE_LAND,
            GameEvent.PROJECTILE_SHOOT,
            GameEvent.RAVAGER_ROAR,
            GameEvent.RING_BELL,
            GameEvent.SHEAR,
            GameEvent.SPLASH,
            GameEvent.STEP,
            GameEvent.SWIM,
    };

    public GameEventTagProvider(DataGenerator root) {
        super(root, Registry.GAME_EVENT);
    }

    protected void configure() {
        this.getOrCreateTagBuilder(GameEventTags.VIBRATIONS).add(wildVibrations).add(GameEvent.FLAP);
        this.getOrCreateTagBuilder(WildEventTags.SHRIEKER_CAN_LISTEN).add(net.frozenblock.wildmod.event.GameEvent.SCULK_SENSOR_TENDRILS_CLICKING);
        this.getOrCreateTagBuilder(WildEventTags.WARDEN_CAN_LISTEN).add(wildVibrations).add(net.frozenblock.wildmod.event.GameEvent.SHRIEK).addTag(WildEventTags.SHRIEKER_CAN_LISTEN);
        this.getOrCreateTagBuilder(WildEventTags.ALLAY_CAN_LISTEN).add(net.frozenblock.wildmod.event.GameEvent.NOTE_BLOCK_PLAY);
    }

    @Override
    public String getName() {
        return null;
    }
}
