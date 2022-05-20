package net.frozenblock.wildmod.fromAccurateSculk;

import net.frozenblock.wildmod.WildMod;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class SculkTags {
    public static final TagKey<Block> SCULK_VEIN_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk_vein_replaceable"));
    public static final TagKey<Block> SCULK_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk_replaceable"));
    public static final TagKey<Block> SCULK_REPLACEABLE_WORLD_GEN = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk_replaceable_world_gen"));
    public static final TagKey<Block> SCULK_UNTOUCHABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk_untouchable"));
    public static final TagKey<Block> SCULK = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk"));
    public static final TagKey<EntityType<?>> THREE = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "3xp"));
    public static final TagKey<EntityType<?>> FIVE = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "5xp"));
    public static final TagKey<EntityType<?>> TEN = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "10xp"));
    public static final TagKey<EntityType<?>> TWENTY = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "20xp"));
    public static final TagKey<EntityType<?>> FIFTY = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "50xp"));
    public static final TagKey<EntityType<?>> ONEHUNDRED = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "100xp"));
    public static final TagKey<EntityType<?>> DROPSXP = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(WildMod.MOD_ID, "dropsxp"));
    public static final TagKey<Block> ALWAYS_WATER = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "always_water_sculk"));
    public static final TagKey<Block> WARDEN_UNSPAWNABLE = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "warden_unspawnable"));
    public static final TagKey<Block> WARDEN_NON_COLLIDE = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "warden_non_collide"));
  
    //ACTIVATORS
    public static final TagKey<Block> ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "sculk_activators"));
    public static final TagKey<Block> COMMON_ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "common_activators"));
    public static final TagKey<Block> RARE_ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "rare_activators"));
    public static final TagKey<Block> GROUND_ACTIVATORS = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "ground_activators"));
    public static final TagKey<Block> OCCLUDES_VIBRATION_SIGNALS = TagKey.of(Registry.BLOCK_KEY, new Identifier(WildMod.MOD_ID, "occludes_vibration_signals"));
    public static boolean blockTagContains(Block block1, TagKey<Block> tag) {
        for (RegistryEntry<Block> block : Registry.BLOCK.iterateEntries(tag)) {
            if (block.getKey().equals(Registry.BLOCK.getKey(block1))) {return true;}
        } return false;
    }
    public static Block getRandomBlock(Random random, TagKey<Block> tag) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (RegistryEntry<Block> block : Registry.BLOCK.iterateEntries(tag)) {
            if (block.getKey().isPresent()) {
                Optional<Block> block1 = Registry.BLOCK.getOrEmpty(block.getKey().get());
                block1.ifPresent(blocks::add);
            }
        }
        if (!blocks.isEmpty()) { return blocks.get((int) (Math.random() * blocks.size())); }
        return null;
    }
    public static boolean fluidTagContains(Fluid fluid1, TagKey<Fluid> tag) {
        for (RegistryEntry<Fluid> fluid : Registry.FLUID.iterateEntries(tag)) {
            if (fluid.getKey().equals(Registry.FLUID.getKey(fluid1))) {return true;}
        } return false;
    }
    public static boolean entityTagContains(EntityType<?> type, TagKey<EntityType<?>> tag) {
        for (RegistryEntry<EntityType<?>> entity : Registry.ENTITY_TYPE.iterateEntries(tag)) {
            if (entity.getKey().equals(Registry.ENTITY_TYPE.getKey(type))) {return true;}
        } return false;
    }
    public static boolean itemTagContains(Item item1, TagKey<Item> tag) {
        for (RegistryEntry<Item> item : Registry.ITEM.iterateEntries(tag)) {
            if (item.getKey().equals(Registry.ITEM.getKey(item1))) { return true; }
        } return false;
    }
}
