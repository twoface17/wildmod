/*package net.frozenblock.wildmod.world.gen.structure;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.registry.Registry;
import net.frozenblock.wildmod.world.gen.JigsawStructure;

public interface StructureType<S extends net.frozenblock.wildmod.world.gen.StructureType> {
    StructureType<JigsawStructure> JIGSAW = register("jigsaw", JigsawStructure.CODEC);

    Codec<S> codec();

    private static <S extends net.frozenblock.wildmod.world.gen.StructureType> StructureType<S> register(String id, Codec<S> codec) {
        return Registry.register(Registry.STRUCTURE_TYPE, id, (StructureType<S>)() -> codec);
    }
}
*/