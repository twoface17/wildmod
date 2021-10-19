package frozenblock.wild.mod.blocks.mangrove;

import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.SignType;

import java.util.Collections;
import java.util.List;

public class MangroveSign extends SignBlock {
    public MangroveSign(Settings settings, SignType signType) {
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
