// TeamSuggestionProvider.java
package com.example.citymod.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.level.Level;

import java.util.concurrent.CompletableFuture;

public class TeamSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        Level world = context.getSource().getUnsidedLevel();
        Scoreboard scoreboard = world.getScoreboard();

        // Получаем все команды из scoreboard
        for (PlayerTeam team : scoreboard.getPlayerTeams()) {
            builder.suggest(team.getName());
        }

        return builder.buildFuture();
    }
}