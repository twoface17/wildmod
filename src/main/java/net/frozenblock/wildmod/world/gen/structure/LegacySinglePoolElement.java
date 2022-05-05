package net.frozenblock.wildmod.world.gen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.registry.RegistryEntry;

import static net.frozenblock.wildmod.world.gen.structure.StructurePoolElement.projectionGetter;

public class LegacySinglePoolElement extends SinglePoolElement {
    public static final Codec<LegacySinglePoolElement> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(locationGetter(), processorsGetter(), projectionGetter()).apply(instance, LegacySinglePoolElement::new)
    );

    protected LegacySinglePoolElement(Either<Identifier, Structure> either, RegistryEntry<StructureProcessorList> registryEntry, StructurePool.Projection projection) {
        super(either, registryEntry, projection);
    }

    protected StructurePlacementData createPlacementData(BlockRotation rotation, BlockBox box, boolean keepJigsaws) {
        StructurePlacementData structurePlacementData = super.createPlacementData(rotation, box, keepJigsaws);
        structurePlacementData.removeProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
        structurePlacementData.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
        return structurePlacementData;
    }

    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.LEGACY_SINGLE_POOL_ELEMENT;
    }

    public String toString() {
        return "LegacySingle[" + this.location + "]";
    }
}
