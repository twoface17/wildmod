package net.frozenblock.wildmod.registry;

import com.mojang.serialization.Codec;
import net.frozenblock.wildmod.mixins.MemoryModuleTypeInvoker;
import net.frozenblock.wildmod.mixins.MemoryModuleTypeInvoker2;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class RegisterMemoryModules {
    public static MemoryModuleType<Unit> IS_PREGNANT;
    public static MemoryModuleType<Unit> IS_IN_WATER;
    public static MemoryModuleType<LivingEntity> ROAR_TARGET;
    public static MemoryModuleType<Unit> RECENT_PROJECTILE;
    public static MemoryModuleType<Unit> IS_SNIFFING;
    public static MemoryModuleType<Unit> IS_EMERGING;
    public static MemoryModuleType<Unit> ROAR_SOUND_DELAY;
    public static MemoryModuleType<Unit> DIG_COOLDOWN;
    public static MemoryModuleType<Unit> ROAR_SOUND_COOLDOWN;
    public static MemoryModuleType<Unit> SNIFF_COOLDOWN;
    public static MemoryModuleType<Unit> TOUCH_COOLDOWN;
    public static MemoryModuleType<Unit> VIBRATION_COOLDOWN;
    public static MemoryModuleType<Unit> SONIC_BOOM_COOLDOWN;
    public static MemoryModuleType<Unit> SONIC_BOOM_SOUND_COOLDOWN;
    public static MemoryModuleType<Unit> SONIC_BOOM_SOUND_DELAY;
    public static MemoryModuleType<UUID> LIKED_PLAYER;
    public static MemoryModuleType<GlobalPos> LIKED_NOTEBLOCK;
    public static MemoryModuleType<Integer> LIKED_NOTEBLOCK_COOLDOWN_TICKS;
    public static MemoryModuleType<Integer> ITEM_PICKUP_COOLDOWN_TICKS;
    public static MemoryModuleType<BlockPos> DISTURBANCE_LOCATION;

    public static void RegisterMemoryModules() {

        IS_PREGNANT = MemoryModuleTypeInvoker.callRegister("is_pregnant", Codec.unit(Unit.INSTANCE));
        IS_IN_WATER = MemoryModuleTypeInvoker.callRegister("is_in_water", Codec.unit(Unit.INSTANCE));
        ROAR_TARGET = MemoryModuleTypeInvoker2.callRegister("roar_target");
        DISTURBANCE_LOCATION = MemoryModuleTypeInvoker2.callRegister("disturbance_location");
        RECENT_PROJECTILE = MemoryModuleTypeInvoker.callRegister("recent_projectile", Codec.unit(Unit.INSTANCE));
        IS_SNIFFING = MemoryModuleTypeInvoker.callRegister("is_sniffing", Codec.unit(Unit.INSTANCE));
        IS_EMERGING = MemoryModuleTypeInvoker.callRegister("is_emerging", Codec.unit(Unit.INSTANCE));
        ROAR_SOUND_DELAY = MemoryModuleTypeInvoker.callRegister("roar_sound_delay", Codec.unit(Unit.INSTANCE));
        DIG_COOLDOWN = MemoryModuleTypeInvoker.callRegister("dig_cooldown", Codec.unit(Unit.INSTANCE));
        ROAR_SOUND_COOLDOWN = MemoryModuleTypeInvoker.callRegister("roar_sound_cooldown", Codec.unit(Unit.INSTANCE));
        SNIFF_COOLDOWN = MemoryModuleTypeInvoker.callRegister("sniff_cooldown", Codec.unit(Unit.INSTANCE));
        TOUCH_COOLDOWN = MemoryModuleTypeInvoker.callRegister("touch_cooldown", Codec.unit(Unit.INSTANCE));
        VIBRATION_COOLDOWN = MemoryModuleTypeInvoker.callRegister("vibration_cooldown", Codec.unit(Unit.INSTANCE));
        SONIC_BOOM_COOLDOWN = MemoryModuleTypeInvoker.callRegister("sonic_boom_cooldown", Codec.unit(Unit.INSTANCE));
        SONIC_BOOM_SOUND_COOLDOWN = MemoryModuleTypeInvoker.callRegister("sonic_boom_sound_cooldown", Codec.unit(Unit.INSTANCE));
        SONIC_BOOM_SOUND_DELAY = MemoryModuleTypeInvoker.callRegister("sonic_boom_sound_delay", Codec.unit(Unit.INSTANCE));
        LIKED_PLAYER = MemoryModuleTypeInvoker.callRegister("liked_player", DynamicSerializableUuid.CODEC);
        LIKED_NOTEBLOCK = MemoryModuleTypeInvoker.callRegister("liked_noteblock", GlobalPos.CODEC);
        LIKED_NOTEBLOCK_COOLDOWN_TICKS = MemoryModuleTypeInvoker.callRegister("liked_noteblock_cooldown_ticks", Codec.INT);
        ITEM_PICKUP_COOLDOWN_TICKS = MemoryModuleTypeInvoker.callRegister("item_pickup_cooldown_ticks", Codec.INT);

    }
}
