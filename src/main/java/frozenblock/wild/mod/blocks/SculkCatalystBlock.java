package frozenblock.wild.mod.blocks;

import frozenblock.wild.mod.registry.RegisterBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class SculkCatalystBlock extends Block {

    public static final BooleanProperty BLOOM = BooleanProperty.of("bloom");
    public static final IntProperty COOLDOWN = IntProperty.of("cooldown", 0, 2);

    public SculkCatalystBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState) this.getDefaultState().with(BLOOM, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(BLOOM);
        builder.add(COOLDOWN);
    }


    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        world.getBlockTickScheduler().schedule(new BlockPos(x, y, z), this, 10);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (state.get(BLOOM)) {
            if (state.get(COOLDOWN) != 0) {
                world.setBlockState(pos, state.with(COOLDOWN, state.get(COOLDOWN) - 1));
            } else {
                world.setBlockState(pos, RegisterBlocks.SCULK_CATALYST.getDefaultState());
            }
        }
        world.getBlockTickScheduler().schedule(new BlockPos(x, y, z), this, 10);

    }

    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        int i = 20;
        this.dropExperience(world, pos, i);
    }

}
