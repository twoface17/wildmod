package net.frozenblock.wildmod.misc;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class WardenAngerManager {
    public static final Codec<UUID> UUID = DynamicSerializableUuid.CODEC;
    @VisibleForTesting
    protected static final int field_38733 = 2;
    @VisibleForTesting
    protected static final int maxAnger = 150;
    private static final int angerDecreasePerTick = 1;
    private int updateTimer = AdvancedMath.nextBetween(WildRandomUtils.createAtomic(), 0, 2);
    int primeAnger;
    private static final Codec<Pair<UUID, Integer>> SUSPECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(UUID.fieldOf("uuid").forGetter(Pair::getFirst), Codecs.NONNEGATIVE_INT.fieldOf("anger").forGetter(Pair::getSecond))
                    .apply(instance, Pair::of)
    );
    private final Predicate<Entity> suspectPredicate;
    @VisibleForTesting
    protected final ArrayList<Entity> suspects;
    private final WardenAngerManager.SuspectComparator suspectComparator;
    @VisibleForTesting
    protected final Object2IntMap<Entity> suspectsToAngerLevel;
    @VisibleForTesting
    protected final Object2IntMap<UUID> suspectUuidsToAngerLevel;

    public static Codec<WardenAngerManager> method_43692(Predicate<Entity> predicate) {
        return RecordCodecBuilder.create(
                instance -> instance.group(SUSPECT_CODEC.listOf().fieldOf("suspects").orElse(Collections.emptyList()).forGetter(WardenAngerManager::getSuspects))
                        .apply(instance, list -> new WardenAngerManager(predicate, list))
        );
    }

    public WardenAngerManager(Predicate<Entity> suspectPredicate, List<Pair<UUID, Integer>> suspectUuidsToAngerLevel) {
        this.suspectPredicate = suspectPredicate;
        this.suspects = new ArrayList<>();
        this.suspectComparator = new WardenAngerManager.SuspectComparator(this);
        this.suspectsToAngerLevel = new Object2IntOpenHashMap<>();
        this.suspectUuidsToAngerLevel = new Object2IntOpenHashMap<>(suspectUuidsToAngerLevel.size());
        suspectUuidsToAngerLevel.forEach(pair -> this.suspectUuidsToAngerLevel.put(pair.getFirst(), pair.getSecond()));
    }

    private List<Pair<UUID, Integer>> getSuspects() {
        return Streams.concat(
                        this.suspects.stream().map(suspect -> Pair.of(suspect.getUuid(), this.suspectsToAngerLevel.getInt(suspect))),
                        this.suspectUuidsToAngerLevel.object2IntEntrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getIntValue()))
                )
                .collect(Collectors.toList());
    }

    public void tick(ServerWorld world, Predicate<Entity> suspectPredicate) {
        --this.updateTimer;
        if (this.updateTimer <= 0) {
            this.updateSuspectsMap(world);
            this.updateTimer = 2;
        }

        ObjectIterator<Object2IntMap.Entry<UUID>> objectIterator = this.suspectUuidsToAngerLevel.object2IntEntrySet().iterator();

        while (objectIterator.hasNext()) {
            Object2IntMap.Entry<UUID> entry = objectIterator.next();
            int i = entry.getIntValue();
            if (i <= 1) {
                objectIterator.remove();
            } else {
                entry.setValue(i - 1);
            }
        }

        ObjectIterator<Object2IntMap.Entry<Entity>> objectIterator2 = this.suspectsToAngerLevel.object2IntEntrySet().iterator();

        while (objectIterator2.hasNext()) {
            Object2IntMap.Entry<Entity> entry2 = objectIterator2.next();
            int j = entry2.getIntValue();
            Entity entity = entry2.getKey();
            Entity.RemovalReason removalReason = entity.getRemovalReason();
            if (j > 1 && suspectPredicate.test(entity) && removalReason == null) {
                entry2.setValue(j - 1);
            } else {
                this.suspects.remove(entity);
                objectIterator2.remove();
                if (j > 1 && removalReason != null) {
                    switch (removalReason) {
                        case CHANGED_DIMENSION:
                        case UNLOADED_TO_CHUNK:
                        case UNLOADED_WITH_PLAYER:
                            this.suspectUuidsToAngerLevel.put(entity.getUuid(), j - 1);
                    }
                }
            }
        }

        this.updatePrimeAnger();
    }

    private void updatePrimeAnger() {
        this.primeAnger = 0;
        this.suspects.sort(this.suspectComparator);
        if (this.suspects.size() == 1) {
            this.primeAnger = this.suspectsToAngerLevel.getInt(this.suspects.get(0));
        }

    }

    private void updateSuspectsMap(ServerWorld world) {
        ObjectIterator<Object2IntMap.Entry<UUID>> objectIterator = this.suspectUuidsToAngerLevel.object2IntEntrySet().iterator();

        while (objectIterator.hasNext()) {
            Object2IntMap.Entry<UUID> entry = objectIterator.next();
            int i = entry.getIntValue();
            Entity entity = world.getEntity(entry.getKey());
            if (entity != null) {
                this.suspectsToAngerLevel.put(entity, i);
                this.suspects.add(entity);
                objectIterator.remove();
            }
        }

    }

    public int increaseAngerAt(Entity entity, int amount) {
        boolean bl = !this.suspectsToAngerLevel.containsKey(entity);
        int i = this.suspectsToAngerLevel.computeInt(entity, (suspect, anger) -> Math.min(150, (anger == null ? 0 : anger) + amount));
        if (bl) {
            int j = this.suspectUuidsToAngerLevel.removeInt(entity.getUuid());
            i += j;
            this.suspectsToAngerLevel.put(entity, i);
            this.suspects.add(entity);
        }

        this.updatePrimeAnger();
        return i;
    }

    public void removeSuspect(Entity entity) {
        this.suspectsToAngerLevel.removeInt(entity);
        this.suspects.remove(entity);
        this.updatePrimeAnger();
    }

    @Nullable
    private Entity getPrimeSuspect1() {
        return this.suspects.stream().filter(this.suspectPredicate).findFirst().orElse(null);
    }

    public int getAngerFor(@Nullable Entity entity) {
        return entity == null ? this.primeAnger : this.suspectsToAngerLevel.getInt(entity);
    }

    public Optional<LivingEntity> getPrimeSuspect() {
        return Optional.ofNullable(this.getPrimeSuspect1()).filter(suspect -> suspect instanceof LivingEntity).map(suspect -> (LivingEntity) suspect);
    }

    @VisibleForTesting
    protected record SuspectComparator(WardenAngerManager angerManagement) implements Comparator<Entity> {
        public int compare(Entity entity, Entity entity2) {
            if (entity.equals(entity2)) {
                return 0;
            } else {
                int i = this.angerManagement.suspectsToAngerLevel.getOrDefault(entity, 0);
                int j = this.angerManagement.suspectsToAngerLevel.getOrDefault(entity2, 0);
                this.angerManagement.primeAnger = Math.max(this.angerManagement.primeAnger, Math.max(i, j));
                boolean bl = Angriness.getForAnger(i).isAngry();
                boolean bl2 = Angriness.getForAnger(j).isAngry();
                if (bl != bl2) {
                    return bl ? -1 : 1;
                } else {
                    boolean bl3 = entity instanceof PlayerEntity;
                    boolean bl4 = entity2 instanceof PlayerEntity;
                    if (bl3 != bl4) {
                        return bl3 ? -1 : 1;
                    } else {
                        return Integer.compare(j, i);
                    }
                }
            }
        }
    }
}
