package net.frozenblock.wildmod.items;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface InstrumentTags {
    TagKey<Instrument> REGULAR_GOAT_HORNS = of("regular_goat_horns");
    TagKey<Instrument> SCREAMING_GOAT_HORNS = of("screaming_goat_horns");
    TagKey<Instrument> GOAT_HORNS = of("goat_horns");

    private static TagKey<Instrument> of(String id) {
        return TagKey.of(WildRegistry.INSTRUMENT_KEY, new Identifier(WildMod.MOD_ID, id));
    }
}
