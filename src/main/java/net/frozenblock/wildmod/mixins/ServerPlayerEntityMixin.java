package net.frozenblock.wildmod.mixins;

import net.frozenblock.wildmod.liukrastapi.WildPlayerEntity;
import net.frozenblock.wildmod.liukrastapi.WildServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements WildServerPlayerEntity {
}
