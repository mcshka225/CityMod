package com.example.citymod.prdc;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import com.example.citymod.network.CitymodModVariables;
import com.google.gson.JsonArray;

import net.minecraft.world.level.LevelAccessor;

import java.io.FileWriter;
import java.io.IOException;

public class PlayerAcceptProcedure {
    public static void execute(LevelAccessor world, String cityName, Entity entity) {
        if (entity == null || !(entity instanceof Player))
            return;

        // Проверяем существование города
        if (!CitymodModVariables.jcity.has(cityName)) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cГород '" + cityName + "' не найден!"), false);
            }
            return;
        }

        // Получаем или создаем список игроков города
        JsonArray playersArray;
        if (CitymodModVariables.jcity.has(cityName + "_Players")) {
            playersArray = CitymodModVariables.jcity.get(cityName + "_Players").getAsJsonArray();
        } else {
            playersArray = new JsonArray();
        }

        String playerName = entity.getDisplayName().getString();

        // Проверяем, не состоит ли игрок уже в городе
        for (int i = 0; i < playersArray.size(); i++) {
            if (playersArray.get(i).getAsString().equals(playerName)) {
                if (entity instanceof Player player && !player.level().isClientSide()) {
                    player.displayClientMessage(Component.literal("§cВы уже состоите в этом городе!"), false);
                }
                return;
            }
        }

        // Добавляем игрока в город
        playersArray.add(playerName);
        CitymodModVariables.jcity.add(cityName + "_Players", playersArray);

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
            player.displayClientMessage(Component.literal("§aВы успешно присоединились к городу '" + cityName + "'!"), false);
        }
    }
}