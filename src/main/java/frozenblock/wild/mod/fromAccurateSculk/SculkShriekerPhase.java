package frozenblock.wild.mod.fromAccurateSculk;

import net.minecraft.util.StringIdentifiable;

public enum SculkShriekerPhase implements StringIdentifiable
{
    INACTIVE("inactive"),
    ACTIVE("active"),
    COOLDOWN("cooldown");

    private final String name;

    SculkShriekerPhase(String string2) {
        this.name = string2;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}