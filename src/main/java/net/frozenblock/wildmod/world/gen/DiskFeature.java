package net.frozenblock.wildmod.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.random.AbstractRandom;

import java.util.Random;

public class DiskFeature extends Feature<DiskFeatureConfig> {
    public DiskFeature(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<DiskFeatureConfig> context) {
        DiskFeatureConfig diskFeatureConfig = context.getConfig();
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        AbstractRandom abstractRandom = (AbstractRandom) context.getRandom();
        boolean bl = false;
        int i = blockPos.getY();
        int j = i + diskFeatureConfig.halfHeight();
        int k = i - diskFeatureConfig.halfHeight() - 1;
        int l = diskFeatureConfig.radius().get((Random) abstractRandom);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-l, 0, -l), blockPos.add(l, 0, l))) {
            int m = blockPos2.getX() - blockPos.getX();
            int n = blockPos2.getZ() - blockPos.getZ();
            if (m * m + n * n <= l * l) {
                bl |= this.placeBlock(diskFeatureConfig, structureWorldAccess, abstractRandom, j, k, mutable.set(blockPos2));
            }
        }

        return bl;
    }

    protected boolean placeBlock(DiskFeatureConfig config, StructureWorldAccess world, AbstractRandom random, int topY, int bottomY, BlockPos.Mutable pos) {
        boolean bl = false;
        BlockState blockState = null;

        for(int i = topY; i > bottomY; --i) {
            pos.setY(i);
            if (config.target().test(world, pos)) {
                BlockState blockState2 = config.stateProvider().getBlockState(world, (Random) random, pos);
                world.setBlockState(pos, blockState2, 2);
                this.markBlocksAboveForPostProcessing(world, pos);
                bl = true;
            }
        }

        return bl;
    }
}
