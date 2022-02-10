package frozenblock.wild.mod.mixins;

import com.mojang.datafixers.util.Pair;
import frozenblock.wild.mod.registry.RegisterWorldgen;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
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
public abstract class VanillaBiomeParametersMixin {

    @Shadow
    @Final
    private RegistryKey<Biome>[][] UNCOMMON_BIOMES;

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange NEAR_INLAND_CONTINENTALNESS;

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange FAR_INLAND_CONTINENTALNESS;

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange[] EROSION_PARAMETERS = new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-1.0F, -0.78F), MultiNoiseUtil.ParameterRange.of(-0.78F, -0.375F), MultiNoiseUtil.ParameterRange.of(-0.375F, -0.2225F), MultiNoiseUtil.ParameterRange.of(-0.2225F, 0.05F), MultiNoiseUtil.ParameterRange.of(0.05F, 0.45F), MultiNoiseUtil.ParameterRange.of(0.45F, 0.55F), MultiNoiseUtil.ParameterRange.of(0.55F, 1.0F)};

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange DEFAULT_PARAMETER = MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F);

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange NON_FROZEN_TEMPERATURE_PARAMETERS;

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange RIVER_CONTINENTALNESS;

    @Shadow
    private void writeBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey<Biome> biome) {
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(0.0F), weirdness, offset), biome));
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(1.0F), weirdness, offset), biome));
    }

    @Shadow
    private void writeCaveBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey<Biome> biome) {
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(1.6f, 2.0f), weirdness, offset), biome));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectBiomes(CallbackInfo ci) {
        UNCOMMON_BIOMES[1][0] = RegisterWorldgen.MANGROVE_SWAMP;
        UNCOMMON_BIOMES[2][0] = RegisterWorldgen.DEEP_DARK;
    }

    @Inject(method = "writeBiomesNearRivers", at = @At("TAIL"))
    private void injectMangroveNearRivers(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness, CallbackInfo ci) {
        this.writeBiomeParameters(parameters, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, RegisterWorldgen.MANGROVE_SWAMP);
    }

    @Inject(method = "writeRiverBiomes", at = @At("TAIL"))
    private void injectMangroveRiverBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness, CallbackInfo ci) {
        this.writeBiomeParameters(parameters, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.combine(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, RegisterWorldgen.MANGROVE_SWAMP);
    }

    @Inject(method = "writeMixedBiomes", at = @At("TAIL"))
    private void injectMangroveMixedBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness, CallbackInfo ci) {
        this.writeBiomeParameters(parameters, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.combine(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0F, RegisterWorldgen.MANGROVE_SWAMP);
    }

    @Inject(method = "writeCaveBiomes", at = @At("HEAD"))
    private void injectDeepDark(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, CallbackInfo ci) {
        this.writeCaveBiomeParameters(parameters, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.of(0.8f, 1.0f), this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0f, RegisterWorldgen.DEEP_DARK);
    }
}
