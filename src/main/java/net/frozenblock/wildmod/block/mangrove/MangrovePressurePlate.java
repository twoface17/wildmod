package net.frozenblock.wildmod.block.mangrove;

import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

import java.util.Collections;
import java.util.List;

public class MangrovePressurePlate extends PressurePlateBlock {
    public MangrovePressurePlate(ActivationRule type, Settings settings) {
        super(type, settings);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDroppedStacks(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }
}
