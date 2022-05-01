package net.frozenblock.wildmod.event;

import net.frozenblock.wildmod.WildMod;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class WildEventTags {
    public static final TagKey<GameEvent> WARDEN_CAN_LISTEN = of("warden_can_listen");
    public static final TagKey<GameEvent> SHRIEKER_CAN_LISTEN = of("shrieker_can_listen");
    public static final TagKey<GameEvent> ALLAY_CAN_LISTEN = of("allay_can_listen");

    private WildEventTags() {
    }

    private static TagKey<net.minecraft.world.event.GameEvent> of(String id) {
        return TagKey.of(Registry.GAME_EVENT_KEY, new Identifier(WildMod.MOD_ID, id));
    }
}
