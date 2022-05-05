/*package net.frozenblock.wildmod.world.gen;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.random.RandomDeriver;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class NoiseConfig {
    final RandomDeriver randomDeriver;
    private final long legacyWorldSeed;
    private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegistry;
    private final NoiseRouter noiseRouter;
    private final MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler;
    private final SurfaceBuilder surfaceBuilder;
    private final RandomDeriver aquiferRandomDeriver;
    private final RandomDeriver oreRandomDeriver;
    private final Map<RegistryKey<DoublePerlinNoiseSampler.NoiseParameters>, DoublePerlinNoiseSampler> field_38262;
    private final Map<Identifier, RandomDeriver> randomDerivers;

    public static NoiseConfig create(
            DynamicRegistryManager dynamicRegistryManager, RegistryKey<ChunkGeneratorSettings> chunkGeneratorSettingsKey, long legacyWorldSeed
    ) {
        return create(
                (ChunkGeneratorSettings)dynamicRegistryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getOrThrow(chunkGeneratorSettingsKey),
                dynamicRegistryManager.get(net.frozenblock.wildmod.registry.Registry.NOISE_KEY),
                legacyWorldSeed
        );
    }

    public static NoiseConfig create(ChunkGeneratorSettings chunkGeneratorSettings, Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersRegistry, long legacyWorldSeed) {
        return new NoiseConfig(chunkGeneratorSettings, noiseParametersRegistry, legacyWorldSeed);
    }

    private NoiseConfig(ChunkGeneratorSettings chunkGeneratorSettings, Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry, long seed) {
        this.randomDeriver = chunkGeneratorSettings.getRandomProvider().create(seed).createRandomDeriver();
        this.legacyWorldSeed = seed;
        this.noiseParametersRegistry = noiseRegistry;
        this.aquiferRandomDeriver = this.randomDeriver.createRandom(new Identifier("aquifer")).createRandomDeriver();
        this.oreRandomDeriver = this.randomDeriver.createRandom(new Identifier("ore")).createRandomDeriver();
        this.field_38262 = new ConcurrentHashMap();
        this.randomDerivers = new ConcurrentHashMap();
        this.surfaceBuilder = new SurfaceBuilder(this, chunkGeneratorSettings.defaultBlock(), chunkGeneratorSettings.seaLevel(), this.randomDeriver);
        final boolean bl = chunkGeneratorSettings.usesLegacyRandom();

        class class_7271 implements DensityFunction.DensityFunctionVisitor {
            private final Map<DensityFunction, DensityFunction> field_38267 = new HashMap();

            class_7271() {
            }

            private AbstractRandom method_42375(long l) {
                return new AtomicSimpleRandom(seed + l);
            }

            public class_7270 method_42358(class_7270 arg) {
                RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = arg.noiseData();
                if (bl) {
                    if (Objects.equals(registryEntry.getKey(), Optional.of(NoiseParametersKeys.TEMPERATURE))) {
                        DoublePerlinNoiseSampler doublePerlinNoiseSampler = DoublePerlinNoiseSampler.createLegacy(
                                this.method_42375(0L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, new double[]{1.0})
                        );
                        return new class_7270(registryEntry, doublePerlinNoiseSampler);
                    }

                    if (Objects.equals(registryEntry.getKey(), Optional.of(NoiseParametersKeys.VEGETATION))) {
                        DoublePerlinNoiseSampler doublePerlinNoiseSampler = DoublePerlinNoiseSampler.createLegacy(
                                this.method_42375(1L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, new double[]{1.0})
                        );
                        return new class_7270(registryEntry, doublePerlinNoiseSampler);
                    }

                    if (Objects.equals(registryEntry.getKey(), Optional.of(NoiseParametersKeys.OFFSET))) {
                        DoublePerlinNoiseSampler doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(
                                NoiseConfig.this.randomDeriver.createRandom(NoiseParametersKeys.OFFSET.getValue()), new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0, new double[0])
                        );
                        return new class_7270(registryEntry, doublePerlinNoiseSampler);
                    }
                }

                DoublePerlinNoiseSampler doublePerlinNoiseSampler = NoiseConfig.this.getOrCreateSampler(
                        (RegistryKey<DoublePerlinNoiseSampler.NoiseParameters>)registryEntry.getKey().orElseThrow()
                );
                return new class_7270(registryEntry, doublePerlinNoiseSampler);
            }

            private DensityFunction method_42376(DensityFunction densityFunction) {
                if (densityFunction instanceof InterpolatedNoiseSampler interpolatedNoiseSampler) {
                    AbstractRandom abstractRandom = bl ? this.method_42375(0L) : NoiseConfig.this.randomDeriver.createRandom(new Identifier("terrain"));
                    return interpolatedNoiseSampler.method_42386(abstractRandom);
                } else {
                    return (DensityFunction)(densityFunction instanceof DensityFunctionTypes.EndIslands ? new DensityFunctionTypes.EndIslands(seed) : densityFunction);
                }
            }

            public DensityFunction apply(DensityFunction densityFunction) {
                return (DensityFunction)this.field_38267.computeIfAbsent(densityFunction, this::method_42376);
            }
        }

        this.noiseRouter = chunkGeneratorSettings.noiseRouter().method_41544(new class_7271());
        this.multiNoiseSampler = new MultiNoiseUtil.MultiNoiseSampler(
                this.noiseRouter.temperature(),
                this.noiseRouter.vegetation(),
                this.noiseRouter.continents(),
                this.noiseRouter.erosion(),
                this.noiseRouter.depth(),
                this.noiseRouter.ridges(),
                chunkGeneratorSettings.spawnTarget()
        );
    }

    public DoublePerlinNoiseSampler getOrCreateSampler(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersKey) {
        return (DoublePerlinNoiseSampler)this.field_38262
                .computeIfAbsent(
                        noiseParametersKey, key -> NoiseParametersKeys.createNoiseSampler(this.noiseParametersRegistry, this.randomDeriver, noiseParametersKey)
                );
    }

    public RandomDeriver getOrCreateRandomDeriver(Identifier id) {
        return (RandomDeriver)this.randomDerivers.computeIfAbsent(id, id2 -> this.randomDeriver.createRandom(id).createRandomDeriver());
    }

    public long getLegacyWorldSeed() {
        return this.legacyWorldSeed;
    }

    public NoiseRouter getNoiseRouter() {
        return this.noiseRouter;
    }

    public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
        return this.multiNoiseSampler;
    }

    public SurfaceBuilder getSurfaceBuilder() {
        return this.surfaceBuilder;
    }

    public RandomDeriver getAquiferRandomDeriver() {
        return this.aquiferRandomDeriver;
    }

    public RandomDeriver getOreRandomDeriver() {
        return this.oreRandomDeriver;
    }
}
*/