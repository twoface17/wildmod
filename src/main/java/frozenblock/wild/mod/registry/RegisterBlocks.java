package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.blocks.MudBlock;
import frozenblock.wild.mod.blocks.MudBricks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RegisterBlocks {

    public static final Block MUD_BLOCK = new MudBlock();
    public static final Block MUD_BRICKS = new MudBricks();

    public static void RegisterBlocks() {
        // REGISTERING MUD
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud_block"), MUD_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_block"), new BlockItem(MUD_BLOCK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud_bricks"), MUD_BRICKS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_bricks"), new BlockItem(MUD_BRICKS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        MangroveWoods.RegisterMangrove();
    }
}
