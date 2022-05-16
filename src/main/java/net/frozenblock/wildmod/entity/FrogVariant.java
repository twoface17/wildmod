package net.frozenblock.wildmod.entity;

import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.RegisterRegistries;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.minecraft.util.Identifier;

public record FrogVariant(Identifier texture) {
    public static final FrogVariant TEMPERATE = register("temperate", "textures/entity/frog/temperate_frog.png");
    public static final FrogVariant WARM = register("warm", "textures/entity/frog/warm_frog.png");
    public static final FrogVariant COLD = register("cold", "textures/entity/frog/cold_frog.png");

    private static FrogVariant register(String id, String textureId) {
        return WildRegistry.register(RegisterRegistries.FROG_VARIANT, new Identifier(WildMod.MOD_ID, id), new FrogVariant(new Identifier(WildMod.MOD_ID, textureId)));
    }
}