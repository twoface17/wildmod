package net.frozenblock.wildmod.registry;

import net.frozenblock.wildmod.WildMod;
import net.minecraft.advancement.criterion.*;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.Identifier;

public class RegisterCriteria {
    public static final LocationArrivalCriterion AVOID_VIBRATION = Criteria.register(new LocationArrivalCriterion(new Identifier(WildMod.MOD_ID, "avoid_vibrations")));
    public static final OnKilledCriterion KILL_MOB_NEAR_SCULK_CATALYST = Criteria.register(new OnKilledCriterion(new Identifier(WildMod.MOD_ID, "kill_mob_near_sculk_catalyst")));

    public static OnKilledCriterion.Conditions createKillMobNearSculkCatalyst() {
        return new OnKilledCriterion.Conditions(RegisterCriteria.KILL_MOB_NEAR_SCULK_CATALYST.getId(), EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, DamageSourcePredicate.EMPTY);
    }

    public static void init() {
    }

}
