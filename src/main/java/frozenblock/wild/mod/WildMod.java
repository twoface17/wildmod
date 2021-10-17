package frozenblock.wild.mod;

import frozenblock.wild.mod.registry.MangroveWood;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.ModItems;
import net.fabricmc.api.ModInitializer;

public class WildMod implements ModInitializer {

    public static final String MOD_ID = "twm";

    @Override
    public void onInitialize() {
        RegisterBlocks.RegisterBlocks();
        ModItems.RegisterItems();
        MangroveWood.RegisterMangrove();
    }
}
