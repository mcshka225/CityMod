package com.example.citymod.prdc;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;

import com.example.citymod.network.CitymodModVariables;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class CmdLevelSetProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;
		if (!world.isClientSide()) {
			BlockPos _bp = BlockPos.containing(new Object() {
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
			}.convert(CitymodModVariables.jcity.get((StringArgumentType.getString(arguments, "name") + "Z")).getAsString()));
			BlockEntity _blockEntity = world.getBlockEntity(_bp);
			BlockState _bs = world.getBlockState(_bp);
			if (_blockEntity != null)
				_blockEntity.getPersistentData().putDouble("CityLevel", (DoubleArgumentType.getDouble(arguments, "level")));
			if (world instanceof Level _level)
				_level.sendBlockUpdated(_bp, _bs, _bs, 3);
		}
		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal(("\u0412\u044B \u0443\u0441\u0442\u0430\u043D\u043E\u0432\u0438\u043B\u0438 \u0443\u0440\u043E\u0432\u0435\u043D\u044C \u0433\u043E\u0440\u043E\u0434\u0430 \u043D\u0430 \u00A76"
					+ DoubleArgumentType.getDouble(arguments, "level"))), false);
	}
}