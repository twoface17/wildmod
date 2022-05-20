package net.frozenblock.wildmod.block;

import net.frozenblock.wildmod.mixins.SignTypeAccessor;
import net.minecraft.util.SignType;

public class WildSignType extends SignType {
    public static final SignType MANGROVE = SignTypeAccessor.registerNew(SignTypeAccessor.newSignType("mangrove"));

    public WildSignType(String name) {
        super(name);
    }
}
