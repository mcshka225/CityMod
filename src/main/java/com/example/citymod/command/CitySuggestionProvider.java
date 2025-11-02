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

public class CitySuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    public static final CitySuggestionProvider INSTANCE = new CitySuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        // Проходим по всем элементам jcity
        for (Map.Entry<String, JsonElement> entry : CitymodModVariables.jcity.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            // Фильтруем только города:
            // 2. Значение является JSON объектом (а не примитивом)
            // 3. В объекте есть поле "name" (основные поля города)
            if (!key.contains("_") && value.isJsonObject()) {
                JsonObject cityData = value.getAsJsonObject();
                if (cityData.has("name") && cityData.has("team")) {
                    builder.suggest(key);
                }
            }
        }
        return builder.buildFuture();
    }
}