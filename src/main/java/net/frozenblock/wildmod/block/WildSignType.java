package net.frozenblock.wildmod.block;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.frozenblock.wildmod.mixins.SignTypeAccessor;
import net.minecraft.util.SignType;

import java.util.Set;
import java.util.stream.Stream;

public class WildSignType extends SignType {
    public static final SignType MANGROVE = SignTypeAccessor.registerNew(SignTypeAccessor.newSignType("mangrove"));

    public WildSignType(String name) {
        super(name);
    }
}
