package net.frozenblock.wildmod.world.gen.root;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.Registry;
import net.minecraft.util.Identifier;

public class RootPlacerType<P extends RootPlacer> {
    public static final RootPlacerType<MangroveRootPlacer> MANGROVE_ROOT_PLACER = register("mangrove_root_placer", MangroveRootPlacer.CODEC);
    private final Codec<P> codec;

    private static <P extends RootPlacer> RootPlacerType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.ROOT_PLACER_TYPE, new Identifier(WildMod.MOD_ID, id), new RootPlacerType<>(codec));
    }

    private RootPlacerType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }
}
