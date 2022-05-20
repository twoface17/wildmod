package net.frozenblock.wildmod.world.gen;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.block.wild.LichenGrower;
import net.frozenblock.wildmod.block.deepdark.SculkShriekerBlock;
import net.frozenblock.wildmod.registry.RegisterBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class SculkPatchFeature extends Feature<SculkPatchFeatureConfig> {
    public SculkPatchFeature(Codec<SculkPatchFeatureConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<SculkPatchFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        if (!this.canGenerate(structureWorldAccess, blockPos)) {
            return false;
        } else {
            SculkPatchFeatureConfig sculkPatchFeatureConfig = (SculkPatchFeatureConfig)context.getConfig();
            Random abstractRandom = context.getRandom();
            SculkSpreadManager sculkSpreadManager = SculkSpreadManager.createWorldGen();
            int i = sculkPatchFeatureConfig.spreadRounds() + sculkPatchFeatureConfig.growthRounds();

            for(int j = 0; j < i; ++j) {
                for(int k = 0; k < sculkPatchFeatureConfig.chargeCount(); ++k) {
                    sculkSpreadManager.spread(blockPos, sculkPatchFeatureConfig.amountPerCharge());
                }

                boolean bl = j < sculkPatchFeatureConfig.spreadRounds();

                for(int l = 0; l < sculkPatchFeatureConfig.spreadAttempts(); ++l) {
                    sculkSpreadManager.tick(structureWorldAccess, blockPos, abstractRandom, bl);
                }

                sculkSpreadManager.clearCursors();
            }

            BlockPos blockPos2 = blockPos.down();
            if (abstractRandom.nextFloat() <= sculkPatchFeatureConfig.catalystChance()
                    && structureWorldAccess.getBlockState(blockPos2).isFullCube(structureWorldAccess, blockPos2)) {
                structureWorldAccess.setBlockState(blockPos, RegisterBlocks.SCULK_CATALYST.getDefaultState(), 3);
            }

            int k = sculkPatchFeatureConfig.extraRareGrowths().get(abstractRandom);

            for(int l = 0; l < k; ++l) {
                BlockPos blockPos3 = blockPos.add(abstractRandom.nextInt(5) - 2, 0, abstractRandom.nextInt(5) - 2);
                if (structureWorldAccess.getBlockState(blockPos3).isAir()
                        && structureWorldAccess.getBlockState(blockPos3.down()).isSideSolidFullSquare(structureWorldAccess, blockPos3.down(), Direction.UP)) {
                    structureWorldAccess.setBlockState(
                            blockPos3, RegisterBlocks.SCULK_SHRIEKER.getDefaultState().with(SculkShriekerBlock.CAN_SUMMON, true), 3
                    );
                }
            }

            return true;
        }
    }

    private boolean canGenerate(WorldAccess world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof SculkSpreadable) {
            return true;
        } else {
            return !blockState.isAir() && (!blockState.isOf(Blocks.WATER) || !blockState.getFluidState().isStill())
                    ? false
                    : LichenGrower.stream().map(pos::offset).anyMatch(pos2 -> world.getBlockState(pos2).isFullCube(world, pos2));
        }
    }
}
