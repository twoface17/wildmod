package net.frozenblock.wildmod.registry;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.WildMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RegisterMemoryModules {
    public static final MemoryModuleType<Unit> IS_PREGNANT = register("is_pregnant", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> IS_IN_WATER = register("is_in_water", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<LivingEntity> ROAR_TARGET = register("roar_target");
    public static final MemoryModuleType<Unit> RECENT_PROJECTILE = register("recent_projectile", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> IS_SNIFFING = register("is_sniffing", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> IS_EMERGING = register("is_emerging", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> ROAR_SOUND_DELAY = register("roar_sound_delay", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> DIG_COOLDOWN = register("dig_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> ROAR_SOUND_COOLDOWN = register("roar_sound_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> SNIFF_COOLDOWN = register("sniff_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> TOUCH_COOLDOWN = register("touch_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> VIBRATION_COOLDOWN = register("vibration_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> SONIC_BOOM_COOLDOWN = register("sonic_boom_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> SONIC_BOOM_SOUND_COOLDOWN = register("sonic_boom_sound_cooldown", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<Unit> SONIC_BOOM_SOUND_DELAY = register("sonic_boom_sound_delay", Codec.unit(Unit.INSTANCE));
    public static final MemoryModuleType<UUID> LIKED_PLAYER = register("liked_player", DynamicSerializableUuid.CODEC);
    public static final MemoryModuleType<GlobalPos> LIKED_NOTEBLOCK = register("liked_noteblock", GlobalPos.CODEC);
    public static final MemoryModuleType<Integer> LIKED_NOTEBLOCK_COOLDOWN_TICKS = register("liked_noteblock_cooldown_ticks", Codec.INT);
    public static final MemoryModuleType<Integer> ITEM_PICKUP_COOLDOWN_TICKS = register("item_pickup_cooldown_ticks", Codec.INT);
    public static final MemoryModuleType<BlockPos> DISTURBANCE_LOCATION = register("disturbance_location");
    public static final MemoryModuleType<Boolean> IS_PANICKING = register("is_panicking", Codec.BOOL);
    public static final MemoryModuleType<List<UUID>> UNREACHABLE_TONGUE_TARGETS = register("unreachable_tongue_targets");


    public static void init() {

    }

    public RegisterMemoryModules() {
    }

    private static <U> MemoryModuleType<U> register(String id, Codec<U> codec) {
        return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(WildMod.MOD_ID, id), new MemoryModuleType<>(Optional.of(codec)));
    }

    private static <U> MemoryModuleType<U> register(String id) {
        return Registry.register(Registry.MEMORY_MODULE_TYPE, new Identifier(WildMod.MOD_ID, id), new MemoryModuleType<>(Optional.empty()));
    }

}
