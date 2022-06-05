package net.frozenblock.wildmod.world.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.wildmod.world.feature.features.WildTreeFeatureConfig;
import net.frozenblock.wildmod.world.feature.foliage.WildFoliagePlacer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

;

public class WildStraightTrunkPlacer extends WildTrunkPlacer {
    public static final Codec<WildStraightTrunkPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return fillTrunkPlacerFields(instance).apply(instance, (WildStraightTrunkPlacer::new));
    });

    public WildStraightTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
    }

    protected WildTrunkPlacerType<?> getType() {
        return WildTrunkPlacerType.STRAIGHT_TRUNK_PLACER;
    }

    public List<WildFoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, WildTreeFeatureConfig config) {
        setToDirt(world, replacer, random, startPos.down(), config);

        for(int i = 0; i < height; ++i) {
            this.getAndSetState(world, replacer, random, startPos.up(i), config);
        }

        return ImmutableList.of(new WildFoliagePlacer.TreeNode(startPos.up(height), 0, false));
    }
}
