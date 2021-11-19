package frozenblock.wild.mod.liukrastapi;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.Set;
import java.util.stream.Stream;

public class CustomSignType {
    private static final Set<frozenblock.wild.mod.liukrastapi.CustomSignType> VALUES = new ObjectArraySet();
    public static final frozenblock.wild.mod.liukrastapi.CustomSignType MANGROVE = register(new frozenblock.wild.mod.liukrastapi.CustomSignType("mangrove"));
    private final String name;

    protected CustomSignType(String name) {
        this.name = name;
    }

    private static frozenblock.wild.mod.liukrastapi.CustomSignType register(frozenblock.wild.mod.liukrastapi.CustomSignType type) {
        VALUES.add(type);
        return type;
    }

    public static Stream<frozenblock.wild.mod.liukrastapi.CustomSignType> stream() {
        return VALUES.stream();
    }

    public String getName() {
        return this.name;
    }
}