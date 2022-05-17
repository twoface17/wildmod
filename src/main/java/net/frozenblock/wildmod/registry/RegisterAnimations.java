package net.frozenblock.wildmod.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wildmod.liukrastapi.Transformation;

@Environment(EnvType.CLIENT)
public class RegisterAnimations {
    public static void register() {
        Transformation.Interpolations.registerInterpolations();
    }
}
