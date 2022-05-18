package net.frozenblock.wildmod.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.block.*;
import net.frozenblock.wildmod.items.FrogSpawnItem;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public abstract class RegisterBlocks {

    public static final WildMaterial FROGSPAWNMATERIAL = new WildMaterial.WildBuilder(MapColor.WATER_BLUE).allowsMovement().lightPassesThrough().notSolid().allowsMovement().build();

    private static final BlockSoundGroup MUDDY_MANGROVE_ROOTS_SOUNDS = new BlockSoundGroup(
            1.0F,
            1.0F,
            RegisterSounds.BLOCK_MUDDY_MANGROVE_ROOTS_BREAK,
            RegisterSounds.BLOCK_MUDDY_MANGROVE_ROOTS_STEP,
            RegisterSounds.BLOCK_MUDDY_MANGROVE_ROOTS_PLACE,
            RegisterSounds.BLOCK_MUDDY_MANGROVE_ROOTS_HIT,
            RegisterSounds.BLOCK_MUDDY_MANGROVE_ROOTS_FALL
    );

    private static final BlockSoundGroup SCULK_SOUNDS = new BlockSoundGroup(
            1.0F,
            1.0F,
            RegisterSounds.BLOCK_SCULK_BREAK,
            RegisterSounds.BLOCK_SCULK_STEP,
            RegisterSounds.BLOCK_SCULK_PLACE,
            RegisterSounds.BLOCK_SCULK_HIT,
            RegisterSounds.BLOCK_SCULK_FALL
    );

    private static final BlockSoundGroup SCULK_VEIN_SOUNDS = new BlockSoundGroup(
            1.0F,
            1.0F,
            RegisterSounds.BLOCK_SCULK_VEIN_BREAK,
            RegisterSounds.BLOCK_SCULK_VEIN_STEP,
            RegisterSounds.BLOCK_SCULK_VEIN_PLACE,
            RegisterSounds.BLOCK_SCULK_VEIN_HIT,
            RegisterSounds.BLOCK_SCULK_VEIN_FALL
    );

    private static final BlockSoundGroup SCULK_CATALYST_SOUNDS = new BlockSoundGroup(
            1.0F,
            1.0F,
            RegisterSounds.BLOCK_SCULK_CATALYST_BREAK,
            RegisterSounds.BLOCK_SCULK_CATALYST_STEP,
            RegisterSounds.BLOCK_SCULK_CATALYST_PLACE,
            RegisterSounds.BLOCK_SCULK_CATALYST_HIT,
            RegisterSounds.BLOCK_SCULK_CATALYST_FALL
    );

    private static final BlockSoundGroup SCULK_SHRIEKER_SOUNDS = new BlockSoundGroup(
            1.0F,
            1.0F,
            RegisterSounds.BLOCK_SCULK_SHRIEKER_BREAK,
            RegisterSounds.BLOCK_SCULK_SHRIEKER_STEP,
            RegisterSounds.BLOCK_SCULK_SHRIEKER_PLACE,
            RegisterSounds.BLOCK_SCULK_SHRIEKER_HIT,
            RegisterSounds.BLOCK_SCULK_SHRIEKER_FALL
    );

    private static final AbstractBlock.Settings REINFORCED_DEEPSLATE_SETTINGS = FabricBlockSettings
            .of(Material.STONE, MapColor.DEEPSLATE_GRAY)
            .requiresTool()
            .strength(3.0F, 6.0F)
            .sounds(new BlockSoundGroup(1.0f, 1.0f,
                    RegisterSounds.BLOCK_REINFORCED_DEEPSLATE_BREAK,
                    RegisterSounds.BLOCK_REINFORCED_DEEPSLATE_STEP,
                    RegisterSounds.BLOCK_REINFORCED_DEEPSLATE_PLACE,
                    RegisterSounds.BLOCK_REINFORCED_DEEPSLATE_HIT,
                    RegisterSounds.BLOCK_REINFORCED_DEEPSLATE_STEP
            ));

    private static final AbstractBlock.Settings FROGSPAWN_PROPERTIES = FabricBlockSettings
            .of(FROGSPAWNMATERIAL).breakInstantly().nonOpaque().noCollision()
            .sounds(new BlockSoundGroup(1.0F, 1.0F,
                    RegisterSounds.BLOCK_FROGSPAWN_BREAK,
                    RegisterSounds.BLOCK_FROGSPAWN_STEP,
                    RegisterSounds.BLOCK_FROGSPAWN_PLACE,
                    RegisterSounds.BLOCK_FROGSPAWN_HIT,
                    RegisterSounds.BLOCK_FROGSPAWN_FALL
            ));

    private static final FabricBlockSettings MUD_BRICKS_SETTINGS = FabricBlockSettings
            .of(Material.STONE)
            .strength(0.5f, 1.0f)
            .requiresTool()
            .sounds(new BlockSoundGroup(1.0f, 1.1f,
                    RegisterSounds.BLOCK_MUD_BRICKS_BREAK,
                    RegisterSounds.BLOCK_MUD_BRICKS_STEP,
                    RegisterSounds.BLOCK_MUD_BRICKS_PLACE,
                    RegisterSounds.BLOCK_MUD_BRICKS_HIT,
                    RegisterSounds.BLOCK_MUD_BRICKS_STEP
            ));
    
    private static final FabricBlockSettings PACKED_MUD_SETTINGS = FabricBlockSettings
            .of(Material.STONE)
            .strength(0.5f, 1.0f)
            .requiresTool()
            .sounds(new BlockSoundGroup(1.0f, 1.1f,
                    RegisterSounds.BLOCK_PACKED_MUD_BREAK,
                    RegisterSounds.BLOCK_PACKED_MUD_STEP,
                    RegisterSounds.BLOCK_PACKED_MUD_PLACE,
                    RegisterSounds.BLOCK_PACKED_MUD_HIT,
                    RegisterSounds.BLOCK_PACKED_MUD_STEP
            ));

    private static final FabricBlockSettings FROGLIGHT_SETTINGS = FabricBlockSettings
            .of(Material.PLANT)
            .strength(0.1f, 1f)
            .requiresTool()
            .luminance(15)
            .sounds(new BlockSoundGroup(1.0f, 1.0f,
                    SoundEvents.BLOCK_CORAL_BLOCK_BREAK,
                    SoundEvents.BLOCK_CORAL_BLOCK_STEP,
                    SoundEvents.BLOCK_CORAL_BLOCK_PLACE,
                    SoundEvents.BLOCK_CORAL_BLOCK_HIT,
                    SoundEvents.BLOCK_CORAL_BLOCK_STEP
            ));

    private static final Block REINFORCED_DEEPSLATE = new PillarBlock(REINFORCED_DEEPSLATE_SETTINGS);

    public static final Block MUD = new MudBlock();
    public static final Block PACKED_MUD = new PackedMudBlock(PACKED_MUD_SETTINGS);
    public static final Block MUDDY_MANGROVE_ROOTS = new PillarBlock(AbstractBlock.Settings.of(Material.SOIL, MapColor.SPRUCE_BROWN).strength(0.7F).sounds(MUDDY_MANGROVE_ROOTS_SOUNDS).nonOpaque().allowsSpawning(RegisterBlocks::canSpawnOnLeaves).suffocates(RegisterBlocks::never).blockVision(RegisterBlocks::never).nonOpaque());
    public static final Block MUD_BRICKS = new MudBricks(MUD_BRICKS_SETTINGS);
    public static final WallBlock MUD_BRICKS_WALL = new MudBricksWall(MUD_BRICKS_SETTINGS);
    public static final SlabBlock MUD_BRICKS_SLAB = new MudBricksSlab(MUD_BRICKS_SETTINGS);
    public static final StairsBlock MUD_BRICKS_STAIRS = new CustomStairs(Blocks.OAK_STAIRS.getDefaultState(), MUD_BRICKS_SETTINGS);

    public static final Block SCULK_CATALYST = new SculkCatalystBlock(AbstractBlock.Settings.of(Material.SCULK).strength(3.0F, 3.0F).sounds(SCULK_CATALYST_SOUNDS).luminance(state -> 6));

    public static final Block SCULK = new SculkBlock(AbstractBlock.Settings.of(Material.SCULK).strength(0.2F).sounds(SCULK_SOUNDS));
    public static final SculkShriekerBlock SCULK_SHRIEKER = new SculkShriekerBlock(AbstractBlock.Settings.of(Material.SCULK, MapColor.BLACK).strength(3.0F, 3.0F).sounds(SCULK_SHRIEKER_SOUNDS));
    public static final Block OCHRE_FROGLIGHT = new PillarBlock(FROGLIGHT_SETTINGS);
    public static final Block PEARLESCENT_FROGLIGHT = new PillarBlock(FROGLIGHT_SETTINGS);
    public static final Block VERDANT_FROGLIGHT = new PillarBlock(FROGLIGHT_SETTINGS);


    // ALL BLOCKS HERE HAVE NO COLLISION
    public static final Block FROGSPAWN = new FrogspawnBlock(FROGSPAWN_PROPERTIES.nonOpaque().noCollision());
    public static final Block SCULK_VEIN = new SculkVeinBlock(AbstractBlock.Settings.of(Material.SCULK).noCollision().strength(0.2F).sounds(SCULK_VEIN_SOUNDS));

    public static void RegisterBlocks() {

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mud"), MUD);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud"), new BlockItem(MUD, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "packed_mud"), PACKED_MUD);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "packed_mud"), new BlockItem(PACKED_MUD, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

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

        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk_sensor"), new BlockItem(Blocks.SCULK_SENSOR, new FabricItemSettings().group(ItemGroup.REDSTONE)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "sculk_catalyst"), SCULK_CATALYST);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk_catalyst"), new BlockItem(SCULK_CATALYST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "sculk_shrieker"), RegisterBlocks.SCULK_SHRIEKER);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "sculk_shrieker"), new BlockItem(RegisterBlocks.SCULK_SHRIEKER, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "reinforced_deepslate"), REINFORCED_DEEPSLATE);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "reinforced_deepslate"), new BlockItem(REINFORCED_DEEPSLATE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "frogspawn"), FROGSPAWN);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "frogspawn"), new FrogSpawnItem(FROGSPAWN, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "pearlescent_froglight"), PEARLESCENT_FROGLIGHT);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "pearlescent_froglight"), new BlockItem(PEARLESCENT_FROGLIGHT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "verdant_froglight"), VERDANT_FROGLIGHT);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "verdant_froglight"), new BlockItem(VERDANT_FROGLIGHT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "ochre_froglight"), OCHRE_FROGLIGHT);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "ochre_froglight"), new BlockItem(OCHRE_FROGLIGHT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        MangroveWoods.RegisterMangrove();

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "muddy_mangrove_roots"), MUDDY_MANGROVE_ROOTS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "muddy_mangrove_roots"), new BlockItem(MUDDY_MANGROVE_ROOTS, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

    }


    protected static Boolean never(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return false;
    }

    protected static boolean never(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    protected static Boolean canSpawnOnLeaves(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return type == EntityType.OCELOT || type == EntityType.PARROT;
    }
}
