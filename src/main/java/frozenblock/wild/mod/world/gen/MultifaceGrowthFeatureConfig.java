package frozenblock.wild.mod.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.random.AbstractRandom;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MultifaceGrowthFeatureConfig implements FeatureConfig {
    public static <T> List<T> copyShuffled(Stream<T> stream, AbstractRandom random) {
        ObjectArrayList<T> objectArrayList = stream.collect(ObjectArrayList.toList());
        shuffle(objectArrayList, random);
        return objectArrayList;
    }

    public static IntArrayList shuffle(IntStream stream, AbstractRandom random) {
        IntArrayList intArrayList = IntArrayList.wrap(stream.toArray());
        int i = intArrayList.size();

        for(int j = i; j > 1; --j) {
            int k = random.nextInt(j);
            intArrayList.set(j - 1, intArrayList.set(k, intArrayList.getInt(j - 1)));
        }

        return intArrayList;
    }

    public static <T> List<T> copyShuffled(T[] array, AbstractRandom random) {
        ObjectArrayList<T> objectArrayList = new ObjectArrayList<>(array);
        shuffle(objectArrayList, random);
        return objectArrayList;
    }

    public static <T> List<T> copyShuffled(ObjectArrayList<T> list, AbstractRandom random) {
        ObjectArrayList<T> objectArrayList = new ObjectArrayList<>(list);
        shuffle(objectArrayList, random);
        return objectArrayList;
    }

    public static <T> void shuffle(ObjectArrayList<T> list, AbstractRandom random) {
        int i = list.size();

        for(int j = i; j > 1; --j) {
            int k = random.nextInt(j);
            list.set(j - 1, list.set(k, list.get(j - 1)));
        }

    }

    public static final Codec<MultifaceGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Registry.BLOCK
                                    .getCodec()
                                    .fieldOf("block")
                                    .flatXmap(MultifaceGrowthFeatureConfig::validateBlock, DataResult::success)
                                    .orElse((AbstractLichenBlock) Blocks.GLOW_LICHEN)
                                    .forGetter(config -> config.lichen),
                            Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter(config -> config.searchRange),
                            Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter(config -> config.placeOnFloor),
                            Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter(config -> config.placeOnCeiling),
                            Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter(config -> config.placeOnWalls),
                            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter(config -> config.spreadChance),
                            RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("can_be_placed_on").forGetter(config -> config.canPlaceOn)
                    )
                    .apply(instance, MultifaceGrowthFeatureConfig::new)
    );
    public final AbstractLichenBlock lichen;
    public final int searchRange;
    public final boolean placeOnFloor;
    public final boolean placeOnCeiling;
    public final boolean placeOnWalls;
    public final float spreadChance;
    public final RegistryEntryList<Block> canPlaceOn;
    private final ObjectArrayList<Direction> directions;

    private static DataResult<AbstractLichenBlock> validateBlock(Block block) {
        return block instanceof AbstractLichenBlock abstractLichenBlock
                ? DataResult.success(abstractLichenBlock)
                : DataResult.error("Growth block should be a multiface block");
    }

    public MultifaceGrowthFeatureConfig(
            AbstractLichenBlock lichen,
            int searchRange,
            boolean placeOnFloor,
            boolean placeOnCeiling,
            boolean placeOnWalls,
            float spreadChance,
            RegistryEntryList<Block> canPlaceOn
    ) {
        this.lichen = lichen;
        this.searchRange = searchRange;
        this.placeOnFloor = placeOnFloor;
        this.placeOnCeiling = placeOnCeiling;
        this.placeOnWalls = placeOnWalls;
        this.spreadChance = spreadChance;
        this.canPlaceOn = canPlaceOn;
        this.directions = new ObjectArrayList(6);
        if (placeOnCeiling) {
            this.directions.add(Direction.UP);
        }

        if (placeOnFloor) {
            this.directions.add(Direction.DOWN);
        }

        if (placeOnWalls) {
            Direction.Type.HORIZONTAL.forEach(this.directions::add);
        }

    }

    public List<Direction> shuffleDirections(AbstractRandom random, Direction excluded) {
        return copyShuffled(this.directions.stream().filter(direction -> direction != excluded), random);
    }

    public List<Direction> shuffleDirections(AbstractRandom random) {
        return copyShuffled(this.directions, random);
    }
}
