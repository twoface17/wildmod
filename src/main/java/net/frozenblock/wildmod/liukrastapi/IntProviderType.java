package net.frozenblock.wildmod.liukrastapi;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.intprovider.*;
import net.minecraft.util.registry.Registry;

public interface IntProviderType<P extends IntProvider> extends net.minecraft.util.math.intprovider.IntProviderType {
    net.minecraft.util.math.intprovider.IntProviderType<ConstantIntProvider> CONSTANT = register("constant", ConstantIntProvider.CODEC);
    net.minecraft.util.math.intprovider.IntProviderType<UniformIntProvider> UNIFORM = register("uniform", UniformIntProvider.CODEC);
    net.minecraft.util.math.intprovider.IntProviderType<BiasedToBottomIntProvider> BIASED_TO_BOTTOM = register("biased_to_bottom", BiasedToBottomIntProvider.CODEC);
    net.minecraft.util.math.intprovider.IntProviderType<ClampedIntProvider> CLAMPED = register("clamped", ClampedIntProvider.CODEC);
    net.minecraft.util.math.intprovider.IntProviderType<WeightedListIntProvider> WEIGHTED_LIST = register("weighted_list", WeightedListIntProvider.CODEC);
    net.minecraft.util.math.intprovider.IntProviderType<ClampedNormalIntProvider> CLAMPED_NORMAL = register("clamped_normal", ClampedNormalIntProvider.CODEC);

    Codec<P> codec();

    static <P extends IntProvider> net.minecraft.util.math.intprovider.IntProviderType<P> register(String id, Codec<P> codec) {
        return (net.minecraft.util.math.intprovider.IntProviderType<P>) Registry.register(Registry.INT_PROVIDER_TYPE, id, (net.minecraft.util.math.intprovider.IntProviderType<>)() -> codec);
    }
}
