package net.frozenblock.wildmod.entity;

import net.frozenblock.wildmod.liukrastapi.MathAddon;
import net.frozenblock.wildmod.liukrastapi.StatusEffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class StatusEffectUtil {
    public StatusEffectUtil() {
    }

    public static String durationToString(StatusEffectInstance effect, float multiplier) {
        if (effect.isPermanent()) {
            return "**:**";
        } else {
            int i = MathAddon.floor((float)effect.getDuration() * multiplier);
            return StringHelper.formatTicks(i);
        }
    }

    public static boolean hasHaste(net.minecraft.entity.LivingEntity entity) {
        return entity.hasStatusEffect(StatusEffects.HASTE) || entity.hasStatusEffect(StatusEffects.CONDUIT_POWER);
    }

    public static int getHasteAmplifier(net.minecraft.entity.LivingEntity entity) {
        int i = 0;
        int j = 0;
        if (entity.hasStatusEffect(StatusEffects.HASTE)) {
            i = Objects.requireNonNull(entity.getStatusEffect(StatusEffects.HASTE)).getAmplifier();
        }

        if (entity.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
            j = Objects.requireNonNull(entity.getStatusEffect(StatusEffects.CONDUIT_POWER)).getAmplifier();
        }

        return Math.max(i, j);
    }

    public static boolean hasWaterBreathing(LivingEntity entity) {
        return entity.hasStatusEffect(StatusEffects.WATER_BREATHING) || entity.hasStatusEffect(StatusEffects.CONDUIT_POWER);
    }

    public static List<ServerPlayerEntity> addEffectToPlayersWithinDistance(
            ServerWorld world, @Nullable Entity entity, Vec3d origin, double range, StatusEffectInstance statusEffectInstance, int duration
    ) {
        StatusEffect statusEffect = statusEffectInstance.getEffectType();
        List<ServerPlayerEntity> list = world.getPlayers(
                player -> player.interactionManager.isSurvivalLike()
                        && origin.isInRange(player.getPos(), range)
                        && (
                        !player.hasStatusEffect(statusEffect)
                                || Objects.requireNonNull(player.getStatusEffect(statusEffect)).getAmplifier() < statusEffectInstance.getAmplifier()
                                || Objects.requireNonNull(player.getStatusEffect(statusEffect)).getDuration() < duration
                )
        );
        list.forEach(player -> player.addStatusEffect(new StatusEffectInstance(statusEffectInstance), entity));
        return list;
    }
}
