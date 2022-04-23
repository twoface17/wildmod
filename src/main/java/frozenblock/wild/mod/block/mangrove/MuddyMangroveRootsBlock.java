package frozenblock.wild.mod.block.mangrove;

import frozenblock.wild.mod.registry.RegisterSounds;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.sound.BlockSoundGroup;

import java.util.Collections;
import java.util.List;

public class MuddyMangroveRootsBlock extends Block {
    public MuddyMangroveRootsBlock() {
        super(FabricBlockSettings
                .of(Material.SOIL, MapColor.GRAY)
                .sounds(new BlockSoundGroup(1.0f, 1.0f,
                        RegisterSounds.BLOCK_MUD_BREAK,
                        RegisterSounds.BLOCK_MUD_STEP,
                        RegisterSounds.BLOCK_MUD_PLACE,
                        RegisterSounds.BLOCK_MUD_HIT,
                        RegisterSounds.BLOCK_MUD_STEP
                ))
                .strength(0.5f, 0.5f)
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
