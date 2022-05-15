package net.frozenblock.wildmod.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.SignType;

import java.util.Collections;
import java.util.List;

public class MangroveSignBlock extends WildSignBlock {
    public MangroveSignBlock(Settings settings, SignType signType) {
        super(settings, signType);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDroppedStacks(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }
}
