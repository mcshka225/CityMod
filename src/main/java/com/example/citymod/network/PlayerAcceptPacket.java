package com.example.citymod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerAcceptPacket {
    private final String cityName;

    public PlayerAcceptPacket(String cityName) {
        this.cityName = cityName;
    }

    public PlayerAcceptPacket(FriendlyByteBuf buffer) {
        this.cityName = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(cityName);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getSender() != null) {
                com.example.citymod.prdc.PlayerAcceptProcedure.execute(
                        context.get().getSender().level(),
                        cityName,
                        context.get().getSender()
                );
            }
        });
        context.get().setPacketHandled(true);
    }
}