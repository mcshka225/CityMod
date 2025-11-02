// BuildingDisplayProcedure.java
package com.example.citymod.prdc;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import com.example.citymod.network.CitymodModVariables;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import net.minecraft.world.level.LevelAccessor;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class BuildingDisplayProcedure {
    public static void execute(LevelAccessor world, String cityName, Entity entity) {
        if (entity == null)
            return;

        // Проверяем существование города
        if (!CitymodModVariables.jcity.has(cityName)) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cГород '" + cityName + "' не найден!"), false);
            }
            return;
        }

        if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(Component.literal("§8================="), false);
            player.displayClientMessage(Component.literal("§6Список построек города '" + cityName + "':"), false);

            int buildingCount = 0;
            double totalRating = 0;
            List<String> buildingList = new ArrayList<>();

            // Ищем все постройки этого города
            for (Map.Entry<String, JsonElement> entry : CitymodModVariables.jcity.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(cityName + "_") && entry.getValue().isJsonObject()) {
                    JsonObject buildingData = entry.getValue().getAsJsonObject();
                    String buildingName = buildingData.get("name").getAsString();
                    double rating = buildingData.get("rating").getAsDouble();

                    buildingList.add("§e- " + buildingName + " §7(Оценка: §6" + rating + "§7)");
                    buildingCount++;
                    totalRating += rating;
                }
            }

            if (buildingCount == 0) {
                player.displayClientMessage(Component.literal("§7В этом городе пока нет построек"), false);
            } else {
                // Выводим каждую постройку на новой строке
                for (String buildingInfo : buildingList) {
                    player.displayClientMessage(Component.literal(buildingInfo), false);
                }

                double averageRating = totalRating / buildingCount;
                player.displayClientMessage(Component.literal("§7Всего построек: §a" + buildingCount + " §7| Средняя оценка: §6" + String.format("%.1f", averageRating)), false);
            }

            player.displayClientMessage(Component.literal("§8================="), false);
        }
    }
}