package frozenblock.wild.mod.mixins;

import com.google.common.collect.Maps;
import frozenblock.wild.mod.registry.RegisterWorldgen;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(VillagerType.class)
public class VillagerTypeMixin {

    private static final Map<RegistryKey<Biome>, VillagerType> BIOME_TO_TYPE_CUSTOM = Util.make(Maps.newHashMap(), map -> {
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

    @Inject(at = @At("RETURN"), method = "forBiome", cancellable = true)
    private static void forBiome(RegistryEntry<Biome> biomeEntry, CallbackInfoReturnable<VillagerType> cir) {
        cir.setReturnValue(biomeEntry.getKey().flatMap(biomeKey -> Optional.ofNullable(BIOME_TO_TYPE_CUSTOM.get(biomeKey))).orElse(VillagerType.PLAINS));
    }

}
