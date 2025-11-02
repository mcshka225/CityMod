package com.example.citymod.prdc;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.commands.CommandSourceStack;

import com.example.citymod.network.CitymodModVariables;

import com.mojang.brigadier.context.CommandContext;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class CityListProcedure {
    public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments, Entity entity) {
        if (entity == null)
            return;

        // Получаем все ключи из JSON объекта
        Set<String> keys = CitymodModVariables.jcity.keySet();
        List<String> cityNames = new ArrayList<>();

        // Фильтруем только названия городов (исключаем координаты и индексы)
        for (String key : keys) {
            if (!key.endsWith("X") && !key.endsWith("Y") && !key.endsWith("Z") && !key.endsWith("_ind") && !key.startsWith("list") &&
                    !key.endsWith("_Team") && !key.endsWith("_Color")) {
                cityNames.add(key);
            }
        }

        // Отправляем сообщение игроку
        if (entity instanceof Player _player && !_player.level().isClientSide()) {
            if (cityNames.isEmpty()) {
                _player.displayClientMessage(Component.literal("§cСписок городов пуст"), false);
            } else {
                _player.displayClientMessage(Component.literal("§6=== Список городов (§a" + cityNames.size() + "§6) ==="), false);
                for (int i = 0; i < cityNames.size(); i++) {
                    String cityName = cityNames.get(i);

                    // Получаем координаты города
                    String xCoord = CitymodModVariables.jcity.get(cityName + "X").getAsString();
                    String yCoord = CitymodModVariables.jcity.get(cityName + "Y").getAsString();
                    String zCoord = CitymodModVariables.jcity.get(cityName + "Z").getAsString();

                    // Получаем цвет команды
                    String teamColor = "§f"; // белый по умолчанию
                    if (CitymodModVariables.jcity.has(cityName + "_Color")) {
                        teamColor = CitymodModVariables.jcity.get(cityName + "_Color").getAsString();
                    }

                    // Создаем кликабельный текст с координатами
                    String coordinates = "[" + Math.round(Double.parseDouble(xCoord)) + " " + Math.round(Double.parseDouble(yCoord)) + " " + Math.round(Double.parseDouble(zCoord)) + "]";
                    String tpCommand = "/tp " + Math.round(Double.parseDouble(xCoord)) + " " + Math.round(Double.parseDouble(yCoord)) + " " + Math.round(Double.parseDouble(zCoord));

                    Component coordinatesComponent = Component.literal(" §a" + coordinates)
                            .setStyle(Style.EMPTY
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, tpCommand))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§eНажмите чтобы вставить команду телепортации: §a" + tpCommand)))
                            );

                    // Собираем полное сообщение с цветом команды
                    Component fullMessage = Component.literal("§e" + (i + 1) + ". " + teamColor + cityName)
                            .append(coordinatesComponent);

                    _player.displayClientMessage(fullMessage, false);
                }
                _player.displayClientMessage(Component.literal("§6========================="), false);
                _player.displayClientMessage(Component.literal("§7Нажмите на §a[координаты]§7 чтобы вставить команду телепортации"), false);
            }
        }
    }
}