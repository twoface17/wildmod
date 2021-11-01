package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.blocks.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RegisterBlocks {

    public static final AbstractBlock.Settings SCULK_PROPERTIES = FabricBlockSettings
            .of(Material.SCULK)
            .breakByTool(FabricToolTags.HOES, 0)
            .sounds(BlockSoundGroup.SCULK_SENSOR);

    public static final AbstractBlock.Settings SCULK_CATALYST_PROPERTIES = FabricBlockSettings
            .of(Material.SCULK)
            .breakByTool(FabricToolTags.HOES, 0)
            .sounds(BlockSoundGroup.SCULK_SENSOR)
            .luminance(6);

    public static final Block MUD_BLOCK = new MudBlock();
    public static final Block MUD_BRICKS = new MudBricks();
    public static final Block SCULK = new SculkBlock(SCULK_PROPERTIES.strength(0.9f, 0.9f));
    public static final Block SCULK_CATALYST = new SculkCatalystBlock(SCULK_CATALYST_PROPERTIES.strength(2f, 2f));

    // ALL BLOCKS HERE HAVE NO COLLISION
    public static final Block SCULK_VEIN = new SculkVeinBlock(SCULK_PROPERTIES.nonOpaque().noCollision().strength(0.5f, 0.5f));

    public static void RegisterBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud_block"), MUD_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_block"), new BlockItem(MUD_BLOCK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud_bricks"), MUD_BRICKS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_bricks"), new BlockItem(MUD_BRICKS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "sculk"), SCULK);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk"), new BlockItem(SCULK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "sculk_vein"), SCULK_VEIN);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk_vein"), new BlockItem(SCULK_VEIN, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "sculk_catalyst"), SCULK_CATALYST);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk_catalyst"), new BlockItem(SCULK_CATALYST, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        MangroveWoods.RegisterMangrove();
    }
}
