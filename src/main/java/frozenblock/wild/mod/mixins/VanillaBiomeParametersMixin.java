package frozenblock.wild.mod.mixins;

import com.mojang.datafixers.util.Pair;
import frozenblock.wild.mod.registry.RegisterWorldgen;
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
    @Shadow @Final private MultiNoiseUtil.ParameterRange riverContinentalness;
    @Shadow @Final private MultiNoiseUtil.ParameterRange farInlandContinentalness;
    @Shadow @Final private MultiNoiseUtil.ParameterRange[] erosionParameters;
    @Shadow @Final private MultiNoiseUtil.ParameterRange nearInlandContinentalness;
    @Shadow @Final private MultiNoiseUtil.ParameterRange nonFrozenTemperatureParameters;
    @Shadow @Final private MultiNoiseUtil.ParameterRange defaultParameter;
    @Shadow @Final private RegistryKey<Biome>[][] uncommonBiomes;

    private void writeBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, final float offset, RegistryKey<Biome> biome) {
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(0.0F, 1.0F), weirdness, offset), biome));
    }

    private void writeCaveBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, final float offset, RegistryKey<Biome> biome) {
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(0.825F, 1.5F), weirdness, offset), biome));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectBiomes(CallbackInfo ci) {
        uncommonBiomes[1][0] = RegisterWorldgen.MANGROVE_SWAMP;
        uncommonBiomes[2][0] = RegisterWorldgen.DEEP_DARK;
    }

    @Inject(method = "writeBiomesNearRivers", at = @At("TAIL"))
    private void injectMangroveNearRivers(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness, CallbackInfo ci) {
        this.writeBiomeParameters(parameters, nonFrozenTemperatureParameters, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, RegisterWorldgen.MANGROVE_SWAMP);
    }

    @Inject(method = "writeRiverBiomes", at = @At("TAIL"))
    private void injectMangroveRiverBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness, CallbackInfo ci) {
        this.writeBiomeParameters(parameters, this.nonFrozenTemperatureParameters, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, RegisterWorldgen.MANGROVE_SWAMP);
    }

    @Inject(method = "writeMixedBiomes", at = @At("TAIL"))
    private void injectMangroveMixedBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness, CallbackInfo ci) {
        this.writeBiomeParameters(parameters, this.nonFrozenTemperatureParameters, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, RegisterWorldgen.MANGROVE_SWAMP);
    }

    @Inject(method = "writeCaveBiomes", at = @At("RETURN"))
    private void injectDeepDark(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, CallbackInfo ci) {
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.4F, 1.0F), MultiNoiseUtil.ParameterRange.of(0.4F, 0.45F), this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.825F, 1.5F), this.defaultParameter, 0.0F), RegisterWorldgen.DEEP_DARK));
        this.writeCaveBiomeParameters(parameters, this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.8f, 1.0F), MultiNoiseUtil.ParameterRange.of(0.0F, 1.0F), MultiNoiseUtil.ParameterRange.of(0.0F, 1.0F), 0.0f, RegisterWorldgen.DEEP_DARK);
    }
}
