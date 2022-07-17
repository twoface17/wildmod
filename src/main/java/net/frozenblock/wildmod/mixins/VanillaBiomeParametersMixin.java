package net.frozenblock.wildmod.mixins;

import com.mojang.datafixers.util.Pair;
import net.frozenblock.wildmod.registry.RegisterWorldgen;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(VanillaBiomeParameters.class)
public final class VanillaBiomeParametersMixin {
    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange riverContinentalness;
    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange farInlandContinentalness;
    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange[] erosionParameters;
    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange nearInlandContinentalness;
    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange[] temperatureParameters;
    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange defaultParameter;
    @Shadow
    @Final
    private RegistryKey<Biome>[][] uncommonBiomes;

    private void writeBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, final float offset, RegistryKey<Biome> biome) {
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(0.0F, 1.0F), weirdness, offset), biome));
    }

    private void writeCaveBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, final float offset, RegistryKey<Biome> biome) {
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(0.825F, 1.5F), weirdness, offset), biome));
    }

    private void method_41419(
            Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer,
            MultiNoiseUtil.ParameterRange parameterRange,
            MultiNoiseUtil.ParameterRange parameterRange2,
            MultiNoiseUtil.ParameterRange parameterRange3,
            MultiNoiseUtil.ParameterRange parameterRange4,
            MultiNoiseUtil.ParameterRange parameterRange5,
            float f,
            RegistryKey<Biome> registryKey
    ) {
        consumer.accept(
                Pair.of(
                        MultiNoiseUtil.createNoiseHypercube(
                                parameterRange, parameterRange2, parameterRange3, parameterRange4, MultiNoiseUtil.ParameterRange.of(1.1F), parameterRange5, f
                        ),
                        registryKey
                )
        );
    }

    @Inject(method = "writeBiomesNearRivers", at = @At("TAIL"))
    private void writeBiomesNearRivers(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness, CallbackInfo ci) {
        this.writeBiomeParameters(
                parameters,
                MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[3], this.temperatureParameters[4]),
                this.defaultParameter,
                MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness),
                this.erosionParameters[6],
                weirdness,
                0.0F,
                RegisterWorldgen.MANGROVE_SWAMP
        );
    }

    @Inject(method = "writeRiverBiomes", at = @At("TAIL"))
    private void injectMangroveRiverBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness, CallbackInfo ci) {
        this.writeBiomeParameters(
                parameters,
                MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[3], this.temperatureParameters[4]),
                this.defaultParameter,
                MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.farInlandContinentalness),
                this.erosionParameters[6],
                weirdness,
                0.0F,
                RegisterWorldgen.MANGROVE_SWAMP
        );
    }

    @Inject(method = "writeMixedBiomes", at = @At("TAIL"))
    private void injectMangroveMixedBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness, CallbackInfo ci) {
        this.writeBiomeParameters(
                parameters,
                MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[3], this.temperatureParameters[4]),
                this.defaultParameter,
                MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness),
                this.erosionParameters[6],
                weirdness,
                0.0F,
                RegisterWorldgen.MANGROVE_SWAMP
        );
    }

    @Inject(method = "writeCaveBiomes", at = @At("TAIL"))
    private void injectDeepDark(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, CallbackInfo ci) {
        this.method_41419(
                parameters,
                this.defaultParameter,
                this.defaultParameter,
                this.defaultParameter,
                MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]),
                this.defaultParameter,
                0.0F,
                RegisterWorldgen.DEEP_DARK
        );
    }
}
