package net.frozenblock.wildmod.world.gen.structure;

import net.minecraft.util.StringIdentifiable;


public enum StructureTerrainAdaptation implements StringIdentifiable {
    NONE("none"),
    BURY("bury"),
    BEARD_THIN("beard_thin"),
    BEARD_BOX("beard_box");

    public static final Codec<StructureTerrainAdaptation> CODEC = Codec.createCodec(StructureTerrainAdaptation::values);
    private final String name;

    StructureTerrainAdaptation(String name) {
        this.name = name;
    }

    public String asString() {
        return this.name;
    }

}
