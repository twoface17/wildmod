package net.frozenblock.wildmod.liukrastapi;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.world.gen.random.AbstractRandom;

import java.util.Random;

public class ConstantIntProvider extends IntProvider {
    public static final ConstantIntProvider ZERO = new ConstantIntProvider(0);
    public static final Codec<ConstantIntProvider> CODEC = Codec.either(
                    Codec.INT,
                    RecordCodecBuilder.create(
                            instance -> instance.group(Codec.INT.fieldOf("value").forGetter(provider -> provider.value)).apply(instance, ConstantIntProvider::new)
                    )
            )
            .xmap(either -> (ConstantIntProvider)either.map(ConstantIntProvider::create, provider -> provider), provider -> Either.left(provider.value));
    private final int value;

    public static ConstantIntProvider create(int value) {
        return value == 0 ? ZERO : new ConstantIntProvider(value);
    }

    private ConstantIntProvider(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public int get(AbstractRandom abstractRandom) {
        return this.value;
    }

    public int getMin() {
        return this.value;
    }

    public int getMax() {
        return this.value;
    }

    public IntProviderType<?> getType() {
        return IntProviderType.CONSTANT;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
}
