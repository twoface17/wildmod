package net.frozenblock.wildmod.liukrastapi;

import com.google.common.collect.Maps;
import net.frozenblock.wildmod.WildMod;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.util.Identifier;

import java.util.Map;

public class Criteria {

    private static final Map<Identifier, Criterion<?>> VALUES = Maps.newHashMap();
    public static OnKilledCriterionCatalyst KILL_MOB_NEAR_SCULK_CATALYST;

    public static void RegisterCriterions() {
        KILL_MOB_NEAR_SCULK_CATALYST = register(new OnKilledCriterionCatalyst(new Identifier(WildMod.MOD_ID, "kill_mob_near_sculk_catalyst")));
    }

    public static <T extends Criterion<?>> T register(T object) {
        if (VALUES.containsKey(object.getId())) {
            throw new IllegalArgumentException("Duplicate criterion id " + object.getId());
        } else {
            VALUES.put(object.getId(), object);
            return object;
        }
    }
}
