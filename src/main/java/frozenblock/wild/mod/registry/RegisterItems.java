package frozenblock.wild.mod.registry;

import frozenblock.wild.mod.WildMod;
import frozenblock.wild.mod.items.FrogEntitySpawnEgg;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RegisterItems {

    public static final Item WARDEN_SPAWN_EGG = new SpawnEggItem(RegisterEntities.WARDEN, Integer.parseInt("266368", 16), Integer.parseInt("c2bea1", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final Item FROG_SPAWN_EGG = new FrogEntitySpawnEgg(RegisterEntities.FROG,
            Integer.parseInt("d97c54", 16),
            Integer.parseInt("d5a56c", 16),
            new FabricItemSettings().group(ItemGroup.MISC));
    public static final Item TADPOLE_SPAWN_EGG = new SpawnEggItem(RegisterEntities.TADPOLE, Integer.parseInt("4a3729", 16), Integer.parseInt("332115", 16), new FabricItemSettings().group(ItemGroup.MISC));
    public static final EntityBucketItem TADPOLE_BUCKET = new EntityBucketItem(RegisterEntities.TADPOLE, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, new FabricItemSettings().group(ItemGroup.MISC));

    public static void RegisterItems() {
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "warden_spawn_egg"), WARDEN_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "frog_spawn_egg"), FROG_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "tadpole_bucket"), TADPOLE_BUCKET);
        Registry.register(Registry.ITEM, new Identifier(WildMod.MOD_ID, "tadpole_spawn_egg"), TADPOLE_SPAWN_EGG);

    }
}
