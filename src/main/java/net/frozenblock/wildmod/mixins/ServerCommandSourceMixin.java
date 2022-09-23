package net.frozenblock.wildmod.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.frozenblock.wildmod.misc.WildServerCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerCommandSource.class)
public class ServerCommandSourceMixin implements WildServerCommandSource {
    @Shadow
    @Final
    private @Nullable Entity entity;

    @Shadow
    @Final
    public static SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION;

    @Override
    public ServerPlayerEntity getPlayerOrThrow() throws CommandSyntaxException {
        Entity var2 = this.entity;
        if (var2 instanceof ServerPlayerEntity) {
            return (ServerPlayerEntity) var2;
        } else {
            throw REQUIRES_PLAYER_EXCEPTION.create();
        }
    }
}
