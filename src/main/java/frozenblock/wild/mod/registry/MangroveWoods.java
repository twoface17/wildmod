package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.blocks.mangrove.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;
import net.minecraft.util.registry.Registry;

public abstract class MangroveWoods {

    // THIS VARIABLE DEFINES ALL PROPERTIES FOR WOOD
    public static final AbstractBlock.Settings WOOD_PROPERTIES = FabricBlockSettings.copyOf(Blocks.OAK_PLANKS);

    // DEFINES ALL BLOCKS AS VARIABLES
    public static final Block MANGROVE_LOG = new MangroveLog(WOOD_PROPERTIES);
    public static final Block MANGROVE_PLANKS = new MangrovePlanks(WOOD_PROPERTIES);
    public static final StairsBlock MANGROVE_STAIRS = new MangroveStairs(Blocks.OAK_STAIRS.getDefaultState(), WOOD_PROPERTIES);
    public static final Block MANGROVE_DOOR = new MangroveDoor(WOOD_PROPERTIES);
    public static final Block STRIPPED_MANGROVE_LOG = new StrippedMangroveLog(WOOD_PROPERTIES);
    public static final Block STRIPPED_MANGROVE_WOOD = new StrippedMangroveWood(WOOD_PROPERTIES);
    public static final Block MANGROVE_WOOD = new MangroveWood(WOOD_PROPERTIES);
    public static final Block MANGROVE_FENCE = new MangroveFence(WOOD_PROPERTIES);
    public static final Block MANGROVE_SLAB = new MangroveSlab(WOOD_PROPERTIES);
    public static final Block MANGROVE_FENCE_GATE = new MangroveFenceGate(WOOD_PROPERTIES);
    public static final PressurePlateBlock MANGROVE_PRESSURE_PLATE = new MangrovePressurePlate(PressurePlateBlock.ActivationRule.EVERYTHING, WOOD_PROPERTIES);
    public static final Block MANGROVE_TRAPDOOR = new MangroveTrapdoor(WOOD_PROPERTIES.nonOpaque());
    public static final Block MANGROVE_LEAVES = new MangroveLeaves();
    public static final Block MANGROVE_ROOTS = new MangroveRoots(MANGROVE_ROOTS_SETTINGS.copyOf(Blocks.AZALEA)nonOpaque());
    public static final Block MANGROVE_BUTTON = new MangroveButton(WOOD_PROPERTIES);
    public static final MangrovePropagule MANGROVE_PROPAGULE = new MangrovePropagule(FabricBlockSettings.copyOf(Blocks.OAK_SAPLING).hardness(0.5F));

    public static void RegisterMangrove() {

        // REGISTERING BLOCKS

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_planks"), MANGROVE_PLANKS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_planks"),
                new BlockItem(MANGROVE_PLANKS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_propagule"), MANGROVE_PROPAGULE);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_propagule"),
                new BlockItem(MANGROVE_PROPAGULE, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_log"), MANGROVE_LOG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_log"),
                new BlockItem(MANGROVE_LOG, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "stripped_mangrove_wood"), STRIPPED_MANGROVE_WOOD);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "stripped_mangrove_wood"),
                new BlockItem(STRIPPED_MANGROVE_WOOD, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "stripped_mangrove_log"), STRIPPED_MANGROVE_LOG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "stripped_mangrove_log"),
                new BlockItem(STRIPPED_MANGROVE_LOG, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_wood"), MANGROVE_WOOD);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_wood"),
                new BlockItem(MANGROVE_WOOD, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_leaves"), MANGROVE_LEAVES);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_leaves"),
                new BlockItem(MANGROVE_LEAVES, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

         Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_roots"), MANGROVE_ROOTS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_roots"),
                new BlockItem(MANGROVE_ROOTS, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_slab"), MANGROVE_SLAB);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_slab"),
                new BlockItem(MANGROVE_SLAB, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_fence"), MANGROVE_FENCE);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_fence"),
                new BlockItem(MANGROVE_FENCE, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_stairs"), MANGROVE_STAIRS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_stairs"),
                new BlockItem(MANGROVE_STAIRS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_button"), MANGROVE_BUTTON);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_button"),
                new BlockItem(MANGROVE_BUTTON, new FabricItemSettings().group(ItemGroup.REDSTONE)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_pressure_plate"), MANGROVE_PRESSURE_PLATE);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_pressure_plate"),
                new BlockItem(MANGROVE_PRESSURE_PLATE, new FabricItemSettings().group(ItemGroup.REDSTONE)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_door"), MANGROVE_DOOR);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_door"),
                new BlockItem(MANGROVE_DOOR, new FabricItemSettings().group(ItemGroup.REDSTONE)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_trapdoor"), MANGROVE_TRAPDOOR);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_trapdoor"),
                new BlockItem(MANGROVE_TRAPDOOR, new FabricItemSettings().group(ItemGroup.REDSTONE)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_fence_gate"), MANGROVE_FENCE_GATE);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_fence_gate"),
                new BlockItem(MANGROVE_FENCE_GATE, new FabricItemSettings().group(ItemGroup.REDSTONE)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_roots"), MANGROVE_ROOTS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_roots"),
                new BlockItem(MANGROVE_ROOTS, new FabricItemSettings().group(ItemGroup.DECORATIONS)));



    }
}
