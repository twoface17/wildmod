package frozenblock.wild.mod.worldgen;

import com.mojang.serialization.Codec;
import frozenblock.wild.mod.blocks.SculkBlock;
import frozenblock.wild.mod.blocks.SculkVeinBlock;
import frozenblock.wild.mod.fromAccurateSculk.SculkTags;
import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.Random;

public class CommonActivatorFeature extends Feature<DefaultFeatureConfig> {

    public CommonActivatorFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    public static final BlockState sculk = SculkBlock.SCULK_BLOCK.getDefaultState();
    public static final BlockState vein = SculkVeinBlock.SCULK_VEIN.getDefaultState();
    public static final BlockState water = Blocks.WATER.getDefaultState();
    public static final Block waterBlock = Blocks.WATER;
    public static final BooleanProperty waterLogged = Properties.WATERLOGGED;

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        if (blockTagsInSphere(context.getOrigin(), 2, SculkTags.COMMON_ACTIVATORS, context.getWorld()).isEmpty() && world.getBlockState(context.getOrigin()).getBlock()==sculk.getBlock()) {
            context.getWorld().setBlockState(context.getOrigin().up(), RegisterBlocks.SCULK_CATALYST.getDefaultState(), 0);
            if (context.getWorld().getBlockEntity(context.getOrigin()) == null) {;
                BlockState activator = SculkTags.COMMON_ACTIVATORS.getRandom(new Random(context.getWorld().getSeed())).getDefaultState();
                if (SculkTags.GROUND_ACTIVATORS.contains(activator.getBlock())) {
                    world.setBlockState(pos, activator,0);
                } else {
                    BlockState stateUp = world.getBlockState(pos.up());
                    if (stateUp == water && activator.contains(waterLogged)) {
                        world.setBlockState(pos.up(), activator.with(waterLogged, true),0);
                    } else if (stateUp.getBlock() != waterBlock) {
                        if (stateUp == vein.with(waterLogged, true) && activator.contains(waterLogged)) {
                            world.setBlockState(pos.up(), activator.with(waterLogged, true),0);
                        } else {
                            world.setBlockState(pos.up(), activator,0);
                        }
                    }
                }
            }
            return true;
        } return false;
    }

    public static ArrayList<BlockPos> blockTagsInSphere(BlockPos pos, int radius, Tag<Block> tag, StructureWorldAccess world) {
        int bx = pos.getX();
        int by = pos.getY();
        int bz = pos.getZ();
        ArrayList<BlockPos> blocks = new ArrayList<>();
        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));
                    if(distance < radius * radius) {
                        BlockPos l = new BlockPos(x, y, z);
                        if (tag.contains(world.getBlockState(l).getBlock())) {
                            blocks.add(l);
                        }
                    }
                }
            }
        }
        return blocks;
    }
}