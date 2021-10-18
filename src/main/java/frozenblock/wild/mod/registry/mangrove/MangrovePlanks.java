package frozenblock.wild.mod.registry.mangrove;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.sound.BlockSoundGroup;

import java.util.Collections;
import java.util.List;

public class MangrovePlanks extends Block {
    public MangrovePlanks() {
        super(FabricBlockSettings
                .of(Material.WOOD)
                .breakByTool(FabricToolTags.AXES, 1)
                .sounds(BlockSoundGroup.WOOD)
                .strength(2, 3)
        );
    }
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDroppedStacks(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }
}
