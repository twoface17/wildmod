package net.frozenblock.wildmod.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.enchantments.SwiftSneakEnchantment;
import net.frozenblock.wildmod.entity.chestboat.ChestBoatEntity;
import net.frozenblock.wildmod.items.*;
import net.frozenblock.wildmod.liukrastapi.SetGoatHornSoundLootFunction;
import net.frozenblock.wildmod.mixins.MusicDiscItemInvoker;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static net.minecraft.enchantment.EnchantmentHelper.getEquipmentLevel;

public abstract class RegisterItems {

    public static final MangroveBoatItem MANGROVE_BOAT = new MangroveBoatItem(new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));

    public static final ChestBoatItem OAK_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.OAK, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem SPRUCE_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.SPRUCE, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem BIRCH_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.BIRCH, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem JUNGLE_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.JUNGLE, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem ACACIA_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.ACACIA, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem DARK_OAK_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.DARK_OAK, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));
    public static final ChestBoatItem MANGROVE_CHEST_BOAT = new ChestBoatItem(ChestBoatEntity.Type.MANGROVE, new FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(1));

    public static final Enchantment SWIFT_SNEAK = new SwiftSneakEnchantment(Enchantment.Rarity.VERY_RARE, new EquipmentSlot[]{EquipmentSlot.LEGS});

    public static final Item WARDEN_SPAWN_EGG = new SpawnEggItem(RegisterEntities.WARDEN, Integer.parseInt("074857", 16), Integer.parseInt("29dfeb", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final Item ALLAY_SPAWN_EGG = new SpawnEggItem(RegisterEntities.ALLAY, Integer.parseInt("00CDF0", 16), Integer.parseInt("0097DE", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final Item FROG_SPAWN_EGG = new SpawnEggItem(RegisterEntities.FROG, Integer.parseInt("d07444", 16), Integer.parseInt("ffc77c", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final Item TADPOLE_SPAWN_EGG = new SpawnEggItem(RegisterEntities.TADPOLE, Integer.parseInt("160a00", 16), Integer.parseInt("332115", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final Item FIREFLY_SPAWN_EGG = new SpawnEggItem(RegisterEntities.FIREFLY, Integer.parseInt("000000", 16), Integer.parseInt("cfff00", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final EntityBucketItem TADPOLE_BUCKET = new EntityBucketItem(RegisterEntities.TADPOLE, Fluids.WATER, RegisterSounds.ITEM_BUCKET_EMPTY_TADPOLE, new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));

    public static final Item RECOVERY_COMPASS = new Item(new Item.Settings().maxCount(1).group(ItemGroup.TOOLS));
    public static final Item ECHO_SHARD = new Item(new Item.Settings().group(ItemGroup.TOOLS));

    public static final Item DISC_FRAGMENT_5 = new DiscFragmentItem(new FabricItemSettings().group(ItemGroup.MISC));
    public static final MusicDiscItem MUSIC_DISC_5 = MusicDiscItemInvoker.invokeConstructor(15, RegisterSounds.MUSIC_DISC_5, new FabricItemSettings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.RARE));

    public static final Item GOAT_HORN = new GoatHornItem(new Item.Settings().group(ItemGroup.MISC).maxCount(1), InstrumentTags.GOAT_HORNS);
    public static final LootFunctionType SET_INSTRUMENT = new LootFunctionType(new SetGoatHornSoundLootFunction.Serializer());

    public static float getSwiftSneakSpeedBoost(LivingEntity livingEntity) {
        return (float)getEquipmentLevel(SWIFT_SNEAK, livingEntity) * 0.15F;
    }

    public static void RegisterItems() {
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "warden_spawn_egg"), WARDEN_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "allay_spawn_egg"), ALLAY_SPAWN_EGG);
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
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "recovery_compass"), RECOVERY_COMPASS);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "echo_shard"), ECHO_SHARD);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "disc_fragment_5"), DISC_FRAGMENT_5);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "music_disc_5"), MUSIC_DISC_5);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "goat_horn"), GOAT_HORN);
        Registry.register(Registry.ENCHANTMENT, new Identifier(WildMod.MOD_ID, "swift_sneak"), SWIFT_SNEAK);
        Registry.register(Registry.LOOT_FUNCTION_TYPE, new Identifier(WildMod.MOD_ID, "set_instrument"), SET_INSTRUMENT);
    }
}
