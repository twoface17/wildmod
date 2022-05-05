/*package net.frozenblock.wildmod.items;

import com.google.common.collect.Maps;
import net.frozenblock.wildmod.registry.RegisterItems;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.GlobalPos;

import java.util.Map;
import java.util.Optional;

public class ModelPredicateProviderRegistry extends net.minecraft.client.item.ModelPredicateProviderRegistry {
    private static final Map<Item, Map<Identifier, ModelPredicateProvider>> ITEM_SPECIFIC = Maps.newHashMap();


    public static void register(Item item, Identifier id, UnclampedModelPredicateProvider provider) {
        (ITEM_SPECIFIC.computeIfAbsent(item, key -> Maps.newHashMap())).put(id, provider);
    }

    static {
        register(
            RegisterItems.RECOVERY_COMPASS,
                new Identifier("angle"),
                new CompassAnglePredicateProvider(
                    (world, stack, entity) -> entity instanceof PlayerEntity playerEntity ? (GlobalPos)playerEntity.getLastDeathPos().orElse(null) : null
                )
        );
    }

}
*/