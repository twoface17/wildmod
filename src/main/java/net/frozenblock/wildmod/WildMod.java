package net.frozenblock.wildmod;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.mixin.object.builder.SpawnRestrictionAccessor;
import net.frozenblock.wildmod.entity.FrogEntity;
import net.frozenblock.wildmod.entity.FrogVariant;
import net.frozenblock.wildmod.entity.WildPacketByteBuf;
import net.frozenblock.wildmod.entity.WildTrackedDataHandler;
import net.frozenblock.wildmod.entity.ai.FrogBrain;
import net.frozenblock.wildmod.entity.ai.sensor.WardenAttackablesSensor;
import net.frozenblock.wildmod.event.WildBlockPositionSource;
import net.frozenblock.wildmod.event.WildEntityPositionSource;
import net.frozenblock.wildmod.event.WildGameEvent;
import net.frozenblock.wildmod.event.WildPositionSourceType;
import net.frozenblock.wildmod.fromAccurateSculk.WildBlockEntityType;
import net.frozenblock.wildmod.misc.FrogAttackablesSensor;
import net.frozenblock.wildmod.misc.IsInWaterSensor;
import net.frozenblock.wildmod.misc.ItemCriterion;
import net.frozenblock.wildmod.misc.animation.AnimationState;
import net.frozenblock.wildmod.registry.*;
import net.frozenblock.wildmod.world.feature.WildTreeRegistry;
import net.frozenblock.wildmod.world.gen.root.RootPlacerType;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.sensor.TemptationsSensor;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

public class WildMod implements ModInitializer {

    public static final String MOD_ID = "twm";

    // This will allow more things to write to the logs to provide more info for development. DISABLE BEFORE RELEASE UNLESS IT IS FOR FEEDBACK
    public static boolean debugMode = true;


    public static final EntityPose CROAKING = ClassTinkerers.getEnum(EntityPose.class, "CROAKING");
    public static final EntityPose USING_TONGUE = ClassTinkerers.getEnum(EntityPose.class, "USING_TONGUE");
    public static final EntityPose ROARING = ClassTinkerers.getEnum(EntityPose.class, "ROARING");
    public static final EntityPose SNIFFING = ClassTinkerers.getEnum(EntityPose.class, "SNIFFING");
    public static final EntityPose EMERGING = ClassTinkerers.getEnum(EntityPose.class, "EMERGING");
    public static final EntityPose DIGGING = ClassTinkerers.getEnum(EntityPose.class, "DIGGING");

    public static final UseAction TOOT_HORN = ClassTinkerers.getEnum(UseAction.class, "TOOT_HORN");

    public static final ItemCriterion ALLAY_DROP_ITEM_ON_BLOCK = new ItemCriterion(new Identifier(WildMod.MOD_ID, "allay_drop_item_on_block"));
    public static final WildPositionSourceType<WildEntityPositionSource> ENTITY = WildPositionSourceType.register("entity", new WildEntityPositionSource.Type());
    public static final WildPositionSourceType<WildBlockPositionSource> BLOCK = WildPositionSourceType.register("block", new WildBlockPositionSource.Type());

    static <T> TrackedDataHandler<Optional<T>> ofOptional(WildPacketByteBuf.PacketWriter<T> packetWriter, WildPacketByteBuf.PacketReader<T> packetReader) {
        return WildTrackedDataHandler.of(packetWriter.asOptional(), packetReader.asOptional());
    }

    public static TrackedDataHandler<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS;

    public static TrackedData<Optional<GlobalPos>> LAST_DEATH_POS;

    @Override
    public void onInitialize() {
        WildRegistry.init();
        RegisterMemoryModules.init();
        RegisterBlocks.register();
        OPTIONAL_GLOBAL_POS = ofOptional(WildPacketByteBuf::writeGlobalPos, WildPacketByteBuf::readGlobalPos);
        registerData(OPTIONAL_GLOBAL_POS);
        LAST_DEATH_POS = DataTracker.registerData(PlayerEntity.class, WildMod.OPTIONAL_GLOBAL_POS);
        RegisterItems.RegisterItems();
        RegisterEntities.init();
        FrogVariant.init();
        RegisterTags.init();
        WildPositionSourceType.init();

        RegisterDispenser.RegisterDispenser();
        RegisterParticles.RegisterParticles();
        RegisterStatusEffects.RegisterStatusEffects();
        RegisterWorldgen.registerWorldgen();
        RootPlacerType.init();

        RegisterSounds.init();
        RegisterBlockSoundGroups.init();
        WildBlockEntityType.register();
        RegisterEnchantments.init();
        AnimationState.init();

        registerData(OPTIONAL_INT);
        registerData(WildRegistry.FROG_VARIANT_DATA);

        TONGUE = Registry.register(Registry.ACTIVITY, new Identifier(WildMod.MOD_ID, "tongue"), new Activity("tongue"));
        SWIM = Registry.register(Registry.ACTIVITY, new Identifier(WildMod.MOD_ID, "swim"), new Activity("swim"));
        LAY_SPAWN = Registry.register(Registry.ACTIVITY, new Identifier(WildMod.MOD_ID, "lay_spawn"), new Activity("lay_spawn"));
        SNIFF = Registry.register(Registry.ACTIVITY, new Identifier(WildMod.MOD_ID, "sniff"), new Activity("sniff"));
        INVESTIGATE = Registry.register(Registry.ACTIVITY, new Identifier(WildMod.MOD_ID, "investigate"), new Activity("investigate"));
        ROAR = Registry.register(Registry.ACTIVITY, new Identifier(WildMod.MOD_ID, "roar"), new Activity("roar"));
        EMERGE = Registry.register(Registry.ACTIVITY, new Identifier(WildMod.MOD_ID, "emerge"), new Activity("emerge"));
        DIG = Registry.register(Registry.ACTIVITY, new Identifier(WildMod.MOD_ID, "dig"), new Activity("dig"));

        RegisterAccurateSculk.register();
        WildGameEvent.RegisterGameEvents();
        RegisterRecoveryCompass.registerRecovery();
        WildTreeRegistry.init();

        SpawnRestrictionAccessor.callRegister(RegisterEntities.FROG, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FrogEntity::canSpawn);
    }

    public static void registerData(TrackedDataHandler<?> handler) {
        TrackedDataHandlerRegistry.DATA_HANDLERS.add(handler);
    }

    public static <T> T registerInRegistryVanilla(Registry<T> registry, Identifier identifier, T values) {
        return Registry.register(registry, identifier, values);
    }

    //public static Registry<Animation> ANIMATION_DEFINITIONS = FabricRegistryBuilder.createSimple(Animation.class, new Identifier(WildMod.MOD_ID, "animation_definitions")).buildAndRegister();
    //public static Registry<Transformation.Interpolations> ANIMATION_CHANNEL_INTERPOLATIONS = FabricRegistryBuilder.createSimple(Transformation.Interpolations.class, new Identifier(WildMod.MOD_ID, "animation_channel_interpolations")).buildAndRegister();
    //public static Registry<Transformation.Target> ANIMATION_CHANNEL_TARGETS = FabricRegistryBuilder.createSimple(Transformation.Target.class, new Identifier(WildMod.MOD_ID, "animation_channel_targets")).buildAndRegister();

    private static <U extends Sensor<?>> SensorType<U> registerSensorType(String id, Supplier<U> factory) {
        return Registry.register(Registry.SENSOR_TYPE, new Identifier(WildMod.MOD_ID, id), new SensorType<>(factory));
    }

    public static final SensorType<TemptationsSensor> FROG_TEMPTATIONS = registerSensorType("frog_temptations", () -> new TemptationsSensor(FrogBrain.getTemptItems()));
    public static final SensorType<FrogAttackablesSensor> FROG_ATTACKABLES = registerSensorType("frog_attackables", FrogAttackablesSensor::new);
    public static final SensorType<IsInWaterSensor> IS_IN_WATER = registerSensorType("is_in_water", IsInWaterSensor::new);
    public static final SensorType<WardenAttackablesSensor> WARDEN_ENTITY_SENSOR = registerSensorType("warden_entity_sensor", WardenAttackablesSensor::new);

    public static Activity TONGUE;
    public static Activity SWIM;
    public static Activity LAY_SPAWN;
    public static Activity SNIFF;
    public static Activity INVESTIGATE;
    public static Activity ROAR;
    public static Activity EMERGE;
    public static Activity DIG;

    public static final TrackedDataHandler<OptionalInt> OPTIONAL_INT = new TrackedDataHandler<>() {
        public void write(PacketByteBuf packetByteBuf, OptionalInt optionalInt) {
            packetByteBuf.writeVarInt(optionalInt.orElse(-1) + 1);
        }

        public OptionalInt read(PacketByteBuf packetByteBuf) {
            int i = packetByteBuf.readVarInt();
            return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
        }

        public OptionalInt copy(OptionalInt optionalInt) {
            return optionalInt;
        }
    };

    public static final GameRules.Key<GameRules.BooleanRule> DARKNESS_ENABLED =
            GameRuleRegistry.register("doDarkness", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> SHRIEKER_NEEDS_SCULK =
            GameRuleRegistry.register("shriekerNeedsSculk", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.BooleanRule> SHRIEKER_GARGLES =
            GameRuleRegistry.register("shriekerGargles", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.IntRule> SCULK_MULTIPLIER =
            GameRuleRegistry.register("spreadMultiplier", GameRules.Category.MISC, GameRuleFactory.createIntRule(1, 1, 20));
    public static final GameRules.Key<GameRules.IntRule> UPWARD_SPREAD =
            GameRuleRegistry.register("upwardSpread", GameRules.Category.MISC, GameRuleFactory.createIntRule(12, 1, 100));
    public static final GameRules.Key<GameRules.IntRule> DOWNWARD_SPREAD =
            GameRuleRegistry.register("downwardSpread", GameRules.Category.MISC, GameRuleFactory.createIntRule(12, 1, 100));
    public static final GameRules.Key<GameRules.BooleanRule> SCULK_STOPS_SCULKCHECK = //PERFORMANCE GAMERULE, STOPS SCULKCHECK SEARCHING IF THE CURRENT BLOCK IS SCULK
            GameRuleRegistry.register("sculkStopsSculkCheck", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanRule> DO_WARDEN_SPAWNING =
            GameRuleRegistry.register("doWardenSpawning", GameRules.Category.SPAWNING, GameRuleFactory.createBooleanRule(true));
}
