package com.example.citymod.network;

import com.example.citymod.CitymodMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PlayerListPacket {
    private final String cityName;

    public PlayerListPacket(String cityName) {
        this.cityName = cityName;
    }

    public PlayerListPacket(FriendlyByteBuf buffer) {
        this.cityName = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(cityName);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getSender() != null) {
                // На серверной стороне отправляем данные обратно клиенту
                CitymodMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) context.get().getSender()),
                        new PlayerListDataPacket(cityName));
            }
        });
        context.get().setPacketHandled(true);
    }
}