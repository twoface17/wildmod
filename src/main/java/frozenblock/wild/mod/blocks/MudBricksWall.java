package frozenblock.wild.mod.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

import java.util.Collections;
import java.util.List;

public class MudBricksWall extends WallBlock {
    public MudBricksWall(FabricBlockSettings settings) {
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
