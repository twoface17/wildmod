package net.frozenblock.wildmod.liukrastapi;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OnKilledCriterionCatalyst extends OnKilledCriterion {

    final Identifier id;

    public OnKilledCriterionCatalyst(Identifier id) {
        super(id);
        this.id = id;
    }

    public Identifier getId() {
        return this.id;
    }

    public OnKilledCriterion.Conditions conditionsFromJson(
            JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
    ) {
        return new OnKilledCriterion.Conditions(
                this.id,
                extended,
                EntityPredicate.Extended.getInJson(jsonObject, "entity", advancementEntityPredicateDeserializer),
                DamageSourcePredicate.fromJson(jsonObject.get("killing_blow"))
        );
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final EntityPredicate.Extended entity;
        private final DamageSourcePredicate killingBlow;

        public Conditions(Identifier id, EntityPredicate.Extended player, EntityPredicate.Extended entity, DamageSourcePredicate killingBlow) {
            super(id, player);
            this.entity = entity;
            this.killingBlow = killingBlow;
        }

        public static OnKilledCriterionCatalyst.Conditions createKillMobNearSculkCatalyst() {
            return new OnKilledCriterionCatalyst.Conditions(Criteria.KILL_MOB_NEAR_SCULK_CATALYST.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, DamageSourcePredicate.EMPTY);
        }

        public boolean test(ServerPlayerEntity player, LootContext killedEntityContext, DamageSource killingBlow) {
            return !this.killingBlow.test(player, killingBlow) ? false : this.entity.test(killedEntityContext);
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("entity", this.entity.toJson(predicateSerializer));
            jsonObject.add("killing_blow", this.killingBlow.toJson());
            return jsonObject;
        }
    }
}
