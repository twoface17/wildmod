package frozenblock.wild.mod.liukrastapi;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WardenAngerManager {
    private static final int maxAnger = 150;
    private static final int angerDecreasePerTick = 1;
    //public static final Codec<WardenAngerManager> field_38127 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.unboundedMap(Codecs.field_38081, Codecs.NONNEGATIVE_INT).fieldOf("suspects")).forGetter(wardenAngerManager -> wardenAngerManager.suspects)).apply((Applicative<WardenAngerManager, ?>)instance, WardenAngerManager::new));
    private final Object2IntMap<UUID> suspects;

    public WardenAngerManager(Map<UUID, Integer> map) {
        this.suspects = new Object2IntOpenHashMap<>(map);
    }

    public void tick() {
        this.suspects.keySet().forEach(uUID2 -> this.suspects.computeInt(uUID2, (uUID, integer) -> {
            if (integer <= 1) {
                return null;
            }
            return Math.max(0, integer - 1);
        }));
    }

    public int increaseAngerAt(Entity entity, int i) {
        return this.suspects.computeInt(entity.getUuid(), (uUID, integer) -> Math.min(150, (integer == null ? 0 : integer) + i));
    }

    public void removeSuspect(Entity entity) {
        this.suspects.removeInt(entity.getUuid());
    }

    private Optional<Object2IntMap.Entry<UUID>> getPrimeSuspect() {
        return this.suspects.object2IntEntrySet().stream().max(Map.Entry.comparingByValue());
    }

    public int getPrimeSuspectAnger() {
        return this.getPrimeSuspect().map(Map.Entry::getValue).orElse(0);
    }

    public Optional<LivingEntity> getPrimeSuspectUuid(World world) {
        if (world instanceof ServerWorld serverWorld) {
            return this.getPrimeSuspect().map(Map.Entry::getKey).map(serverWorld::getEntity).filter(entity -> entity instanceof LivingEntity).map(entity -> (LivingEntity)entity);
        }
        return Optional.empty();
    }
}

