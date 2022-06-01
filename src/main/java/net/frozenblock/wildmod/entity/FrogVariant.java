package net.frozenblock.wildmod.entity;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public record FrogVariant(Identifier texture) {
    public static FrogVariant TEMPERATE = register("temperate", "textures/entity/frog/temperate_frog.png");
    public static FrogVariant WARM = register("warm", "textures/entity/frog/warm_frog.png");
    public static FrogVariant COLD = register("cold", "textures/entity/frog/cold_frog.png");

    public static void init() {
    }

    private static FrogVariant register(String id, String textureId) {
        return Registry.register(WildRegistry.FROG_VARIANT, new Identifier(WildMod.MOD_ID, id), new FrogVariant(new Identifier(WildMod.MOD_ID, textureId)));
    }
}