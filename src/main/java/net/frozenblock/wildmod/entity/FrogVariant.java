package net.frozenblock.wildmod.entity;

import net.minecraft.util.Identifier;

public record FrogVariant(Identifier texture) {
    public static FrogVariant TEMPERATE;
    public static FrogVariant WARM;
    public static FrogVariant COLD;

    //public static void registerFrogVariants() {
        //TEMPERATE = register("temperate", "textures/entity/frog/temperate_frog.png");
        //WARM = register("warm", "textures/entity/frog/warm_frog.png");
        //COLD = register("cold", "textures/entity/frog/cold_frog.png");
    //}

    //private static FrogVariant register(String id, String textureId) {
        //return Registry.register(WildRegistry.FROG_VARIANT, new Identifier(WildMod.MOD_ID, id), new FrogVariant(new Identifier(WildMod.MOD_ID, textureId)));
    //}
}