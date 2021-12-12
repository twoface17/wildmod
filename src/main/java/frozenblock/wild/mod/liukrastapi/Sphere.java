package frozenblock.wild.mod.liukrastapi;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;

public abstract class Sphere {

    public static ArrayList<BlockPos> checkSphereSolid(BlockState state, World world, BlockPos pos, Integer radius, boolean defaultstate) {

        int fixedradius = radius - 1;

        ArrayList<BlockPos> exitList = new ArrayList<BlockPos>();

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double sx = fixedradius * -1;
        double sy = 0;
        double sz = 0;

        for (int index0 = 0; index0 < (int) ((radius * 2) - 1); index0++) {
            sy = fixedradius * -1;
            for (int index1 = 0; index1 < (int) ((radius * 2) - 1); index1++) {
                sz = fixedradius * -1;
                for (int index2 = 0; index2 < (int) ((radius * 2) - 1); index2++) {
                    if (Math.sqrt(Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2)) <= radius) {
                        if(defaultstate) {
                            if (world.getBlockState(new BlockPos(x + sx, y + sy, z + sz)).isSolidBlock(world, new BlockPos(x + sx, y + sy, z + sz))) {
                                exitList.add(new BlockPos(x + sx, y + sy, z + sz));
                            }
                        }
                    }
                    sz = sz + 1;
                }
                sy = sy + 1;
            }
            sx = sx + 1;
        }
        return exitList;
    }

    public static ArrayList<BlockPos> checkSpherePos(BlockState state, World world, BlockPos pos, Integer radius, boolean defaultstate) {

        int fixedradius = radius - 1;

        ArrayList<BlockPos> exitList = new ArrayList<BlockPos>();

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double sx = fixedradius * -1;
        double sy = 0;
        double sz = 0;

        for (int index0 = 0; index0 < (int) ((radius * 2) - 1); index0++) {
            sy = fixedradius * -1;
            for (int index1 = 0; index1 < (int) ((radius * 2) - 1); index1++) {
                sz = fixedradius * -1;
                for (int index2 = 0; index2 < (int) ((radius * 2) - 1); index2++) {
                    if (Math.sqrt(Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2)) <= radius) {
                        if(defaultstate) {
                            if (world.getBlockState(new BlockPos(x + sx, y + sy, z + sz)) == state) {
                                exitList.add(new BlockPos(x + sx, y + sy, z + sz));
                            }
                        }
                    }
                    sz = sz + 1;
                }
                sy = sy + 1;
            }
            sx = sx + 1;
        }
        return exitList;
    }

    public static boolean checkSphere(BlockState state, World world, BlockPos pos, Integer radius) {

        int fixedradius = radius - 1;

        boolean exit = false;

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double sx = fixedradius * -1;
        double sy = 0;
        double sz = 0;

        for (int index0 = 0; index0 < (int) ((radius * 2) - 1); index0++) {
            sy = fixedradius * -1;
            for (int index1 = 0; index1 < (int) ((radius * 2) - 1); index1++) {
                sz = fixedradius * -1;
                for (int index2 = 0; index2 < (int) ((radius * 2) - 1); index2++) {
                    if (Math.sqrt(Math.pow(sx, 2) + Math.pow(sy, 2) + Math.pow(sz, 2)) <= radius) {
                        if (world.getBlockState(new BlockPos(x + sx, y + sy, z + sz)) == state) {
                            exit = true;
                        }
                    }
                    sz = sz + 1;
                }
                sy = sy + 1;
            }
            sx = sx + 1;
        }
        return exit;
    }
}
