package net.frozenblock.wildmod.misc;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Collection;

public class WardenSpawnTrackerCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            dispatcher.register(
                    CommandManager.literal("warden_spawn_tracker")
                            .requires(source -> source.hasPermissionLevel(2))
                            .then(CommandManager.literal("clear").executes(context -> clearTracker(context.getSource(), ImmutableList.of(((WildServerCommandSource) context.getSource()).getPlayerOrThrow()))))
                            .then(
                                    CommandManager.literal("set")
                                            .then(
                                                    CommandManager.argument("warning_level", IntegerArgumentType.integer(0, 4))
                                                            .executes(
                                                                    context -> setWarningLevel(
                                                                            context.getSource(), ImmutableList.of(((WildServerCommandSource) context.getSource()).getPlayerOrThrow()), IntegerArgumentType.getInteger(context, "warning_level")
                                                                    )
                                                            )
                                            )
                            )
            );
        }));

    }

    private static int setWarningLevel(ServerCommandSource source, Collection<? extends PlayerEntity> players, int warningCount) {
        for(PlayerEntity playerEntity : players) {
            ((WildPlayerEntity) playerEntity).getSculkShriekerWarningManager().setWarningLevel(warningCount);
        }

        if (players.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.warden_spawn_tracker.set.success.single", players.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.warden_spawn_tracker.set.success.multiple", players.size()), true);
        }

        return players.size();
    }

    private static int clearTracker(ServerCommandSource source, Collection<? extends PlayerEntity> players) {
        for(PlayerEntity playerEntity : players) {
            ((WildPlayerEntity) playerEntity).getSculkShriekerWarningManager().reset();
        }

        if (players.size() == 1) {
            source.sendFeedback(
                    new TranslatableText("commands.warden_spawn_tracker.clear.success.single", ((PlayerEntity)players.iterator().next()).getDisplayName()), true
            );
        } else {
            source.sendFeedback(new TranslatableText("commands.warden_spawn_tracker.clear.success.multiple", players.size()), true);
        }

        return players.size();
    }
}
