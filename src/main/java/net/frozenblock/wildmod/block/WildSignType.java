package net.frozenblock.wildmod.block;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.frozenblock.wildmod.mixins.SignTypeInvoker;
import net.minecraft.util.SignType;

import java.util.Set;
import java.util.stream.Stream;

public class WildSignType extends net.minecraft.util.SignType {

    private static final Set<SignType> VALUES = new ObjectArraySet<>();
    public static final SignType MANGROVE = register(new WildSignType("mangrove"));

    private final String name;

    public WildSignType(String name) {
        super(name);
        this.name = name;
    }

    public static SignType register(SignType type) {
        VALUES.add(type);
        return type;
    }

    public static Stream<SignType> stream() {
        return VALUES.stream();
    }

    public String getName() {
        return this.name;
    }
}
