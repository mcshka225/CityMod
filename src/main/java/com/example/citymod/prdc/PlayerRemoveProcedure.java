package com.example.citymod.prdc;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import com.example.citymod.network.CitymodModVariables;
import com.google.gson.JsonArray;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.world.level.LevelAccessor;

import java.io.FileWriter;
import java.io.IOException;

public class PlayerRemoveProcedure {
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

        // Проверяем права управленца
        String managerName = "";
        if (CitymodModVariables.jcity.has(cityName + "_Manager")) {
            managerName = CitymodModVariables.jcity.get(cityName + "_Manager").getAsString();
        }

        if (!managerName.equals(entity.getName().getString())) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cУ вас нет прав для удаления игроков из этого города!"), false);
            }
            return;
        }

        // Проверяем, что управленец не удаляет сам себя
        if (playerName.equals(managerName)) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cВы не можете удалить себя из города! Используйте команду /city manager для передачи прав."), false);
            }
            return;
        }

        // Получаем список игроков города
        if (!CitymodModVariables.jcity.has(cityName + "_Players")) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cВ городе нет игроков!"), false);
            }
            return;
        }

        JsonArray playersArray = CitymodModVariables.jcity.get(cityName + "_Players").getAsJsonArray();
        boolean playerFound = false;

        // Удаляем игрока из списка
        for (int i = 0; i < playersArray.size(); i++) {
            if (playersArray.get(i).getAsString().equals(playerName)) {
                playersArray.remove(i);
                playerFound = true;
                break;
            }
        }

        if (!playerFound) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cИгрок '" + playerName + "' не найден в городе '" + cityName + "'!"), false);
            }
            return;
        }

        // Обновляем список игроков
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
            player.displayClientMessage(Component.literal("§aИгрок '" + playerName + "' удален из города '" + cityName + "'"), false);
        }
    }
}