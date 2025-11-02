// BuildingAddProcedure.java
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

public class BuildingAddProcedure {
    public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments, Entity entity) {
        if (entity == null)
            return;

        String cityName = StringArgumentType.getString(arguments, "city");
        String buildingName = StringArgumentType.getString(arguments, "building");
        double rating = DoubleArgumentType.getDouble(arguments, "rating");

        // Проверяем существование города
        if (!CitymodModVariables.jcity.has(cityName)) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cГород '" + cityName + "' не найден!"), false);
            }
            return;
        }

        // Проверяем, существует ли уже постройка с таким именем
        String buildingKey = cityName + "_" + buildingName;
        if (CitymodModVariables.jcity.has(buildingKey)) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cПостройка '" + buildingName + "' уже существует в городе '" + cityName + "'!"), false);
                player.displayClientMessage(Component.literal("§7Используйте другое название или команду /city build reset"), false);
            }
            return;
        }

        // Добавляем постройку
        JsonObject buildingData = new JsonObject();
        buildingData.addProperty("name", buildingName);
        buildingData.addProperty("rating", rating);

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
            player.displayClientMessage(Component.literal("§aПостройка '" + buildingName + "' добавлена в город '" + cityName + "' с оценкой " + rating), false);
        }
    }
}