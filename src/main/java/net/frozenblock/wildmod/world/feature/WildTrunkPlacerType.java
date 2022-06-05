package net.frozenblock.wildmod.world.feature;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.frozenblock.wildmod.world.gen.UpwardsBranchingTrunkPlacer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WildTrunkPlacerType<P extends WildTrunkPlacer> {

    public static final WildTrunkPlacerType<WildStraightTrunkPlacer> STRAIGHT_TRUNK_PLACER;
    public static final WildTrunkPlacerType<UpwardsBranchingTrunkPlacer> UPWARDS_BRANCHING_TRUNK_PLACER;
    private final Codec<P> codec;

    private static <P extends WildTrunkPlacer> WildTrunkPlacerType<P> register(String id, Codec<P> codec) {
        return Registry.register(WildRegistry.TRUNK_PLACER_TYPE, new Identifier(WildMod.MOD_ID, id), new WildTrunkPlacerType<>(codec));
    }

    public WildTrunkPlacerType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }

    static {
        STRAIGHT_TRUNK_PLACER = register("straight_trunk_placer", WildStraightTrunkPlacer.CODEC);
        UPWARDS_BRANCHING_TRUNK_PLACER = register("upwards_branching_trunk_placer", UpwardsBranchingTrunkPlacer.CODEC);
    }

    public static void init() {
    }
}
