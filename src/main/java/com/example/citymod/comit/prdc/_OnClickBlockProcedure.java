// OnClickBlockProcedure.java - заменяем выполнение команды на прямой вывод
package com.example.citymod.comit.prdc;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import com.example.citymod.network.CitymodModVariables;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.util.Map;

public class _OnClickBlockProcedure {
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

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal("§8================="), false);

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal(("Название: §9" + cityName)), false);

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal(("Уровень: §6" + Math.round(new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "CityLevel")))), false);

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal(("Прогресс: §3" + Math.round(new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "CityProgress")) + "%")), false);

		// Показываем список построек напрямую
		displayBuildingsList(world, cityName, entity);

		if (entity instanceof Player _player && !_player.level().isClientSide()) {
			_player.displayClientMessage(Component.literal("§8================="), false);
		}
	}

	private static void displayBuildingsList(LevelAccessor world, String cityName, Entity entity) {
		if (entity == null)
			return;

		// Проверяем существование города
		if (!CitymodModVariables.jcity.has(cityName)) {
			return;
		}

		if (entity instanceof Player player && !player.level().isClientSide()) {
			player.displayClientMessage(Component.literal("§6Постройки:"), false);

			int buildingCount = 0;
			double totalRating = 0;

			// Ищем все постройки этого города
			for (Map.Entry<String, JsonElement> entry : CitymodModVariables.jcity.entrySet()) {
				String key = entry.getKey();
				if (key.startsWith(cityName + "_") && entry.getValue().isJsonObject()) {
					JsonObject buildingData = entry.getValue().getAsJsonObject();
					String buildingName = buildingData.get("name").getAsString();
					double rating = buildingData.get("rating").getAsDouble();

					// Выводим каждую постройку на новой строке
					player.displayClientMessage(Component.literal("§e- " + buildingName + " §7(Оценка: §6" + rating + "§7)"), false);
					buildingCount++;
					totalRating += rating;
				}
			}

			if (buildingCount == 0) {
				player.displayClientMessage(Component.literal("§7В этом городе пока нет построек"), false);
			} else {
				double averageRating = totalRating / buildingCount;
				player.displayClientMessage(Component.literal("§7Всего: §a" + buildingCount + " §7| Средняя: §6" + String.format("%.1f", averageRating)), false);
			}
		}
	}
}