package com.example.citymod.prdc;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import com.example.citymod.network.CitymodModVariables;

import net.minecraft.world.level.LevelAccessor;

import java.util.List;

public class PlayerInviteProcedure {
    public static void execute(LevelAccessor world, String cityName, String targetPlayerName, Entity entity) {
        if (entity == null)
            return;

        // Проверяем права управленца
        if (!CitymodModVariables.jcity.has(cityName + "_Manager") ||
                !CitymodModVariables.jcity.get(cityName + "_Manager").getAsString().equals(entity.getName().getString())) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cУ вас нет прав для приглашения игроков в этот город!"), false);
            }
            return;
        }

        // Ищем целевого игрока
        Player targetPlayer = null;
        if (world instanceof ServerLevel serverLevel) {
            List<ServerPlayer> players = serverLevel.getServer().getPlayerList().getPlayers();
            for (ServerPlayer player : players) {
                if (player.getName().getString().equals(targetPlayerName)) {
                    targetPlayer = player;
                    break;
                }
            }
        }

        if (targetPlayer == null) {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("§cИгрок '" + targetPlayerName + "' не найден или не в сети!"), false);
            }
            return;
        }

        // Отправляем приглашение целевому игроку
        Component acceptButton = Component.literal("§a[Принять]")
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/city accept " + cityName))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.literal("§eНажмите чтобы присоединиться к городу " + cityName)))
                );

        Component message = Component.literal("§6Вы получили приглашение присоединиться к городу '")
                .append(Component.literal("§b" + cityName + "§6' от "))
                .append(Component.literal("§a" + entity.getName().getString()))
                .append(Component.literal("§6. "))
                .append(acceptButton);

        if (targetPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(message, false);
        }

        if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(Component.literal("§aПриглашение отправлено игроку '" + targetPlayerName + "'"), false);
        }
    }
}