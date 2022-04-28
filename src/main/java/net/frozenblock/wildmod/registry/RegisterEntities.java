package net.frozenblock.wildmod.registry;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.WildMod;
import net.frozenblock.wildmod.entity.*;
import net.frozenblock.wildmod.entity.chestboat.ChestBoatEntity;
import net.frozenblock.wildmod.mixins.MemoryModuleTypeInvoker;
import net.frozenblock.wildmod.mixins.MemoryModuleTypeInvoker2;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

import java.util.UUID;

public class RegisterEntities {

    public static final MemoryModuleType<Unit> IS_PREGNANT = MemoryModuleTypeInvoker.callRegister("is_pregnant", Codec.unit(Unit.INSTANCE));;
    public static final MemoryModuleType<Unit> IS_IN_WATER = MemoryModuleTypeInvoker.callRegister("is_in_water", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<LivingEntity> ROAR_TARGET = MemoryModuleTypeInvoker2.callRegister("roar_target");
    public static final MemoryModuleType<BlockPos> DISTURBANCE_LOCATION = MemoryModuleTypeInvoker2.callRegister("disturbance_location");
    public static final MemoryModuleType<Unit> RECENT_PROJECTILE = MemoryModuleTypeInvoker.callRegister("recent_projectile", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> IS_SNIFFING = MemoryModuleTypeInvoker.callRegister("is_sniffing", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> IS_EMERGING = MemoryModuleTypeInvoker.callRegister("is_emerging", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> ROAR_SOUND_DELAY = MemoryModuleTypeInvoker.callRegister("roar_sound_delay", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> DIG_COOLDOWN = MemoryModuleTypeInvoker.callRegister("dig_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> ROAR_SOUND_COOLDOWN  = MemoryModuleTypeInvoker.callRegister("roar_sound_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> SNIFF_COOLDOWN = MemoryModuleTypeInvoker.callRegister("sniff_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> TOUCH_COOLDOWN = MemoryModuleTypeInvoker.callRegister("touch_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> VIBRATION_COOLDOWN = MemoryModuleTypeInvoker.callRegister("vibration_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> SONIC_BOOM_COOLDOWN = MemoryModuleTypeInvoker.callRegister("sonic_boom_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> SONIC_BOOM_SOUND_COOLDOWN = MemoryModuleTypeInvoker.callRegister("sonic_boom_sound_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> SONIC_BOOM_SOUND_DELAY = MemoryModuleTypeInvoker.callRegister("sonic_boom_sound_delay", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<UUID> LIKED_PLAYER = MemoryModuleTypeInvoker.callRegister("liked_player", DynamicSerializableUuid.CODEC);
    public static final MemoryModuleType<GlobalPos> LIKED_NOTEBLOCK = MemoryModuleTypeInvoker.callRegister("liked_noteblock", GlobalPos.CODEC);
    public static final MemoryModuleType<Integer> LIKED_NOTEBLOCK_COOLDOWN_TICKS = MemoryModuleTypeInvoker.callRegister("liked_noteblock_cooldown_ticks", Codec.INT);
    public static final MemoryModuleType<Integer> ITEM_PICKUP_COOLDOWN_TICKS = MemoryModuleTypeInvoker.callRegister("item_pickup_cooldown_ticks", Codec.INT);

    public static final TagKey<GameEvent> WARDEN_CAN_LISTEN = of("warden_can_listen");
    public static final TagKey<GameEvent> SHRIEKER_CAN_LISTEN = of("shrieker_can_listen");
    public static final TagKey<GameEvent> IGNORE_VIBRATIONS_ON_OCCLUDING_BLOCK = of("ignore_vibrations_on_occluding_block");

    private static TagKey<GameEvent> of(String id) {
        return TagKey.of(Registry.GAME_EVENT_KEY, new Identifier(id));
    }

    public static EntityType<MangroveBoatEntity> MANGROVE_BOAT;
    public static EntityType<ChestBoatEntity> CHEST_BOAT;
    public static final EntityType<WardenEntity> WARDEN = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "warden"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WardenEntity::new).dimensions(EntityDimensions.fixed(0.9f, 2.95f)).build());
    public static final EntityType<AllayEntity> ALLAY = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "allay"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, AllayEntity::new).dimensions(EntityDimensions.fixed(0.8f, 0.8f)).build());
    public static final EntityType<FrogEntity> FROG = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "frog"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FrogEntity::new).dimensions(EntityDimensions.fixed(0.5F, 0.5F)).build());
    public static final EntityType<TadpoleEntity> TADPOLE = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "tadpole"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TadpoleEntity::new).dimensions(EntityDimensions.fixed(0.5F, 0.4F)).build());
    public static final EntityType<FireflyEntity> FIREFLY = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "firefly"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FireflyEntity::new).dimensions(EntityDimensions.fixed(0.1F, 0.1F)).build());

    public static void RegisterEntities() {
        MANGROVE_BOAT = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "mangrove_boat"), FabricEntityTypeBuilder.<MangroveBoatEntity>create(SpawnGroup.MISC, MangroveBoatEntity::new).dimensions(EntityDimensions.fixed(1.375F, 0.5625F)).build());
        CHEST_BOAT = Registry.register(Registry.ENTITY_TYPE, new Identifier(WildMod.MOD_ID, "chest_boat"), FabricEntityTypeBuilder.<ChestBoatEntity>create(SpawnGroup.MISC, ChestBoatEntity::new).dimensions(EntityDimensions.fixed(1.375F, 0.5625F)).build());
        FabricDefaultAttributeRegistry.register(WARDEN, WardenEntity.addAttributes());
        FabricDefaultAttributeRegistry.register(ALLAY, AllayEntity.createAllayAttributes());
        FabricDefaultAttributeRegistry.register(FROG, FrogEntity.createFrogAttributes());
        FabricDefaultAttributeRegistry.register(TADPOLE, TadpoleEntity.createTadpoleAttributes());
        FabricDefaultAttributeRegistry.register(FIREFLY, FireflyEntity.createFireflyAttributes());

        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.SWAMP), FROG.getSpawnGroup(), RegisterEntities.FROG, 200, 3, 6);
        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.SWAMP), FIREFLY.getSpawnGroup(), RegisterEntities.FIREFLY, 200, 4, 10);
    }
}
