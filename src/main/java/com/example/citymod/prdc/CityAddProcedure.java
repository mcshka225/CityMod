package com.example.citymod.prdc;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.CommandSourceStack;

import com.example.citymod.network.CitymodModVariables;
import com.example.citymod.init.CitymodModBlocks;

import java.io.IOException;
import java.io.FileWriter;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;

public class CityAddProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments) {
		String cityName = StringArgumentType.getString(arguments, "name");
		String teamName = StringArgumentType.getString(arguments, "team"); // Получаем название команды из аргумента
		Entity entity = arguments.getSource().getEntity();

		// Проверяем, существует ли уже город с таким именем
		if (CitymodModVariables.jcity.has(cityName)) {
			if (entity instanceof Player player && !player.level().isClientSide()) {
				player.displayClientMessage(Component.literal("§cГород с названием '" + cityName + "' уже существует!"), false);
				player.displayClientMessage(Component.literal("§7Используйте другое название или удалите существующий город"), false);
			}
			return; // Прерываем выполнение процедуры
		}

		// Проверяем, есть ли координаты с таким именем (защита от частичных дубликатов)
		if (CitymodModVariables.jcity.has(cityName + "X") ||
				CitymodModVariables.jcity.has(cityName + "Y") ||
				CitymodModVariables.jcity.has(cityName + "Z")) {
			if (entity instanceof Player player && !player.level().isClientSide()) {
				player.displayClientMessage(Component.literal("§cОбнаружены конфликтующие данные для города '" + cityName + "'!"), false);
				player.displayClientMessage(Component.literal("§7Возможно, город был некорректно удален. Используйте другое название"), false);
			}
			return;
		}

		// Проверяем, существует ли указанная команда
		if (!(entity instanceof Player player)) {
			return;
		}

		// Получаем scoreboard из мира
		Scoreboard scoreboard;
		if (world instanceof Level) {
			scoreboard = ((Level) world).getScoreboard();
		} else {
			// Если не можем получить scoreboard из LevelAccessor, используем scoreboard игрока
			scoreboard = player.getScoreboard();
		}

		PlayerTeam team = scoreboard.getPlayerTeam(teamName);

		if (team == null) {
			if (!player.level().isClientSide()) {
				player.displayClientMessage(Component.literal("§cКоманда '" + teamName + "' не найдена!"), false);
				player.displayClientMessage(Component.literal("§7Убедитесь, что команда существует или создайте её с помощью /team add " + teamName), false);
			}
			return;
		}

		// Получаем цвет команды
		ChatFormatting teamColorFormatting = team.getColor();
		String teamColor = "§f"; // белый по умолчанию
		if (teamColorFormatting != null) {
			teamColor = getColorCodeFromChatFormatting(teamColorFormatting);
		}

		// Создаем Style для цвета команды
		Style teamStyle = Style.EMPTY.withColor(teamColorFormatting);

		world.setBlock(BlockPos.containing(new Object() {
			public double getX() {
				try {
					return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getX();
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
					return 0;
				}
			}
		}.getX(), new Object() {
			public double getY() {
				try {
					return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getY();
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
					return 0;
				}
			}
		}.getY(), new Object() {
			public double getZ() {
				try {
					return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getZ();
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
					return 0;
				}
			}
		}.getZ()), CitymodModBlocks.CITY_BLOCK.get().defaultBlockState(), 3);

		// Сохраняем данные в BlockEntity
		if (!world.isClientSide()) {
			BlockPos _bp = BlockPos.containing(new Object() {
				public double getX() {
					try {
						return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getX();
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
						return 0;
					}
				}
			}.getX(), new Object() {
				public double getY() {
					try {
						return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getY();
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
						return 0;
					}
				}
			}.getY(), new Object() {
				public double getZ() {
					try {
						return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getZ();
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
						return 0;
					}
				}
			}.getZ());
			BlockEntity _blockEntity = world.getBlockEntity(_bp);
			BlockState _bs = world.getBlockState(_bp);
			if (_blockEntity != null) {
				_blockEntity.getPersistentData().putString("CityName", cityName);
				_blockEntity.getPersistentData().putString("TeamName", teamName);
				_blockEntity.getPersistentData().putString("TeamColor", teamColor);
			}
			if (world instanceof Level _level)
				_level.sendBlockUpdated(_bp, _bs, _bs, 3);
		}

		if (!world.isClientSide()) {
			BlockPos _bp = BlockPos.containing(new Object() {
				public double getX() {
					try {
						return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getX();
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
						return 0;
					}
				}
			}.getX(), new Object() {
				public double getY() {
					try {
						return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getY();
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
						return 0;
					}
				}
			}.getY(), new Object() {
				public double getZ() {
					try {
						return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getZ();
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
						return 0;
					}
				}
			}.getZ());
			BlockEntity _blockEntity = world.getBlockEntity(_bp);
			BlockState _bs = world.getBlockState(_bp);
			if (_blockEntity != null)
				_blockEntity.getPersistentData().putDouble("CityLevel", 0);
			if (world instanceof Level _level)
				_level.sendBlockUpdated(_bp, _bs, _bs, 3);
		}

		if (!world.isClientSide()) {
			BlockPos _bp = BlockPos.containing(new Object() {
				public double getX() {
					try {
						return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getX();
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
						return 0;
					}
				}
			}.getX(), new Object() {
				public double getY() {
					try {
						return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getY();
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
						return 0;
					}
				}
			}.getY(), new Object() {
				public double getZ() {
					try {
						return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getZ();
					} catch (CommandSyntaxException e) {
						e.printStackTrace();
						return 0;
					}
				}
			}.getZ());
			BlockEntity _blockEntity = world.getBlockEntity(_bp);
			BlockState _bs = world.getBlockState(_bp);
			if (_blockEntity != null)
				_blockEntity.getPersistentData().putDouble("CityProgress", 0);
			if (world instanceof Level _level)
				_level.sendBlockUpdated(_bp, _bs, _bs, 3);
		}

		// Сохраняем координаты и информацию о команде в JSON
		CitymodModVariables.jcity.addProperty((cityName + "X"), (new Object() {
			public double getX() {
				try {
					return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getX();
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
					return 0;
				}
			}
		}.getX()));
		CitymodModVariables.jcity.addProperty((cityName + "Y"), (new Object() {
			public double getY() {
				try {
					return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getY();
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
					return 0;
				}
			}
		}.getY()));
		CitymodModVariables.jcity.addProperty((cityName + "Z"), (new Object() {
			public double getZ() {
				try {
					return BlockPosArgument.getLoadedBlockPos(arguments, "pos").getZ();
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
					return 0;
				}
			}
		}.getZ()));
		CitymodModVariables.jcity.addProperty((cityName + "_Team"), teamName);
		CitymodModVariables.jcity.addProperty((cityName + "_Color"), teamColor);

		// Сохраняем имя города в список
		CitymodModVariables.jcity.addProperty(cityName, cityName);

		// Обновляем файл
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

		// Сообщение об успешном создании
		if (!player.level().isClientSide()) {
			Component coloredCityName = Component.literal(cityName).setStyle(teamStyle);
			player.displayClientMessage(Component.literal("§aГород ").append(coloredCityName).append(Component.literal(" §aуспешно создан!")), false);
			player.displayClientMessage(Component.literal("§7Команда: " + teamName), false);
			player.displayClientMessage(Component.literal("§7Используйте команду: §e/city info " + cityName + " §7для просмотра информации"), false);
		}
	}

	// Вспомогательный метод для конвертации ChatFormatting в коды форматирования
	private static String getColorCodeFromChatFormatting(ChatFormatting formatting) {
		switch (formatting) {
			case BLACK: return "§0";
			case DARK_BLUE: return "§1";
			case DARK_GREEN: return "§2";
			case DARK_AQUA: return "§3";
			case DARK_RED: return "§4";
			case DARK_PURPLE: return "§5";
			case GOLD: return "§6";
			case GRAY: return "§7";
			case DARK_GRAY: return "§8";
			case BLUE: return "§9";
			case GREEN: return "§a";
			case AQUA: return "§b";
			case RED: return "§c";
			case LIGHT_PURPLE: return "§d";
			case YELLOW: return "§e";
			case WHITE: return "§f";
			default: return "§f"; // белый по умолчанию
		}
	}
}