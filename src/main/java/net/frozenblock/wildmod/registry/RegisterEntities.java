package net.frozenblock.wildmod.registry;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.*;
import net.frozenblock.wildmod.entity.chestboat.ChestBoatEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

public class RegisterEntities {

    public static EntityType<MangroveBoatEntity> MANGROVE_BOAT = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "mangrove_boat"), FabricEntityTypeBuilder.<MangroveBoatEntity>create(SpawnGroup.MISC, MangroveBoatEntity::new).dimensions(EntityDimensions.fixed(1.375F, 0.5625F)).build());
    public static EntityType<ChestBoatEntity> CHEST_BOAT = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "chest_boat"), FabricEntityTypeBuilder.<ChestBoatEntity>create(SpawnGroup.MISC, ChestBoatEntity::new).dimensions(EntityDimensions.fixed(1.375F, 0.5625F)).build());
    public static final EntityType<WardenEntity> WARDEN = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "warden"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WardenEntity::new).dimensions(EntityDimensions.fixed(0.9f, 2.95f)).build());
    public static final EntityType<AllayEntity> ALLAY = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "allay"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, AllayEntity::new).dimensions(EntityDimensions.fixed(0.35F, 0.6F)).trackRangeBlocks(8).trackedUpdateRate(2).build());
    public static final EntityType<FrogEntity> FROG = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "frog"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FrogEntity::new).dimensions(EntityDimensions.fixed(0.5F, 0.5F)).build());
    public static final EntityType<TadpoleEntity> TADPOLE = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "tadpole"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TadpoleEntity::new).dimensions(EntityDimensions.fixed(0.5F, 0.4F)).build());
    public static final EntityType<FireflyEntity> FIREFLY = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "firefly"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FireflyEntity::new).dimensions(EntityDimensions.fixed(0.1F, 0.1F)).build());

    public static void init() {
        FabricDefaultAttributeRegistry.register(WARDEN, WardenEntity.addAttributes());
        FabricDefaultAttributeRegistry.register(ALLAY, AllayEntity.createAllayAttributes());
        FabricDefaultAttributeRegistry.register(FROG, FrogEntity.createFrogAttributes());
        FabricDefaultAttributeRegistry.register(TADPOLE, TadpoleEntity.createTadpoleAttributes());
        FabricDefaultAttributeRegistry.register(FIREFLY, FireflyEntity.createFireflyAttributes());

        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.SWAMP), FROG.getSpawnGroup(), RegisterEntities.FROG, 10, 2, 5);
        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.SWAMP), FIREFLY.getSpawnGroup(), RegisterEntities.FIREFLY, 8, 2, 4);
    }
}
