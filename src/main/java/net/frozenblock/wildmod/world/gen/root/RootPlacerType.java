package net.frozenblock.wildmod.world.gen.root;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.registry.WildRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RootPlacerType<P extends RootPlacer> {
    public static RootPlacerType<MangroveRootPlacer> MANGROVE_ROOT_PLACER;
    private final Codec<P> codec;

    public static void registerRootTypes() {
        MANGROVE_ROOT_PLACER = register("mangrove_root_placer", MangroveRootPlacer.CODEC);
    }

    private static <P extends RootPlacer> RootPlacerType<P> register(String id, Codec<P> codec) {
        return Registry.register(WildRegistry.ROOT_PLACER_TYPE, new Identifier(WildMod.MOD_ID, id), new RootPlacerType<>(codec));
    }

    private RootPlacerType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }
}
