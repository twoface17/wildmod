package net.frozenblock.wildmod.mixins;

import com.google.common.collect.Maps;
import net.frozenblock.wildmod.registry.RegisterWorldgen;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(VillagerType.class)
public class VillagerTypeMixin {

    @Shadow
    @Final
    @Mutable
    private static final Map<RegistryKey<Biome>, VillagerType> BIOME_TO_TYPE = Util.make(Maps.newHashMap(), map -> {
        map.put(BiomeKeys.BADLANDS, VillagerType.DESERT);
        map.put(BiomeKeys.DESERT, VillagerType.DESERT);
        map.put(BiomeKeys.ERODED_BADLANDS, VillagerType.DESERT);
        map.put(BiomeKeys.WOODED_BADLANDS, VillagerType.DESERT);
        map.put(BiomeKeys.BAMBOO_JUNGLE, VillagerType.JUNGLE);
        map.put(BiomeKeys.JUNGLE, VillagerType.JUNGLE);
        map.put(BiomeKeys.SPARSE_JUNGLE, VillagerType.JUNGLE);
        map.put(BiomeKeys.SAVANNA_PLATEAU, VillagerType.SAVANNA);
        map.put(BiomeKeys.SAVANNA, VillagerType.SAVANNA);
        map.put(BiomeKeys.WINDSWEPT_SAVANNA, VillagerType.SAVANNA);
        map.put(BiomeKeys.DEEP_FROZEN_OCEAN, VillagerType.SNOW);
        map.put(BiomeKeys.FROZEN_OCEAN, VillagerType.SNOW);
        map.put(BiomeKeys.FROZEN_RIVER, VillagerType.SNOW);
        map.put(BiomeKeys.ICE_SPIKES, VillagerType.SNOW);
        map.put(BiomeKeys.SNOWY_BEACH, VillagerType.SNOW);
        map.put(BiomeKeys.SNOWY_TAIGA, VillagerType.SNOW);
        map.put(BiomeKeys.SNOWY_PLAINS, VillagerType.SNOW);
        map.put(BiomeKeys.GROVE, VillagerType.SNOW);
        map.put(BiomeKeys.SNOWY_SLOPES, VillagerType.SNOW);
        map.put(BiomeKeys.FROZEN_PEAKS, VillagerType.SNOW);
        map.put(BiomeKeys.JAGGED_PEAKS, VillagerType.SNOW);
        map.put(BiomeKeys.SWAMP, VillagerType.SWAMP);
        map.put(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, VillagerType.TAIGA);
        map.put(BiomeKeys.OLD_GROWTH_PINE_TAIGA, VillagerType.TAIGA);
        map.put(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, VillagerType.TAIGA);
        map.put(BiomeKeys.WINDSWEPT_HILLS, VillagerType.TAIGA);
        map.put(BiomeKeys.TAIGA, VillagerType.TAIGA);
        map.put(BiomeKeys.WINDSWEPT_FOREST, VillagerType.TAIGA);
        map.put(RegisterWorldgen.MANGROVE_SWAMP, VillagerType.SWAMP);
    });

}
