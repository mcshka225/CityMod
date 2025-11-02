package com.example.citymod.command;

import com.example.citymod.prdc.*;
import com.example.citymod.network.CitymodModVariables;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.Commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;

@Mod.EventBusSubscriber
public class CityCommand {

	// Создаем провайдер для автодополнения команд
	private static final TeamSuggestionProvider TEAM_SUGGESTIONS = new TeamSuggestionProvider();

	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		// Регистрируем основную команду /city - все подкоманды требуют прав уровня 4
		event.getDispatcher().register(
				Commands.literal("city").requires(s -> s.hasPermission(4))
						.then(Commands.literal("add")
								.then(Commands.argument("name", StringArgumentType.word())
										.then(Commands.argument("team", StringArgumentType.word())
												.suggests(TEAM_SUGGESTIONS)
												.then(Commands.argument("pos", BlockPosArgument.blockPos())
														.executes(arguments -> {
															Level world = arguments.getSource().getUnsidedLevel();
															Entity entity = arguments.getSource().getEntity();
															if (entity == null && world instanceof ServerLevel _servLevel)
																entity = FakePlayerFactory.getMinecraft(_servLevel);

															CityAddProcedure.execute(world, arguments);
															return 0;
														})))))
						.then(Commands.literal("remove")
								.then(Commands.argument("name", StringArgumentType.word())
										.executes(arguments -> {
											Level world = arguments.getSource().getUnsidedLevel();
											Entity entity = arguments.getSource().getEntity();
											if (entity == null && world instanceof ServerLevel _servLevel)
												entity = FakePlayerFactory.getMinecraft(_servLevel);

											CityRemoveProcedure.execute(world, arguments);
											return 0;
										})))
						.then(Commands.literal("level")
								.then(Commands.literal("set")
										.then(Commands.argument("name", StringArgumentType.word())
												.then(Commands.argument("level", DoubleArgumentType.doubleArg(0, 7))
														.executes(arguments -> {
															Level world = arguments.getSource().getUnsidedLevel();
															Entity entity = arguments.getSource().getEntity();
															if (entity == null && world instanceof ServerLevel _servLevel)
																entity = FakePlayerFactory.getMinecraft(_servLevel);

															CmdLevelSetProcedure.execute(world, arguments, entity);
															return 0;
														}))))
								.then(Commands.literal("progress")
										.then(Commands.argument("name", StringArgumentType.word())
												.then(Commands.argument("progress", DoubleArgumentType.doubleArg(0, 100))
														.executes(arguments -> {
															Level world = arguments.getSource().getUnsidedLevel();
															Entity entity = arguments.getSource().getEntity();
															if (entity == null && world instanceof ServerLevel _servLevel)
																entity = FakePlayerFactory.getMinecraft(_servLevel);

															CmdLevelProgressProcedure.execute(world, arguments, entity);
															return 0;
														}))))
						) // Закрываем level
						.then(Commands.literal("info")
								.then(Commands.argument("name", StringArgumentType.word())
										.suggests(CitySuggestionProvider.INSTANCE)
										.executes(arguments -> {
											Level world = arguments.getSource().getUnsidedLevel();
											Entity entity = arguments.getSource().getEntity();
											if (entity == null && world instanceof ServerLevel _servLevel)
												entity = FakePlayerFactory.getMinecraft(_servLevel);

											CmdInfoProcedure.execute(world, arguments, entity);
											return 0;
										}))
						)
						.then(Commands.literal("list")
								.executes(arguments -> {
									Level world = arguments.getSource().getUnsidedLevel();
									Entity entity = arguments.getSource().getEntity();
									if (entity == null && world instanceof ServerLevel _servLevel)
										entity = FakePlayerFactory.getMinecraft(_servLevel);

									CityListProcedure.execute(world, arguments, entity);
									return 0;
								})
						)
						.then(Commands.literal("manager")
								.then(Commands.argument("city", StringArgumentType.word())
										.suggests(CitySuggestionProvider.INSTANCE)
										.then(Commands.argument("player", StringArgumentType.string())
												.executes(arguments -> {
													Level world = arguments.getSource().getUnsidedLevel();
													Entity entity = arguments.getSource().getEntity();
													if (entity == null && world instanceof ServerLevel _servLevel)
														entity = FakePlayerFactory.getMinecraft(_servLevel);

													PlayerManagerProcedure.execute(world, arguments, entity);
													return 0;
												}))
								)
						)
						.then(Commands.literal("accept")
								.then(Commands.argument("city", StringArgumentType.word())
										.suggests(CitySuggestionProvider.INSTANCE)
										.executes(arguments -> {
											Level world = arguments.getSource().getUnsidedLevel();
											Entity entity = arguments.getSource().getEntity();
											if (entity == null && world instanceof ServerLevel _servLevel)
												entity = FakePlayerFactory.getMinecraft(_servLevel);

											String cityName = StringArgumentType.getString(arguments, "city");
											PlayerAcceptProcedure.execute(world, cityName, entity);
											return 0;
										})
								)
						)
						.then(Commands.literal("removeplayer")
								.then(Commands.argument("city", StringArgumentType.word())
										.suggests(CitySuggestionProvider.INSTANCE)
										.then(Commands.argument("player", StringArgumentType.string())
												.executes(arguments -> {
													Level world = arguments.getSource().getUnsidedLevel();
													Entity entity = arguments.getSource().getEntity();
													if (entity == null && world instanceof ServerLevel _servLevel)
														entity = FakePlayerFactory.getMinecraft(_servLevel);

													PlayerRemoveProcedure.execute(world, arguments, entity);
													return 0;
												}))
								)
						)
						.then(Commands.literal("build")
								.then(Commands.literal("add")
										.then(Commands.argument("city", StringArgumentType.word())
												.suggests(CitySuggestionProvider.INSTANCE)
												.then(Commands.argument("building", StringArgumentType.string())
														.then(Commands.argument("rating", DoubleArgumentType.doubleArg(1, 10))
																.executes(arguments -> {
																	Level world = arguments.getSource().getUnsidedLevel();
																	Entity entity = arguments.getSource().getEntity();
																	if (entity == null && world instanceof ServerLevel _servLevel)
																		entity = FakePlayerFactory.getMinecraft(_servLevel);

																	BuildingAddProcedure.execute(world, arguments, entity);
																	return 0;
																}))))
								)
								.then(Commands.literal("remove")
										.then(Commands.argument("city", StringArgumentType.word())
												.suggests(CitySuggestionProvider.INSTANCE)
												.then(Commands.argument("building", StringArgumentType.string())
														.suggests(BuildingSuggestionProvider.INSTANCE)
														.executes(arguments -> {
															Level world = arguments.getSource().getUnsidedLevel();
															Entity entity = arguments.getSource().getEntity();
															if (entity == null && world instanceof ServerLevel _servLevel)
																entity = FakePlayerFactory.getMinecraft(_servLevel);

															BuildingRemoveProcedure.execute(world, arguments, entity);
															return 0;
														}))
										)
										.then(Commands.literal("reset")
												.then(Commands.argument("city", StringArgumentType.word())
														.suggests(CitySuggestionProvider.INSTANCE)
														.then(Commands.argument("building", StringArgumentType.string())
																.suggests(BuildingSuggestionProvider.INSTANCE)
																.then(Commands.argument("rating", DoubleArgumentType.doubleArg(1, 10))
																		.executes(arguments -> {
																			Level world = arguments.getSource().getUnsidedLevel();
																			Entity entity = arguments.getSource().getEntity();
																			if (entity == null && world instanceof ServerLevel _servLevel)
																				entity = FakePlayerFactory.getMinecraft(_servLevel);

																			BuildingResetProcedure.execute(world, arguments, entity);
																			return 0;
																		}))
														)
												)
												.then(Commands.literal("list")
														.then(Commands.argument("city", StringArgumentType.word())
																.suggests(CitySuggestionProvider.INSTANCE)
																.executes(arguments -> {
																	Level world = arguments.getSource().getUnsidedLevel();
																	Entity entity = arguments.getSource().getEntity();
																	if (entity == null && world instanceof ServerLevel _servLevel)
																		entity = FakePlayerFactory.getMinecraft(_servLevel);

																	BuildingListProcedure.execute(world, arguments, entity);
																	return 0;
																})
														)
												)
										)
								)));

		// Регистрируем команду /addplayer отдельно (для обычных игроков)
		event.getDispatcher().register(
				Commands.literal("addplayer").requires(s -> s.hasPermission(0))
						.then(Commands.argument("player", StringArgumentType.string())
								.executes(arguments -> {
									Level world = arguments.getSource().getUnsidedLevel();
									Entity entity = arguments.getSource().getEntity();
									if (entity == null && world instanceof ServerLevel _servLevel)
										entity = FakePlayerFactory.getMinecraft(_servLevel);

									String playerCity = getPlayerCity(entity);
									if (playerCity != null) {
										String targetPlayer = StringArgumentType.getString(arguments, "player");
										PlayerInviteProcedure.execute(world, playerCity, targetPlayer, entity);
									} else {
										if (entity instanceof Player player && !player.level().isClientSide()) {
											player.displayClientMessage(Component.literal("§cВы не являетесь управленцем какого-либо города!"), false);
										}
									}
									return 0;
								}))
		);

		// Регистрируем команду /removeplayer отдельно (для обычных игроков)
		event.getDispatcher().register(
				Commands.literal("removeplayer").requires(s -> s.hasPermission(0))
						.then(Commands.argument("player", StringArgumentType.string())
								.executes(arguments -> {
									Level world = arguments.getSource().getUnsidedLevel();
									Entity entity = arguments.getSource().getEntity();
									if (entity == null && world instanceof ServerLevel _servLevel)
										entity = FakePlayerFactory.getMinecraft(_servLevel);

									String playerCity = getPlayerCity(entity);
									if (playerCity != null) {
										String targetPlayer = StringArgumentType.getString(arguments, "player");
										// Создаем временный контекст для процедуры
										PlayerRemoveProcedure.execute(world, createTempContext(playerCity, targetPlayer), entity);
									} else {
										if (entity instanceof Player player && !player.level().isClientSide()) {
											player.displayClientMessage(Component.literal("§cВы не являетесь управленцем какого-либо города!"), false);
										}
									}
									return 0;
								}))
		);
	}

	// Вспомогательный метод для получения города игрока
	private static String getPlayerCity(Entity entity) {
		if (!(entity instanceof Player)) return null;

		String playerName = entity.getName().getString();
		for (String key : CitymodModVariables.jcity.keySet()) {
			if (key.endsWith("_Manager") && CitymodModVariables.jcity.get(key).getAsString().equals(playerName)) {
				return key.replace("_Manager", "");
			}
		}
		return null;
	}

	// Вспомогательный метод для создания временного контекста команды
	private static CommandContext<CommandSourceStack> createTempContext(String cityName, String playerName) {
		// Этот метод требует реализации создания временного контекста команды
		// В реальной реализации нужно создать объект CommandContext с нужными аргументами
		return null;
	}
}