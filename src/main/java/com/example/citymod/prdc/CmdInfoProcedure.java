package com.example.citymod.prdc;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;

import com.example.citymod.network.CitymodModVariables;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;

public class CmdInfoProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;

		String cityName = StringArgumentType.getString(arguments, "name");

		// Получаем координаты из JSON
		double x = new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(CitymodModVariables.jcity.get(cityName + "X").getAsString());

		double y = new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(CitymodModVariables.jcity.get(cityName + "Y").getAsString());

		double z = new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(CitymodModVariables.jcity.get(cityName + "Z").getAsString());

		BlockPos cityPos = BlockPos.containing(x, y, z);

		// Получаем цвет команды из BlockEntity
		String teamColor = new Object() {
			public String getValue(LevelAccessor world, BlockPos pos, String tag) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null)
					return blockEntity.getPersistentData().getString(tag);
				return "§f"; // белый по умолчанию
			}
		}.getValue(world, cityPos, "TeamColor");

		// Получаем название команды
		String teamName = new Object() {
			public String getValue(LevelAccessor world, BlockPos pos, String tag) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null)
					return blockEntity.getPersistentData().getString(tag);
				return "";
			}
		}.getValue(world, cityPos, "TeamName");

		// Создаем кликабельные координаты
		String coordinates = Math.round(x) + " " + Math.round(y) + " " + Math.round(z);
		String tpCommand = "/tp " + coordinates;

		Component coordinatesComponent = Component.literal("§a" + coordinates)
				.setStyle(Style.EMPTY
						.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, tpCommand))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§eНажмите чтобы вставить команду телепортации: §a" + tpCommand)))
				);

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal("§8================="), false);

		// Название города с цветом команды
		String coloredCityName = teamColor + cityName;
		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal(("Название: " + coloredCityName)), false);

		// Название команды
		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal(("Команда: §7" + teamName)), false);

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal(("Уровень: §6" + Math.round(new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, cityPos, "CityLevel")))), false);

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal(("Прогресс: §3" + Math.round(new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, cityPos, "CityProgress")) + "%")), false);

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal("Координаты: ").append(coordinatesComponent), false);

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal("§8================="), false);

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal("§7Нажмите на §aкоординаты§7 чтобы вставить команду телепортации"), false);

		Component buildingsComponent = Component.literal("§aПосмотреть список построек")
				.setStyle(Style.EMPTY
						.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/city build list " + cityName))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§eНажмите чтобы посмотреть список построек этого города")))
				);
		BuildingDisplayProcedure.execute(world, cityName, entity);
	}
}