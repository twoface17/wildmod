package net.frozenblock.wildmod.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.block.swamp.MangroveLeavesBlock;
import net.frozenblock.wildmod.block.swamp.MangroveRootsBlock;
import net.frozenblock.wildmod.block.swamp.PropaguleBlock;
import net.frozenblock.wildmod.block.wild.WildSignBlock;
import net.frozenblock.wildmod.block.wild.WildSignType;
import net.frozenblock.wildmod.block.wild.WildWallSignBlock;
import net.minecraft.block.*;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SignItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public abstract class MangroveWoods {


    // THIS VARIABLE DEFINES ALL PROPERTIES FOR WOOD
    public static final AbstractBlock.Settings WOOD_PROPERTIES = FabricBlockSettings.copyOf(Blocks.OAK_PLANKS);

    // DEFINES ALL BLOCKS AS VARIABLES
    public static final Block MANGROVE_LOG = new PillarBlock(
            AbstractBlock.Settings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.RED : MapColor.SPRUCE_BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD)
    );
    public static final Block MANGROVE_PLANKS = new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.RED).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    public static final StairsBlock MANGROVE_STAIRS = new StairsBlock(MANGROVE_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(MANGROVE_PLANKS));
    public static final Block MANGROVE_DOOR = new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, MANGROVE_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque());
    public static final Block STRIPPED_MANGROVE_LOG = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, state -> MapColor.RED).strength(2.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block STRIPPED_MANGROVE_WOOD = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, state -> MapColor.RED).strength(2.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block MANGROVE_WOOD = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.RED).strength(2.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block MANGROVE_FENCE = new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, MANGROVE_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block MANGROVE_SLAB = new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.RED).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block MANGROVE_FENCE_GATE = new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, MANGROVE_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    public static final PressurePlateBlock MANGROVE_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.WOOD, MANGROVE_PLANKS.getDefaultMapColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
    public static final Block MANGROVE_TRAPDOOR = new TrapdoorBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.RED).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(RegisterBlocks::never));
    public static final Block MANGROVE_LEAVES = new MangroveLeavesBlock(AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(RegisterBlocks::canSpawnOnLeaves).suffocates(RegisterBlocks::never).blockVision(RegisterBlocks::never));
    public static final Block MANGROVE_ROOTS = new MangroveRootsBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(0.7F).ticksRandomly().sounds(new BlockSoundGroup(1.0F, 1.0F,
            RegisterSounds.BLOCK_MANGROVE_ROOTS_BREAK,
            RegisterSounds.BLOCK_MANGROVE_ROOTS_STEP,
            RegisterSounds.BLOCK_MANGROVE_ROOTS_PLACE,
            RegisterSounds.BLOCK_MANGROVE_ROOTS_HIT,
            RegisterSounds.BLOCK_MANGROVE_ROOTS_FALL
    )).nonOpaque().allowsSpawning(RegisterBlocks::canSpawnOnLeaves).suffocates(RegisterBlocks::never).blockVision(RegisterBlocks::never).nonOpaque());
    public static final Block MANGROVE_PROPAGULE = new PropaguleBlock(FabricBlockSettings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(new BlockSoundGroup(1.0f, 1.0f,
            RegisterSounds.BLOCK_MANGROVE_PROPAGULE_BREAK,
            RegisterSounds.BLOCK_MANGROVE_PROPAGULE_STEP,
            RegisterSounds.BLOCK_MANGROVE_PROPAGULE_PLACE,
            RegisterSounds.BLOCK_MANGROVE_PROPAGULE_HIT,
            RegisterSounds.BLOCK_MANGROVE_PROPAGULE_FALL
    )));
    public static final Block MANGROVE_BUTTON = new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
    public static final Block MANGROVE_SIGN = new WildSignBlock(AbstractBlock.Settings.of(Material.WOOD, MANGROVE_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), WildSignType.MANGROVE);
    public static final Block MANGROVE_WALL_SIGN = new WildWallSignBlock(AbstractBlock.Settings.of(Material.WOOD, MANGROVE_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(MANGROVE_SIGN), WildSignType.MANGROVE);

    public static final BlockFamily MANGROVE = BlockFamilies.register(MANGROVE_PLANKS)
            .button(MANGROVE_BUTTON)
            .slab(MANGROVE_SLAB)
            .stairs(MANGROVE_STAIRS)
            .fence(MANGROVE_FENCE)
            .fenceGate(MANGROVE_FENCE_GATE)
            .pressurePlate(MANGROVE_PRESSURE_PLATE)
            .sign(MANGROVE_SIGN, MANGROVE_WALL_SIGN)
            .door(MANGROVE_DOOR)
            .trapdoor(MANGROVE_TRAPDOOR)
            .group("wooden")
            .unlockCriterionName("has_planks")
            .build();

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

        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_sign"), MANGROVE_SIGN);
        Registry.register(Registry.BLOCK, new Identifier(WildMod.MOD_ID, "mangrove_wall_sign"), MANGROVE_WALL_SIGN);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_sign"), new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), MANGROVE_SIGN, MANGROVE_WALL_SIGN));

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


        StrippableBlockRegistry.register(MANGROVE_LOG, STRIPPED_MANGROVE_LOG);

        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_STAIRS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_DOOR, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_FENCE, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_SLAB, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_FENCE_GATE, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_PRESSURE_PLATE, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_TRAPDOOR, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_LEAVES, 100, 60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_ROOTS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_PROPAGULE, 100, 60);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_BUTTON, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_SIGN, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.MANGROVE_WALL_SIGN, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.STRIPPED_MANGROVE_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(MangroveWoods.STRIPPED_MANGROVE_WOOD, 5, 5);
    }
}
