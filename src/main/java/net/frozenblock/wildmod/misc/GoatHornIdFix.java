package net.frozenblock.wildmod.misc;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class GoatHornIdFix extends ItemNbtFix {
    private static final String[] GOAT_HORN_IDS = new String[]{
            "twm:ponder_goat_horn",
            "twm:sing_goat_horn",
            "twm:seek_goat_horn",
            "twm:feel_goat_horn",
            "twm:admire_goat_horn",
            "twm:call_goat_horn",
            "twm:yearn_goat_horn",
            "twm:dream_goat_horn"
    };

    public GoatHornIdFix(Schema schema) {
        super(schema, "GoatHornIdFix", itemId -> itemId.equals("twm:goat_horn"));
    }

    protected <T> Dynamic<T> fixNbt(Dynamic<T> dynamic) {
        int i = dynamic.get("SoundVariant").asInt(0);
        String string = GOAT_HORN_IDS[i >= 0 && i < GOAT_HORN_IDS.length ? i : 0];
        return dynamic.remove("SoundVariant").set("instrument", dynamic.createString(string));
    }
}
