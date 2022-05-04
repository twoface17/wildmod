package net.frozenblock.wildmod.world.gen.structure.ancientcity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.RegisterWorldgen;
import net.frozenblock.wildmod.world.gen.structure.SinglePoolElement;
import net.frozenblock.wildmod.world.gen.structure.StructurePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;

import java.util.function.Function;

public class AncientCityGenerator
{
    public static final RegistryEntry<StructurePool> CITY_CENTER = StructurePools.register(
            new StructurePool(
                    new Identifier("ancient_city/city_center"),
                    new Identifier("empty"),
                    ImmutableList.of(
                            Pair.of(
                                    StructurePoolElement.ofProcessedSingle("ancient_city/city_center/city_center_1", RegisterWorldgen.ANCIENT_CITY_START_DEGRADATION), 1
                            ),
                            Pair.of(
                                    StructurePoolElement.ofProcessedSingle("ancient_city/city_center/city_center_2", RegisterWorldgen.ANCIENT_CITY_START_DEGRADATION), 1
                            ),
                            Pair.of(
                                    StructurePoolElement.ofProcessedSingle("ancient_city/city_center/city_center_3", RegisterWorldgen.ANCIENT_CITY_START_DEGRADATION), 1
                            )
                    ),
                    StructurePool.Projection.RIGID
            )
    );

    public AncientCityGenerator() {
    }

    public static void init() {
        AncientCityOutskirtsGenerator.init();
    }

    public static Function<StructurePool.Projection, SinglePoolElement> ofProcessedSingle(String id, RegistryEntry<StructureProcessorList> registryEntry) {
        return projection -> new net.frozenblock.wildmod.world.gen.structure.SinglePoolElement(Either.left(new Identifier(WildMod.MOD_ID, id)), registryEntry, projection);
    }
}
