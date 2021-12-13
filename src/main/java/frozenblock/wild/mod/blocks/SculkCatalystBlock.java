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

    public SculkCatalystBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState) this.getDefaultState().with(BLOOM, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(BLOOM);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.get(BLOOM)) {
            world.setBlockState(pos, RegisterBlocks.SCULK_CATALYST.getDefaultState());
        }

    }

    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        int i = 20;
        this.dropExperience(world, pos, i);
    }

}
