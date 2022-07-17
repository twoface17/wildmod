package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.liukrastapi.WildPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements WildPlayerEntity {
    @Override
    public Optional<GlobalPos> getLastDeathPos() {
        return Optional.empty();//((WildPlayerEntity)PlayerEntity).getLastDeathPos();
    }

    @Override
    public void setLastDeathPos(Optional<GlobalPos> lastDeathPos) {

    }
}
