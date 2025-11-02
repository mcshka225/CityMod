// BuildingListProcedure.java - используем новую процедуру
package com.example.citymod.prdc;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.world.level.LevelAccessor;

public class BuildingListProcedure {
    public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments, Entity entity) {
        if (entity == null)
            return;

        String cityName = StringArgumentType.getString(arguments, "city");

        // Используем ту же логику, что и в OnClickBlockProcedure
        BuildingDisplayProcedure.execute(world, cityName, entity);
    }
}