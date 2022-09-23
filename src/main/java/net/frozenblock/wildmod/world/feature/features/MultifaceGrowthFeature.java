package net.frozenblock.wildmod.world.feature.features;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.frozenblock.wildmod.block.deepdark.SculkVeinBlock;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GlowLichenFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class MultifaceGrowthFeature extends Feature<GlowLichenFeatureConfig> {
    public MultifaceGrowthFeature(Codec<GlowLichenFeatureConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<GlowLichenFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        GlowLichenFeatureConfig multifaceGrowthFeatureConfig = context.getConfig();
        if (!isAirOrWater(structureWorldAccess.getBlockState(blockPos))) {
            return false;
        } else {
            List<Direction> list = shuffleDirections(multifaceGrowthFeatureConfig, random);
            if (generate(structureWorldAccess, blockPos, structureWorldAccess.getBlockState(blockPos), multifaceGrowthFeatureConfig, random, list)) {
                return true;
            } else {
                BlockPos.Mutable mutable = blockPos.mutableCopy();

                for (Direction direction : list) {
                    mutable.set(blockPos);
                    List<Direction> list2 = shuffleDirections(multifaceGrowthFeatureConfig, random, direction.getOpposite());

                    for (int i = 0; i < multifaceGrowthFeatureConfig.searchRange; ++i) {
                        mutable.set(blockPos, direction);
                        BlockState blockState = structureWorldAccess.getBlockState(mutable);
                        if (!isAirOrWater(blockState) && !blockState.isOf(RegisterBlocks.SCULK_VEIN)) {
                            break;
                        }

                        if (generate(structureWorldAccess, mutable, blockState, multifaceGrowthFeatureConfig, random, list2)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    public static boolean generate(
            StructureWorldAccess world, BlockPos pos, BlockState state, GlowLichenFeatureConfig config, Random random, List<Direction> directions
    ) {
        BlockPos.Mutable mutable = pos.mutableCopy();

        for (Direction direction : directions) {
            BlockState blockState = world.getBlockState(mutable.set(pos, direction));
            if (blockState.isIn(config.canPlaceOn)) {
                SculkVeinBlock vein = (SculkVeinBlock) RegisterBlocks.SCULK_VEIN;
                BlockState blockState2 = vein.withDirection(state, world, pos, direction);
                if (blockState2 == null) {
                    return false;
                }

                world.setBlockState(pos, blockState2, 3);
                world.getChunk(pos).markBlockForPostProcessing(pos);
                if (random.nextFloat() < config.spreadChance) {
                    vein.getGrower().grow(blockState2, world, pos, direction, random, true);
                }

                return true;
            }
        }

        return false;
    }

    public static <T> List<T> copyShuffled(Stream<T> stream, Random random) {
        ObjectArrayList<T> objectArrayList = stream.collect(ObjectArrayList.toList());
        shuffle(objectArrayList, random);
        return objectArrayList;
    }

    public static <T> List<T> copyShuffled(T[] array, Random random) {
        ObjectArrayList<T> objectArrayList = new ObjectArrayList<>(array);
        shuffle(objectArrayList, random);
        return objectArrayList;
    }

    public static <T> List<T> copyShuffled(ObjectArrayList<T> list, Random random) {
        ObjectArrayList<T> objectArrayList = new ObjectArrayList<>(list);
        shuffle(objectArrayList, random);
        return objectArrayList;
    }

    public static <T> void shuffle(ObjectArrayList<T> list, Random random) {
        int i = list.size();

        for (int j = i; j > 1; --j) {
            int k = random.nextInt(j);
            list.set(j - 1, list.set(k, list.get(j - 1)));
        }

    }

    public List<Direction> shuffleDirections(GlowLichenFeatureConfig config, Random random, Direction excluded) {
        return copyShuffled(config.directions.stream().filter(direction -> direction != excluded), random);
    }

    public List<Direction> shuffleDirections(GlowLichenFeatureConfig config, Random random) {
        List<Direction> list = Lists.newArrayList(config.directions);
        Collections.shuffle(list, random);
        return list;
    }

    private static boolean isAirOrWater(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER);
    }
}
