package net.frozenblock.wildmod.world.feature.foliage;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.minecraft.util.registry.Registry;

public class WildFoliagePlacerType<P extends WildFoliagePlacer> {
    public static final WildFoliagePlacerType<WildBlobFoliagePlacer> BLOB_FOLIAGE_PLACER;
    public static final WildFoliagePlacerType<WildSpruceFoliagePlacer> SPRUCE_FOLIAGE_PLACER;
    public static final WildFoliagePlacerType<WildPineFoliagePlacer> PINE_FOLIAGE_PLACER;
    public static final WildFoliagePlacerType<WildAcaciaFoliagePlacer> ACACIA_FOLIAGE_PLACER;
    public static final WildFoliagePlacerType<WildBushFoliagePlacer> BUSH_FOLIAGE_PLACER;
    public static final WildFoliagePlacerType<WildLargeOakFoliagePlacer> FANCY_FOLIAGE_PLACER;
    public static final WildFoliagePlacerType<WildJungleFoliagePlacer> JUNGLE_FOLIAGE_PLACER;
    public static final WildFoliagePlacerType<WildMegaPineFoliagePlacer> MEGA_PINE_FOLIAGE_PLACER;
    public static final WildFoliagePlacerType<WildDarkOakFoliagePlacer> DARK_OAK_FOLIAGE_PLACER;
    public static final WildFoliagePlacerType<WildRandomSpreadFoliagePlacer> RANDOM_SPREAD_FOLIAGE_PLACER;
    private final Codec<P> codec;

    private static <P extends WildFoliagePlacer> WildFoliagePlacerType<P> register(String id, Codec<P> codec) {
        return Registry.register(WildRegistry.FOLIAGE_PLACER_TYPE, id, new WildFoliagePlacerType<>(codec));
    }

    private WildFoliagePlacerType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }

    static {
        BLOB_FOLIAGE_PLACER = register("blob_foliage_placer", WildBlobFoliagePlacer.CODEC);
        SPRUCE_FOLIAGE_PLACER = register("spruce_foliage_placer", WildSpruceFoliagePlacer.CODEC);
        PINE_FOLIAGE_PLACER = register("pine_foliage_placer", WildPineFoliagePlacer.CODEC);
        ACACIA_FOLIAGE_PLACER = register("acacia_foliage_placer", WildAcaciaFoliagePlacer.CODEC);
        BUSH_FOLIAGE_PLACER = register("bush_foliage_placer", WildBushFoliagePlacer.CODEC);
        FANCY_FOLIAGE_PLACER = register("fancy_foliage_placer", WildLargeOakFoliagePlacer.CODEC);
        JUNGLE_FOLIAGE_PLACER = register("jungle_foliage_placer", WildJungleFoliagePlacer.CODEC);
        MEGA_PINE_FOLIAGE_PLACER = register("mega_pine_foliage_placer", WildMegaPineFoliagePlacer.CODEC);
        DARK_OAK_FOLIAGE_PLACER = register("dark_oak_foliage_placer", WildDarkOakFoliagePlacer.CODEC);
        RANDOM_SPREAD_FOLIAGE_PLACER = register("random_spread_foliage_placer", WildRandomSpreadFoliagePlacer.CODEC);
    }

    public static void init() {
    }
}
