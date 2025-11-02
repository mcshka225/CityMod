package com.example.citymod.prdc;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;

import com.example.citymod.network.CitymodModVariables;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.io.FileWriter;
import java.io.IOException;

public class CityRemoveProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments) {
		world.setBlock(BlockPos.containing(new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(CitymodModVariables.jcity.get((StringArgumentType.getString(arguments, "name") + "X")).getAsString()), new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(CitymodModVariables.jcity.get((StringArgumentType.getString(arguments, "name") + "Y")).getAsString()), new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(CitymodModVariables.jcity.get((StringArgumentType.getString(arguments, "name") + "Z")).getAsString())), Blocks.AIR.defaultBlockState(), 3);
		CitymodModVariables.jcity.remove((StringArgumentType.getString(arguments, "name") + "X"));
		CitymodModVariables.jcity.remove((StringArgumentType.getString(arguments, "name") + "Y"));
		CitymodModVariables.jcity.remove((StringArgumentType.getString(arguments, "name") + "Z"));
		CitymodModVariables.jcity.remove(("list" + CitymodModVariables.jcity.get((StringArgumentType.getString(arguments, "name") + "_ind")).getAsString()));
		CitymodModVariables.jcity.remove((StringArgumentType.getString(arguments, "name") + "_ind"));
		// Удаляем имя города из списка
		CitymodModVariables.jcity.remove(StringArgumentType.getString(arguments, "name"));

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
	}
}