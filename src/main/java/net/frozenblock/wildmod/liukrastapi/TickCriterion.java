package net.frozenblock.wildmod.liukrastapi;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TickCriterion extends AbstractCriterion<TickCriterion.Conditions> {
    final Identifier id;
    public static final TickCriterion AVOID_VIBRATION = Criteria.register(new TickCriterion(new Identifier("avoid_vibrations")));

    public TickCriterion(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return this.id;
    }

    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        return new TickCriterion.Conditions(this.id, extended);
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, (conditions) -> {
            return true;
        });
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions(Identifier identifier, EntityPredicate.Extended extended) {
            super(identifier, extended);
        }

        //public static Conditions createLocation(LocationPredicate location) {
            //return new Conditions(Criteria.LOCATION.id, EntityPredicate.Extended.ofLegacy(EntityPredicate.Builder.create().location(location).build()));
        //}

        //public static Conditions createLocation(EntityPredicate entity) {
            //return new Conditions(Criteria.LOCATION.id, EntityPredicate.Extended.ofLegacy(entity));
        //}

       // public static Conditions createSleptInBed() {
            //return new Conditions(Criteria.SLEPT_IN_BED.id, EntityPredicate.Extended.EMPTY);
       // }

       // public static Conditions createHeroOfTheVillage() {
            //return new Conditions(Criteria.HERO_OF_THE_VILLAGE.id, EntityPredicate.Extended.EMPTY);
        //}

        //public static Conditions method_43279() {
            //return new Conditions(AVOID_VIBRATION.id, EntityPredicate.Extended.EMPTY);
        //}

        //public static Conditions createLocation(Block block, Item item) {
            //return createLocation(EntityPredicate.Builder.create().equipment(EntityEquipmentPredicate.Builder.create().feet(ItemPredicate.Builder.create().items(new ItemConvertible[]{item}).build()).build()).steppingOn(LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(new Block[]{block}).build()).build()).build());
        //}
    }

}
