// OpenCityInfoPacket.java
package com.example.citymod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenCityInfoPacket {
    private final String cityName;
    private final int cityLevel;
    private final int cityProgress;

    public OpenCityInfoPacket(String cityName, int cityLevel, int cityProgress) {
        this.cityName = cityName;
        this.cityLevel = cityLevel;
        this.cityProgress = cityProgress;
    }

    public OpenCityInfoPacket(FriendlyByteBuf buffer) {
        this.cityName = buffer.readUtf();
        this.cityLevel = buffer.readInt();
        this.cityProgress = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(cityName);
        buffer.writeInt(cityLevel);
        buffer.writeInt(cityProgress);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Здесь будет вызов открытия GUI на клиенте
            // Нужно зарегистрировать пакет в CitymodMod
        });
        context.get().setPacketHandled(true);
    }
}