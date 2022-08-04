package net.frozenblock.wildmod.registry;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.*;
import net.frozenblock.wildmod.entity.chestboat.ChestBoatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class RegisterEntities {

    public static final EntityType<ChestBoatEntity> CHEST_BOAT = register(
            "chest_boat", EntityType.Builder.<ChestBoatEntity>create(ChestBoatEntity::new, SpawnGroup.MISC).setDimensions(1.375F, 0.5625F).maxTrackingRange(10)
    );

    public static final EntityType<WardenEntity> WARDEN = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "warden"), FabricEntityTypeBuilder.createLiving().spawnGroup(SpawnGroup.MONSTER).entityFactory(WardenEntity::new).dimensions(EntityDimensions.fixed(0.9F, 2.9F)).trackRangeBlocks(16).fireImmune().defaultAttributes(WardenEntity::addAttributes).build());
    public static final EntityType<AllayEntity> ALLAY = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "allay"), FabricEntityTypeBuilder.createLiving().spawnGroup(SpawnGroup.CREATURE).entityFactory(AllayEntity::new).dimensions(EntityDimensions.fixed(0.35F, 0.6F)).trackRangeBlocks(8).trackedUpdateRate(2).defaultAttributes(AllayEntity::createAllayAttributes).build());
    public static final EntityType<FrogEntity> FROG = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "frog"), FabricEntityTypeBuilder.createLiving().spawnGroup(SpawnGroup.CREATURE).entityFactory(FrogEntity::new).dimensions(EntityDimensions.fixed(0.5F, 0.5F)).trackRangeBlocks(10).defaultAttributes(FrogEntity::createFrogAttributes).build());
    public static final EntityType<TadpoleEntity> TADPOLE = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "tadpole"), FabricEntityTypeBuilder.createLiving().spawnGroup(SpawnGroup.CREATURE).entityFactory(TadpoleEntity::new).dimensions(EntityDimensions.fixed(TadpoleEntity.WIDTH, TadpoleEntity.HEIGHT)).trackRangeBlocks(10).defaultAttributes(TadpoleEntity::createTadpoleAttributes).build());
    public static final EntityType<FireflyEntity> FIREFLY = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "firefly"), FabricEntityTypeBuilder.createLiving().spawnGroup(SpawnGroup.CREATURE).entityFactory(FireflyEntity::new).dimensions(EntityDimensions.fixed(0.3F, 0.3F)).defaultAttributes(FireflyEntity::createFireflyAttributes).build());

    public static void init() {
        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.SWAMP), FROG.getSpawnGroup(), RegisterEntities.FROG, 10, 2, 5);
        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.SWAMP), FIREFLY.getSpawnGroup(), RegisterEntities.FIREFLY, 8, 2, 4);
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, id), type.build(id));
    }
}
