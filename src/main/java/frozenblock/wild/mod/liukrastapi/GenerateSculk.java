package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class GenerateSculk {

    public static void generateSculk(World world, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        double sx = 0;
        double sy = 0;
        double sz = 0;
        sx = (double) (-1);
        for (int index0 = 0; index0 < (int) (3); index0++) {
            sy = (double) (-1);
            for (int index1 = 0; index1 < (int) (3); index1++) {
                sz = (double) (-1);
                for (int index2 = 0; index2 < (int) (3); index2++) {
                    if(world.getBlockState(new BlockPos(x+sx,y+sy,z+sz)).isIn(BlockTags.MOSS_REPLACEABLE)) {
                        world.setBlockState(new BlockPos(x + sx, y + sy, z + sz), RegisterBlocks.SCULK.getDefaultState());
                    }
                    sz = (double) (sz + 1);
                }
                sy = (double) (sy + 1);
            }
            sx = (double) (sx + 1);
        }

    }
}
