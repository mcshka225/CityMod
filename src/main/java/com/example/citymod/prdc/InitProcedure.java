package com.example.citymod.prdc;

import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.api.distmarker.Dist;

import com.example.citymod.network.CitymodModVariables;

import javax.annotation.Nullable;

import java.io.IOException;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.DEDICATED_SERVER})
public class InitProcedure {
	@SubscribeEvent
	public static void init(FMLDedicatedServerSetupEvent event) {
		execute();
	}

	public static void execute() {
		execute(null);
	}

	private static void execute(@Nullable Event event) {
		if (CitymodModVariables.city.getName() == null) {
			try {
				CitymodModVariables.city.getParentFile().mkdirs();
				CitymodModVariables.city.createNewFile();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
}