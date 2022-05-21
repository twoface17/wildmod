package net.frozenblock.wildmod.registry;

import net.frozenblock.wildmod.liukrastapi.CompassAnglePredicateProvider;
import net.frozenblock.wildmod.liukrastapi.WildPlayerEntity;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class RegisterRecoveryCompass {

    public static void registerRecovery() {
        /*ModelPredicateProviderRegistry.register(RegisterItems.RECOVERY_COMPASS, new Identifier("angle"), new CompassAnglePredicateProvider(
                (world, stack, entity) -> entity instanceof PlayerEntity playerEntity ? ((WildPlayerEntity)playerEntity).getLastDeathPos().orElse(null) : null
        ));*/
    }
}
