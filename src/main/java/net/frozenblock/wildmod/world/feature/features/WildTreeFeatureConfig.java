package net.frozenblock.wildmod.world.feature.features;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.world.gen.root.RootPlacer;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.trunk.TrunkPlacer;

import java.util.List;
import java.util.Optional;

public class WildTreeFeatureConfig extends TreeFeatureConfig {
    public static final Codec<WildTreeFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BlockStateProvider.TYPE_CODEC.fieldOf("trunk_provider").forGetter(config -> config.trunkProvider),
                            TrunkPlacer.TYPE_CODEC.fieldOf("trunk_placer").forGetter(config -> config.trunkPlacer),
                            BlockStateProvider.TYPE_CODEC.fieldOf("foliage_provider").forGetter(config -> config.foliageProvider),
                            FoliagePlacer.TYPE_CODEC.fieldOf("foliage_placer").forGetter(config -> config.foliagePlacer),
                            RootPlacer.TYPE_CODEC.optionalFieldOf("root_placer").forGetter(config -> config.rootPlacer),
                            BlockStateProvider.TYPE_CODEC.fieldOf("dirt_provider").forGetter(config -> config.dirtProvider),
                            FeatureSize.TYPE_CODEC.fieldOf("minimum_size").forGetter(config -> config.minimumSize),
                            TreeDecorator.TYPE_CODEC.listOf().fieldOf("decorators").forGetter(config -> config.decorators),
                            Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter(config -> config.ignoreVines),
                            Codec.BOOL.fieldOf("force_dirt").orElse(false).forGetter(config -> config.forceDirt)
                    )
                    .apply(instance, WildTreeFeatureConfig::new)
    );
    public final Optional<RootPlacer> rootPlacer;

    protected WildTreeFeatureConfig(
            BlockStateProvider trunkProvider,
            TrunkPlacer trunkPlacer,
            BlockStateProvider foliageProvider,
            FoliagePlacer foliagePlacer,
            Optional<RootPlacer> rootPlacer,
            BlockStateProvider dirtProvider,
            FeatureSize minimumSize,
            List<TreeDecorator> decorators,
            boolean ignoreVines,
            boolean forceDirt
    ) {
        super(trunkProvider, trunkPlacer, foliageProvider, foliagePlacer, dirtProvider, minimumSize, decorators, ignoreVines, forceDirt);
        this.rootPlacer = rootPlacer;
    }

    public static class Builder {
        public final BlockStateProvider trunkProvider;
        private final TrunkPlacer trunkPlacer;
        public final BlockStateProvider foliageProvider;
        private final FoliagePlacer foliagePlacer;
        private final Optional<RootPlacer> rootPlacer;
        private BlockStateProvider dirtProvider;
        private final FeatureSize minimumSize;
        private List<TreeDecorator> decorators = ImmutableList.of();
        private boolean ignoreVines;
        private boolean forceDirt;

        public Builder(
                BlockStateProvider trunkProvider,
                TrunkPlacer trunkPlacer,
                BlockStateProvider foliageProvider,
                FoliagePlacer foliagePlacer,
                Optional<RootPlacer> rootPlacer,
                FeatureSize minimumSize
        ) {
            this.trunkProvider = trunkProvider;
            this.trunkPlacer = trunkPlacer;
            this.foliageProvider = foliageProvider;
            this.dirtProvider = BlockStateProvider.of(Blocks.DIRT);
            this.foliagePlacer = foliagePlacer;
            this.rootPlacer = rootPlacer;
            this.minimumSize = minimumSize;
        }

        public Builder(
                BlockStateProvider trunkProvider, TrunkPlacer trunkPlacer, BlockStateProvider foliageProvider, FoliagePlacer foliagePlacer, FeatureSize minimumSize
        ) {
            this(trunkProvider, trunkPlacer, foliageProvider, foliagePlacer, Optional.empty(), minimumSize);
        }

        public WildTreeFeatureConfig.Builder dirtProvider(BlockStateProvider dirtProvider) {
            this.dirtProvider = dirtProvider;
            return this;
        }

        public WildTreeFeatureConfig.Builder decorators(List<TreeDecorator> decorators) {
            this.decorators = decorators;
            return this;
        }

        public WildTreeFeatureConfig.Builder ignoreVines() {
            this.ignoreVines = true;
            return this;
        }

        public WildTreeFeatureConfig.Builder forceDirt() {
            this.forceDirt = true;
            return this;
        }

        public WildTreeFeatureConfig build() {
            return new WildTreeFeatureConfig(
                    this.trunkProvider,
                    this.trunkPlacer,
                    this.foliageProvider,
                    this.foliagePlacer,
                    this.rootPlacer,
                    this.dirtProvider,
                    this.minimumSize,
                    this.decorators,
                    this.ignoreVines,
                    this.forceDirt
            );
        }
    }
}
