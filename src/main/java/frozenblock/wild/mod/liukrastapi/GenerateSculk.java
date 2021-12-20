package frozenblock.wild.mod.liukrastapi;

import frozenblock.wild.mod.blocks.SculkShriekerBlock;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.registry.RegisterBlocks;
import frozenblock.wild.mod.registry.RegisterParticles;
import frozenblock.wild.mod.registry.RegisterSounds;
import frozenblock.wild.mod.registry.RegisterStatusEffects;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class GenerateSculk {

    public static void generateSculk(World world, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        double sx = 0;
        double sy = 0;
        double sz = 0;
        sx = (double) (-2);

        for (int index0 = 0; index0 < (int) (5); index0++) {
            sy = (double) (-2);
            for (int index1 = 0; index1 < (int) (5); index1++) {
                sz = (double) (-2);
                for (int index2 = 0; index2 < (int) (5); index2++) {
                    if (Math.random() > 0.5) {
                        if (world.getBlockState(new BlockPos(x + sx, y + sy, z + sz)).isIn(BlockTags.MOSS_REPLACEABLE)) {
                            world.setBlockState(new BlockPos(x + sx, y + sy, z + sz), RegisterBlocks.SCULK.getDefaultState());
                        }
                    }
                    sz = (double) (sz + 1);
                }
                sy = (double) (sy + 1);
            }
            sx = (double) (sx + 1);
        }

        sx = (double) (-1);
        for (int index0 = 0; index0 < (int) (3); index0++) {
            sy = (double) (-1);
            for (int index1 = 0; index1 < (int) (3); index1++) {
                sz = (double) (-1);
                for (int index2 = 0; index2 < (int) (3); index2++) {
                    if (world.getBlockState(new BlockPos(x + sx, y + sy, z + sz)).isIn(BlockTags.MOSS_REPLACEABLE)) {
                        world.setBlockState(new BlockPos(x + sx, y + sy, z + sz), RegisterBlocks.SCULK.getDefaultState());
                    }
                    BlockState blockState = world.getBlockState(new BlockPos(x + sx, y + sy, z + sz));
                    if (blockState.getBlock().getDefaultState() == RegisterBlocks.SCULK_VEIN.getDefaultState()) {
                        world.setBlockState(new BlockPos(x + sx, y + sy, z + sz), Blocks.AIR.getDefaultState());
                    }
                    sz = (double) (sz + 1);
                }
                sy = (double) (sy + 1);
            }
            sx = (double) (sx + 1);
        }

        sx = (double) (-3);
        for (int index0 = 0; index0 < (int) (7); index0++) {
            sy = (double) (-3);
            for (int index1 = 0; index1 < (int) (7); index1++) {
                sz = (double) (-3);
                for (int index2 = 0; index2 < (int) (7); index2++) {
                    if (checkforVein(world, new BlockPos(x + sx, y + sy, z + sz))) {
                        if (world.getBlockState(new BlockPos(x + sx, y + sy, z + sz)).getMaterial() == Material.AIR || world.getBlockState(new BlockPos(x + sx, y + sy, z + sz)).getMaterial() == Material.WATER) {
                            world.setBlockState(new BlockPos(x + sx, y + sy, z + sz), RegisterBlocks.SCULK_VEIN.getDefaultState()
                                    .with(AbstractLichenBlock.getProperty(Direction.UP),
                                            world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).up()) != RegisterBlocks.SCULK.getDefaultState()
                                                    && world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).up()).isIn(BlockTags.MOSS_REPLACEABLE))
                                    .with(AbstractLichenBlock.getProperty(Direction.DOWN),
                                            world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).down()) != RegisterBlocks.SCULK.getDefaultState()
                                                    && world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).down()).isIn(BlockTags.MOSS_REPLACEABLE))
                                    .with(AbstractLichenBlock.getProperty(Direction.NORTH),
                                            world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).north()) != RegisterBlocks.SCULK.getDefaultState()
                                                    && world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).north()).isIn(BlockTags.MOSS_REPLACEABLE))
                                    .with(AbstractLichenBlock.getProperty(Direction.SOUTH),
                                            world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).south()) != RegisterBlocks.SCULK.getDefaultState()
                                                    && world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).south()).isIn(BlockTags.MOSS_REPLACEABLE))
                                    .with(AbstractLichenBlock.getProperty(Direction.EAST),
                                            world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).east()) != RegisterBlocks.SCULK.getDefaultState()
                                                    && world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).east()).isIn(BlockTags.MOSS_REPLACEABLE))
                                    .with(AbstractLichenBlock.getProperty(Direction.WEST),
                                            world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).west()) != RegisterBlocks.SCULK.getDefaultState()
                                                    && world.getBlockState(new BlockPos(x + sx, y + sy, z + sz).west()).isIn(BlockTags.MOSS_REPLACEABLE))
                                    .with(SculkVeinBlock.WATERLOGGED, world.getBlockState(new BlockPos(x + sx, y + sy, z + sz)).getMaterial() == Material.WATER)
                            );
                        }
                    }
                    sz = (double) (sz + 1);
                }
                sy = (double) (sy + 1);
            }
            sx = (double) (sx + 1);
        }

    }

    private static boolean checkforVein(World world, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        double sx = 0;
        double sy = 0;
        double sz = 0;


        boolean exit = true;


        if (!world.getBlockState(pos.up()).isIn(BlockTags.MOSS_REPLACEABLE)) {
            if (!world.getBlockState(pos.down()).isIn(BlockTags.MOSS_REPLACEABLE)) {
                if (!world.getBlockState(pos.down()).isIn(BlockTags.MOSS_REPLACEABLE)) {
                    if (!world.getBlockState(pos.down()).isIn(BlockTags.MOSS_REPLACEABLE)) {
                        if (!world.getBlockState(pos.down()).isIn(BlockTags.MOSS_REPLACEABLE)) {
                            if (!world.getBlockState(pos.down()).isIn(BlockTags.MOSS_REPLACEABLE)) {
                                exit = false;
                            }
                        }
                    }
                }
            }
        }


        if (world.getBlockState(pos.up()) != RegisterBlocks.SCULK.getDefaultState()) {
            if (world.getBlockState(pos.down()) != RegisterBlocks.SCULK.getDefaultState()) {
                if (world.getBlockState(pos.north()) != RegisterBlocks.SCULK.getDefaultState()) {
                    if (world.getBlockState(pos.south()) != RegisterBlocks.SCULK.getDefaultState()) {
                        if (world.getBlockState(pos.west()) != RegisterBlocks.SCULK.getDefaultState()) {
                            if (world.getBlockState(pos.east()) != RegisterBlocks.SCULK.getDefaultState()) {
                                if (world.getBlockState(pos.up().north()) != RegisterBlocks.SCULK.getDefaultState()) {
                                    if (world.getBlockState(pos.up().south()) != RegisterBlocks.SCULK.getDefaultState()) {
                                        if (world.getBlockState(pos.west()) != RegisterBlocks.SCULK.getDefaultState()) {
                                            if (world.getBlockState(pos.up().east()) != RegisterBlocks.SCULK.getDefaultState()) {

                                                if (world.getBlockState(pos.down().north()) != RegisterBlocks.SCULK.getDefaultState()) {
                                                    if (world.getBlockState(pos.down().south()) != RegisterBlocks.SCULK.getDefaultState()) {
                                                        if (world.getBlockState(pos.down().west()) != RegisterBlocks.SCULK.getDefaultState()) {
                                                            if (world.getBlockState(pos.down().east()) != RegisterBlocks.SCULK.getDefaultState()) {
                                                                exit = false;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return exit;
    }
}
