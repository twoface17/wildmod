package frozenblock.wild.mod.blocks.mangrove;

import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

import java.util.Collections;
import java.util.List;

public class MangroveFence extends FenceBlock {
    public MangroveFence(Settings settings) {
        super(settings);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDroppedStacks(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }
}