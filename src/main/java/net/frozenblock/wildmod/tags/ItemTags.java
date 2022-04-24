package net.frozenblock.wildmod.tags;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemTags {
    public static final TagKey<Item> AXES = TagKey.of(Registry.ITEM_KEY, new Identifier("fabric", "axes"));
}
