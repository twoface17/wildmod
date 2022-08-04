package net.frozenblock.wildmod.registry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.frozenblock.wildmod.misc.TickCriterion;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class RegisterAdvancements extends FabricAdvancementProvider {
    public RegisterAdvancements(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }


    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        Advancement advancement = net.minecraft.advancement.Advancement.Builder.create()
                .display(
                        Items.MAP,
                        new TranslatableText("advancements.adventure.root.title"),
                        new TranslatableText("advancements.adventure.root.description"),
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criteriaMerger(CriterionMerger.OR)
                .criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity())
                .criterion("killed_by_something", OnKilledCriterion.Conditions.createEntityKilledPlayer())
                .build(consumer, "adventure/root");
        Advancement.Builder.create()
                .parent(advancement)
                .display(RegisterBlocks.SCULK_CATALYST,
                        new TranslatableText("twm:advancements.adventure.kill_mob_near_sculk_catalyst.title"),
                        new TranslatableText("twm:advancements.adventure.kill_mob_near_sculk_catalyst.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .criterion("kill_mob_near_sculk_catalyst", TickCriterion.createKillMobNearSculkCatalyst())
                .build(
                        consumer, "twm:adventure/kill_mob_near_sculk_catalyst");
    }
}
