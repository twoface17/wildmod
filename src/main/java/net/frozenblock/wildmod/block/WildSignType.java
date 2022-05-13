package net.frozenblock.wildmod.block;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.util.SignType;

import java.util.Set;
import java.util.stream.Stream;

public class WildSignType extends SignType {
    public static final SignType MANGROVE = SignType.register(new SignType("mangrove"));

    public WildSignType(String name) {
        super(name);
    }
}
