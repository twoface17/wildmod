package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class ModItems {

    public static final Item MUD_BALL = new Item(new Item.Settings().group(ItemGroup.MISC));

    public static void RegisterItems() {

        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "mud_ball"), MUD_BALL);

    }
}
