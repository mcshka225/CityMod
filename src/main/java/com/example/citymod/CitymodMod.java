package com.example.citymod;

import com.example.citymod.network.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import com.example.citymod.init.CitymodModItems;
import com.example.citymod.init.CitymodModBlocks;
import com.example.citymod.init.CitymodModBlockEntities;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod("citymod")
public class CitymodMod {
	public static final Logger LOGGER = LogManager.getLogger(CitymodMod.class);
	public static final String MODID = "citymod";

	public CitymodMod(FMLJavaModLoadingContext context) {
		// Start of user code block mod constructor
		// End of user code block mod constructor
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus bus = context.getModEventBus();

		CitymodModBlocks.REGISTRY.register(bus);
		CitymodModBlockEntities.REGISTRY.register(bus);
		CitymodModItems.REGISTRY.register(bus);

		CitymodMod.addNetworkMessage(PlayerInvitePacket.class, PlayerInvitePacket::encode, PlayerInvitePacket::new, PlayerInvitePacket::handle);
		CitymodMod.addNetworkMessage(PlayerAcceptPacket.class, PlayerAcceptPacket::encode, PlayerAcceptPacket::new, PlayerAcceptPacket::handle);
		CitymodMod.addNetworkMessage(PlayerListPacket.class, PlayerListPacket::encode, PlayerListPacket::new, PlayerListPacket::handle);
		CitymodMod.addNetworkMessage(PlayerListDataPacket.class, PlayerListDataPacket::encode, PlayerListDataPacket::new, PlayerListDataPacket::handle);
		CitymodMod.addNetworkMessage(BuildingListDataPacket.class, BuildingListDataPacket::encode, BuildingListDataPacket::new, BuildingListDataPacket::handle);

		CitymodMod.addNetworkMessage(PlayerInvitePacket.class, PlayerInvitePacket::encode, PlayerInvitePacket::new, PlayerInvitePacket::handle);
		CitymodMod.addNetworkMessage(PlayerAcceptPacket.class, PlayerAcceptPacket::encode, PlayerAcceptPacket::new, PlayerAcceptPacket::handle);

		CitymodMod.addNetworkMessage(OpenCityInfoPacket.class, OpenCityInfoPacket::encode, OpenCityInfoPacket::new, OpenCityInfoPacket::handle);
		CitymodMod.addNetworkMessage(BuildingListPacket.class, BuildingListPacket::encode, BuildingListPacket::new, BuildingListPacket::handle);
		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
}