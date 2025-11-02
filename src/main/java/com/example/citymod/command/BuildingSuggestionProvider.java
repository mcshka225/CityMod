package com.example.citymod.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import com.example.citymod.network.CitymodModVariables;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.util.concurrent.CompletableFuture;
import java.util.Map;

public class BuildingSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    public static final BuildingSuggestionProvider INSTANCE = new BuildingSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        try {
            // Получаем название города из контекста
            String cityName = context.getArgument("city", String.class);
            String cityPrefix = cityName + "_";

            // Проходим по всем элементам jcity
            for (Map.Entry<String, JsonElement> entry : CitymodModVariables.jcity.entrySet()) {
                String key = entry.getKey();

                // Фильтруем постройки для данного города
                if (key.startsWith(cityPrefix) && entry.getValue().isJsonObject()) {
                    JsonObject buildingData = entry.getValue().getAsJsonObject();
                    String buildingName = buildingData.get("name").getAsString();
                    builder.suggest(buildingName);
                }
            }
        } catch (IllegalArgumentException e) {
            // Город не выбран, ничего не предлагаем
        }
        return builder.buildFuture();
    }
}