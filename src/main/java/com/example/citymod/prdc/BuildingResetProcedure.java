// BuildingResetProcedure.java
package com.example.citymod.prdc;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import com.example.citymod.network.CitymodModVariables;
import com.google.gson.JsonObject;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.world.level.LevelAccessor;

import java.io.IOException;
import java.io.FileWriter;

public class BuildingResetProcedure {
    public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments, Entity entity) {
        if (entity == null)
            return;

        String cityName = StringArgumentType.getString(arguments, "city");
        String buildingName = StringArgumentType.getString(arguments, "building");
        double newRating = DoubleArgumentType.getDouble(arguments, "rating");
        String buildingKey = cityName + "_" + buildingName;

        // Проверяем существование постройки
        if (!CitymodModVariables.jcity.has(buildingKey)) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cПостройка '" + buildingName + "' не найдена в городе '" + cityName + "'!"), false);
                player.displayClientMessage(Component.literal("§7Используйте команду /city build add для добавления"), false);
            }
            return;
        }

        // Обновляем оценку
        JsonObject buildingData = CitymodModVariables.jcity.get(buildingKey).getAsJsonObject();
        double oldRating = buildingData.get("rating").getAsDouble();
        buildingData.addProperty("rating", newRating);
        CitymodModVariables.jcity.add(buildingKey, buildingData);

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
            player.displayClientMessage(Component.literal("§aОценка постройки '" + buildingName + "' изменена с " + oldRating + " на " + newRating), false);
        }
    }
}