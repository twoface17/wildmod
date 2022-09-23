package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.entity.util.LargeEntitySpawnHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.village.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements InteractionObserver, VillagerDataContainer {

    @Shadow
    @Final
    private VillagerGossips gossip;

    @Shadow
    protected abstract boolean hasRecentlySlept(long worldTime);

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public boolean canSummonGolem(long time) {
        if (!this.hasRecentlySlept(this.world.getTime())) {
            return false;
        } else {
            return !this.brain.hasMemoryModule(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
        }
    }

    /**
     * @author FrozenBlock
     * @reason use 1.19 iron golem spawning
     */
    @Overwrite
    public void summonGolem(ServerWorld world, long time, int requiredCount) {
        if (this.canSummonGolem(time)) {
            Box box = this.getBoundingBox().expand(10.0, 10.0, 10.0);
            List<VillagerEntity> list = world.getNonSpectatingEntities(VillagerEntity.class, box);
            List<VillagerEntity> list2 = list.stream().filter(villager -> villager.canSummonGolem(time)).limit(5L).toList();
            if (list2.size() >= requiredCount) {
                if (LargeEntitySpawnHelper.trySpawnAt(
                                EntityType.IRON_GOLEM, SpawnReason.MOB_SUMMONED, world, this.getBlockPos(), 10, 8, 6, LargeEntitySpawnHelper.Requirements.IRON_GOLEM
                        )
                        .isPresent()) {
                    list.forEach(GolemLastSeenSensor::rememberIronGolem);
                }
            }
        }
    }

    @Shadow
    public void onInteractionWith(EntityInteraction interaction, Entity entity) {
        if (interaction == EntityInteraction.ZOMBIE_VILLAGER_CURED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_POSITIVE, 20);
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_POSITIVE, 25);
        } else if (interaction == EntityInteraction.TRADE) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.TRADING, 2);
        } else if (interaction == EntityInteraction.VILLAGER_HURT) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_NEGATIVE, 25);
        } else if (interaction == EntityInteraction.VILLAGER_KILLED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_NEGATIVE, 25);
        }

    }

    @Shadow
    protected abstract void afterUsing(TradeOffer offer);

    @Shadow
    protected abstract void fillRecipes();

    @Shadow
    public abstract VillagerEntity createChild(ServerWorld world, PassiveEntity entity);

    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    public abstract void setVillagerData(VillagerData villagerData);
}
