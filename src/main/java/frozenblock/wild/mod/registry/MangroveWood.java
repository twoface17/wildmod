package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.registry.mangrove.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class MangroveWood {

    public static final Block MANGROVE_LOG = new MangroveLog();
    public static final Block MANGROVE_WOOD = new MangroveWoodBlock();
    public static final Block MANGROVE_PLANKS = new Block(FabricBlockSettings.of(Material.WOOD).strength(2).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES));
    public static final Block MANGROVE_FENCE = new MangroveFence();
    public static final Block MANGROVE_SLAB = new MangroveSlab();
    public static final Block MANGROVE_STAIRS = new MangroveStairs(Blocks.OAK_STAIRS.getDefaultState(),
            FabricBlockSettings.of(Material.WOOD)
                    .breakByTool(FabricToolTags.AXES, 1)
                    .sounds(BlockSoundGroup.WOOD)
                    .strength(2, 3));

    public static void RegisterMangrove() {
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_log"), MANGROVE_LOG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_log"), new BlockItem(MANGROVE_LOG, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_planks"), MANGROVE_PLANKS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_planks"), new BlockItem(MANGROVE_PLANKS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_wood"), MANGROVE_WOOD);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_wood"), new BlockItem(MANGROVE_WOOD, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_fence"), MANGROVE_FENCE);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_fence"), new BlockItem(MANGROVE_FENCE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_stairs"), MANGROVE_STAIRS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_stairs"), new BlockItem(MANGROVE_STAIRS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_slab"), MANGROVE_SLAB);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_slab"), new BlockItem(MANGROVE_SLAB, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

    }
}
