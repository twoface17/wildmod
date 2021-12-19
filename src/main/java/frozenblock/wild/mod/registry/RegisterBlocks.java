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
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RegisterBlocks {

    public static final AbstractBlock.Settings SCULK_PROPERTIES = FabricBlockSettings
            .of(Material.SCULK)
            .sounds(BlockSoundGroup.SCULK_SENSOR);

    public static final AbstractBlock.Settings SCULK_CATALYST_PROPERTIES = FabricBlockSettings
            .of(Material.SCULK)
            .sounds(BlockSoundGroup.SCULK_SENSOR)
            .luminance(6);

    public static final FabricBlockSettings MUD_BRICKS_SETTINGS = FabricBlockSettings
            .of(Material.STONE)
            .sounds(BlockSoundGroup.DEEPSLATE_BRICKS)
            .strength(1.5f, 10f)
            .requiresTool();


    public static final Block MUD_BLOCK = new MudBlock();
    public static final Block MUD_BRICKS = new MudBricks(MUD_BRICKS_SETTINGS);
    public static final WallBlock MUD_BRICKS_WALL = new WallBlock(MUD_BRICKS_SETTINGS);
    public static final SlabBlock MUD_BRICKS_SLAB = new SlabBlock(MUD_BRICKS_SETTINGS);
    public static final StairsBlock MUD_BRICKS_STAIRS = new CustomStairs(Blocks.OAK_STAIRS.getDefaultState(), MUD_BRICKS_SETTINGS);
    public static final Block SCULK = new SculkBlock(SCULK_PROPERTIES.strength(0.9f, 0.9f));

    public static final Block SCULK_CATALYST = new SculkCatalystBlock(SCULK_CATALYST_PROPERTIES
            .strength(2f, 2f)
            .sounds(new BlockSoundGroup(0.8f, 1.0f,
                    RegisterSounds.BLOCK_SCULK_CATALYST_BREAK,
                    RegisterSounds.BLOCK_SCULK_CATALYST_STEP,
                    RegisterSounds.BLOCK_SCULK_CATALYST_PLACE,
                    RegisterSounds.BLOCK_SCULK_CATALYST_STEP,
                    RegisterSounds.BLOCK_SCULK_CATALYST_STEP
            )));

    public static final Block SCULK_SHRIEKER = new SculkShriekerBlock(SCULK_PROPERTIES.strength(2f, 2f).nonOpaque());
    public static final Block DEEPSLATE_FRAME = new PillarBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE));
    public static final Block FROG_EGG = new FrogEggBlock(FabricBlockSettings.copyOf(Blocks.LILY_PAD));


    // ALL BLOCKS HERE HAVE NO COLLISION
    public static final Block SCULK_VEIN = new SculkVeinBlock(SCULK_PROPERTIES.nonOpaque().noCollision().strength(0.5f, 0.5f));

    public static void RegisterBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud_block"), MUD_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_block"), new BlockItem(MUD_BLOCK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud_bricks"), MUD_BRICKS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_bricks"), new BlockItem(MUD_BRICKS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud_brick_wall"), MUD_BRICKS_WALL);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_brick_wall"), new BlockItem(MUD_BRICKS_WALL, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud_brick_slab"), MUD_BRICKS_SLAB);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_brick_slab"), new BlockItem(MUD_BRICKS_SLAB, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud_brick_stairs"), MUD_BRICKS_STAIRS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_brick_stairs"), new BlockItem(MUD_BRICKS_STAIRS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "sculk"), SCULK);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk"), new BlockItem(SCULK, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "sculk_vein"), SCULK_VEIN);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk_vein"), new BlockItem(SCULK_VEIN, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "sculk_catalyst"), SCULK_CATALYST);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk_catalyst"), new BlockItem(SCULK_CATALYST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "sculk_shrieker"), SCULK_SHRIEKER);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk_shrieker"), new BlockItem(SCULK_SHRIEKER, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk_sensor"), new BlockItem(Blocks.SCULK_SENSOR, new FabricItemSettings().group(ItemGroup.REDSTONE)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "deepslate_frame"), DEEPSLATE_FRAME);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "deepslate_frame"), new BlockItem(DEEPSLATE_FRAME, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "frog_egg"), FROG_EGG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "frog_egg"), new BlockItem(FROG_EGG, new FabricItemSettings().group(ItemGroup.DECORATIONS)));


        MangroveWoods.RegisterMangrove();
    }
}
