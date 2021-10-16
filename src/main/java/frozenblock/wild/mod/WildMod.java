package frozenblock.wild.mod;

import frozenblock.wild.mod.registry.ModBlocks;
import frozenblock.wild.mod.registry.ModItems;
import net.fabricmc.api.ModInitializer;

public class WildMod implements ModInitializer {

    public static final String MOD_ID = "twm";

    @Override
    public void onInitialize() {
        ModBlocks.RegisterBlocks();
        ModItems.RegisterItems();
    }
}
