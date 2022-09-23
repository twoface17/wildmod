package net.frozenblock.wildmod.misc;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.network.ServerPlayerEntity;

public interface WildServerCommandSource {

    ServerPlayerEntity getPlayerOrThrow() throws CommandSyntaxException;
}
