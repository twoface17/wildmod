package frozenblock.wild.mod;

import frozenblock.wild.mod.registry.*;
import net.fabricmc.api.ModInitializer;

public class WildMod implements ModInitializer {

    public static final String MOD_ID = "twm";

    @Override
    public void onInitialize() {
        RegisterBlocks.RegisterBlocks();
        RegisterItems.RegisterItems();
        RegisterEntities.RegisterEntities();
        RegisterDispenser.RegisterDispenser();
        RegisterParticles.RegisterParticles();
        RegisterSounds.RegisterSounds();
        RegisterStatusEffects.RegisterStatusEffects();
        RegisterWorldgen.RegisterWorldgen();
    }
}
