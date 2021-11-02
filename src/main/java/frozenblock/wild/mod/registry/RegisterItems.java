package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RegisterItems {

    public static final Item WARDEN_SPAWN_EGG = new SpawnEggItem(RegisterEntities.WARDEN, Integer.parseInt("264A50", 16), Integer.parseInt("50979B", 16), new FabricItemSettings().group(ItemGroup.MISC));

    public static void RegisterItems() {
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "warden_spawn_egg"), WARDEN_SPAWN_EGG);

    }
}
