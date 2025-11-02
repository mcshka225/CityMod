package com.example.citymod.prdc;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;

import com.example.citymod.network.CitymodModVariables;
import com.example.citymod.client.gui.CityInfoScreen;

public class OnClickBlockProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;

        String cityName = new Object() {
            public String getValue(LevelAccessor world, BlockPos pos, String tag) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null)
                    return blockEntity.getPersistentData().getString(tag);
                return "";
            }
        }.getValue(world, BlockPos.containing(x, y, z), "CityName");

        if (cityName.isEmpty()) return;

        // Получаем данные о городе
        double cityLevel = new Object() {
            public double getValue(LevelAccessor world, BlockPos pos, String tag) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null)
                    return blockEntity.getPersistentData().getDouble(tag);
                return -1;
            }
        }.getValue(world, BlockPos.containing(x, y, z), "CityLevel");

        double cityProgress = new Object() {
            public double getValue(LevelAccessor world, BlockPos pos, String tag) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null)
                    return blockEntity.getPersistentData().getDouble(tag);
                return -1;
            }
        }.getValue(world, BlockPos.containing(x, y, z), "CityProgress");

        // Получаем цвет команды
        String teamColor = new Object() {
            public String getValue(LevelAccessor world, BlockPos pos, String tag) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity != null)
                    return blockEntity.getPersistentData().getString(tag);
                return "§f"; // белый по умолчанию
            }
        }.getValue(world, BlockPos.containing(x, y, z), "TeamColor");

        // Открываем GUI для игрока
        if (entity instanceof Player player) {
            if (player.level().isClientSide()) {
                openCityInfoScreen(cityName, (int) Math.round(cityLevel), (int) Math.round(cityProgress), teamColor);
            }
        }
    }

    private static void openCityInfoScreen(String cityName, int cityLevel, int cityProgress, String teamColor) {
        // Открываем GUI на клиентской стороне
        Minecraft.getInstance().setScreen(new CityInfoScreen(cityName, cityLevel, cityProgress, teamColor));
    }
}