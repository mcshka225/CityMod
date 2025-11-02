package com.example.citymod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerInvitePacket {
    private final String cityName;
    private final String playerName;

    public PlayerInvitePacket(String cityName, String playerName) {
        this.cityName = cityName;
        this.playerName = playerName;
    }

    public PlayerInvitePacket(FriendlyByteBuf buffer) {
        this.cityName = buffer.readUtf();
        this.playerName = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(cityName);
        buffer.writeUtf(playerName);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getSender() != null) {
                com.example.citymod.prdc.PlayerInviteProcedure.execute(
                        context.get().getSender().level(),
                        cityName,
                        playerName,
                        context.get().getSender()
                );
            }
        });
        context.get().setPacketHandled(true);
    }
}