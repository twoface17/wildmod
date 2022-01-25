package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.entity.chestboat.ChestBoatEntity;
import frozenblock.wild.mod.items.*;
import frozenblock.wild.mod.items.ChestBoatItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RegisterItems {

    public static final MangroveBoatItem MANGROVE_BOAT = new MangroveBoatItem(new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));

    public static final ChestBoatItem OAK_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.OAK, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem SPRUCE_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.SPRUCE, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem BIRCH_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.BIRCH, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem JUNGLE_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.JUNGLE, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem ACACIA_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.ACACIA, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem DARK_OAK_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.DARK_OAK, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem MANGROVE_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.MANGROVE, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));

    public static final Item WARDEN_SPAWN_EGG = new SpawnEggItem(RegisterEntities.WARDEN, Integer.parseInt("074857", 16), Integer.parseInt("29dfeb", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final Item FROG_SPAWN_EGG = new SpawnEggItem(RegisterEntities.FROG, Integer.parseInt("d07444", 16), Integer.parseInt("ffc77c", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final Item TADPOLE_SPAWN_EGG = new SpawnEggItem(RegisterEntities.TADPOLE, Integer.parseInt("160a00", 16), Integer.parseInt("332115", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final Item FIREFLY_SPAWN_EGG = new SpawnEggItem(RegisterEntities.FIREFLY, Integer.parseInt("000000", 16), Integer.parseInt("cfff00", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final EntityBucketItem TADPOLE_BUCKET = new EntityBucketItem(RegisterEntities.TADPOLE, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));

    public static void RegisterItems() {
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "warden_spawn_egg"), WARDEN_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "frog_spawn_egg"), FROG_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "tadpole_bucket"), TADPOLE_BUCKET);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "tadpole_spawn_egg"), TADPOLE_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "firefly_spawn_egg"), FIREFLY_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_boat"), MANGROVE_BOAT);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "oak_chest_boat"), OAK_CHEST_BOAT);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "spruce_chest_boat"), SPRUCE_CHEST_BOAT);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "birch_chest_boat"), BIRCH_CHEST_BOAT);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "jungle_chest_boat"), JUNGLE_CHEST_BOAT);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "acacia_chest_boat"), ACACIA_CHEST_BOAT);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "dark_oak_chest_boat"), DARK_OAK_CHEST_BOAT);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mangrove_chest_boat"), MANGROVE_CHEST_BOAT);

    }
}
