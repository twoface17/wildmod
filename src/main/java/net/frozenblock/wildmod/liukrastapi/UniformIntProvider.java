package net.frozenblock.wildmod.liukrastapi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.world.gen.random.AbstractRandom;

import java.util.Random;
import java.util.function.Function;

public class UniformIntProvider extends IntProvider {
    public static final Codec<UniformIntProvider> CODEC = RecordCodecBuilder.create(
                    instance -> instance.group(
                                    Codec.INT.fieldOf("min_inclusive").forGetter(provider -> provider.min),
                                    Codec.INT.fieldOf("max_inclusive").forGetter(provider -> provider.max)
                            )
                            .apply(instance, UniformIntProvider::new)
            )
            .comapFlatMap(
                    provider -> provider.max < provider.min
                            ? DataResult.error("Max must be at least min, min_inclusive: " + provider.min + ", max_inclusive: " + provider.max)
                            : DataResult.success(provider),
                    Function.identity()
            );
    private final int min;
    private final int max;

    private UniformIntProvider(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static UniformIntProvider create(int min, int max) {
        return new UniformIntProvider(min, max);
    }

    public int get(AbstractRandom abstractRandom) {
        return MathAddon.nextBetween(abstractRandom, this.min, this.max);
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public IntProviderType<?> getType() {
        return IntProviderType.UNIFORM;
    }

    public String toString() {
        return "[" + this.min + "-" + this.max + "]";
    }
}
