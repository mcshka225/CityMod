package com.example.citymod.prdc;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import com.example.citymod.network.CitymodModVariables;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.world.level.LevelAccessor;

import java.io.FileWriter;
import java.io.IOException;

public class PlayerManagerProcedure {
    public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments, Entity entity) {
        if (entity == null)
            return;

        String cityName = StringArgumentType.getString(arguments, "city");
        String playerName = StringArgumentType.getString(arguments, "player");

        // Проверяем существование города
        if (!CitymodModVariables.jcity.has(cityName)) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cГород '" + cityName + "' не найден!"), false);
            }
            return;
        }

        // Добавляем или обновляем управленца города
        CitymodModVariables.jcity.addProperty(cityName + "_Manager", playerName);

        // Добавляем управленца в список игроков города, если его там нет
        JsonArray playersArray;
        if (CitymodModVariables.jcity.has(cityName + "_Players")) {
            playersArray = CitymodModVariables.jcity.get(cityName + "_Players").getAsJsonArray();
        } else {
            playersArray = new JsonArray();
        }

        boolean managerAlreadyInList = false;
        for (int i = 0; i < playersArray.size(); i++) {
            if (playersArray.get(i).getAsString().equals(playerName)) {
                managerAlreadyInList = true;
                break;
            }
        }

        if (!managerAlreadyInList) {
            playersArray.add(playerName);
            CitymodModVariables.jcity.add(cityName + "_Players", playersArray);
        }

        // Сохраняем в файл
        {
            com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            try {
                FileWriter fileWriter = new FileWriter(CitymodModVariables.city);
                fileWriter.write(mainGSONBuilderVariable.toJson(CitymodModVariables.jcity));
                fileWriter.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(Component.literal("§aИгрок '" + playerName + "' назначен управленцем города '" + cityName + "'"), false);
        }
    }
}