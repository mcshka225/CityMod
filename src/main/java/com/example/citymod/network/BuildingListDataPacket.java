package com.example.citymod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BuildingListDataPacket {
    private final String cityName;

    public BuildingListDataPacket(String cityName) {
        this.cityName = cityName;
    }

    public BuildingListDataPacket(FriendlyByteBuf buffer) {
        this.cityName = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(cityName);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // На клиентской стороне открываем GUI
            if (context.get().getDirection().getReceptionSide().isClient()) {
                // Здесь нужно получить данные о городе (level, progress, teamColor)
                // Для простоты можно передать значения по умолчанию или получить из CitymodModVariables
                com.example.citymod.client.gui.BuildingListScreen.openScreen(cityName, 0, 0, "§f");
            }
        });
        context.get().setPacketHandled(true);
    }
}