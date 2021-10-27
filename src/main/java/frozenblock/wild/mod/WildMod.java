package frozenblock.wild.mod;

import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterItems;
import frozenblock.wild.mod.registry.RegisterWorldgen;
import net.fabricmc.api.ModInitializer;

public class WildMod implements ModInitializer {

    public static final String MOD_ID = "twm";

    @Override
    public void onInitialize() {
        RegisterBlocks.RegisterBlocks();
        RegisterItems.RegisterItems();
        RegisterWorldgen.RegisterWorldgen();
    }
}
