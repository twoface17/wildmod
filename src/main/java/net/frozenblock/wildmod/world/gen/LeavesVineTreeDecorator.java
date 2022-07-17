package net.frozenblock.wildmod.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class LeavesVineTreeDecorator extends TreeDecorator {
    public static final Codec<LeavesVineTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(LeavesVineTreeDecorator::new, (treeDecorator) -> {
        return treeDecorator.probability;
    }).codec();
    private final float probability;

    public LeavesVineTreeDecorator(float probability) {
        this.probability = probability;
    }

    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.LEAVE_VINE;
    }

    public void generate(
            TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions
    ) {
        leavesPositions.forEach(pos -> {
            BlockPos blockPos;
            if (random.nextFloat() < this.probability) {
                blockPos = pos.west();
                if (Feature.isAir(world, blockPos)) {
                    placeVines(world, blockPos, VineBlock.EAST, replacer);
                }
            }

            if (random.nextFloat() < this.probability) {
                blockPos = pos.east();
                if (Feature.isAir(world, blockPos)) {
                    placeVines(world, blockPos, VineBlock.WEST, replacer);
                }
            }

            if (random.nextFloat() < this.probability) {
                blockPos = pos.north();
                if (Feature.isAir(world, blockPos)) {
                    placeVines(world, blockPos, VineBlock.SOUTH, replacer);
                }
            }

            if (random.nextFloat() < this.probability) {
                blockPos = pos.south();
                if (Feature.isAir(world, blockPos)) {
                    placeVines(world, blockPos, VineBlock.NORTH, replacer);
                }
            }

        });
    }

    private static void placeVines(TestableWorld world, BlockPos pos, BooleanProperty facing, BiConsumer<BlockPos, BlockState> replacer) {
        placeVine(replacer, pos, facing);
        int i = 4;

        for (pos = pos.down(); Feature.isAir(world, pos) && i > 0; --i) {
            placeVine(replacer, pos, facing);
            pos = pos.down();
        }

    }
}
