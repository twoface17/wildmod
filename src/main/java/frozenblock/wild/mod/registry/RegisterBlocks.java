package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RegisterBlocks {

    public static final Block MUD_BLOCK = new MudBlock(FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.5f).breakByTool(FabricToolTags.SHOVELS).sounds(BlockSoundGroup.GRAVEL));

    public static void RegisterBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud_block"), MUD_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_block"), new BlockItem(MUD_BLOCK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
    }
}
