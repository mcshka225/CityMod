package com.example.citymod.client.gui;

import com.example.citymod.network.BuildingListPacket;
import com.example.citymod.network.PlayerListPacket;
import com.example.citymod.CitymodMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CityInfoScreen extends Screen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("citymod", "textures/gui/city_info.png");

    private final String cityName;
    private final int cityLevel;
    private final int cityProgress;
    private final String teamColor;

    private int imageWidth = 200;
    private int imageHeight = 150;
    private int leftPos;
    private int topPos;

    public CityInfoScreen(String cityName, int cityLevel, int cityProgress, String teamColor) {
        super(Component.literal("Информация о городе"));
        this.cityName = cityName;
        this.cityLevel = cityLevel;
        this.cityProgress = cityProgress;
        this.teamColor = teamColor;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        // Кнопка "Состав" в левом нижнем углу
        int buttonWidth = 80;
        int buttonHeight = 20;
        int buttonXLeft = leftPos + 10; // Отступ слева
        int buttonXRight = leftPos + imageWidth - buttonWidth - 10; // Отступ справа
        int buttonY = topPos + 120;

        // Кнопка "Состав"
        this.addRenderableWidget(Button.builder(Component.literal("§aСостав"), button -> {
                    this.minecraft.setScreen(null);
                    if (this.minecraft.player != null) {
                        CitymodMod.PACKET_HANDLER.sendToServer(new PlayerListPacket(cityName));
                    }
                })
                .bounds(buttonXLeft, buttonY, buttonWidth, buttonHeight)
                .build());

        // Кнопка "Постройки" в правом нижнем углу
        this.addRenderableWidget(Button.builder(Component.literal("§6Постройки"), button -> {
                    this.minecraft.setScreen(null);
                    if (this.minecraft.player != null) {
                        CitymodMod.PACKET_HANDLER.sendToServer(new BuildingListPacket(cityName));
                    }
                })
                .bounds(buttonXRight, buttonY, buttonWidth, buttonHeight)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        // Рисуем фон GUI
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF404040);
        guiGraphics.fill(leftPos + 5, topPos + 5, leftPos + imageWidth - 5, topPos + imageHeight - 5, 0xFF202020);

        guiGraphics.drawCenteredString(this.font, "§6Информация о городе", this.width / 2, topPos + 12, 0xFFFFFF);

        // Название города цветом команды
        String coloredCityName = teamColor + cityName;
        Component cityNameComponent = Component.literal(coloredCityName);
        int cityNameWidth = this.font.width(cityNameComponent);
        guiGraphics.drawString(this.font, cityNameComponent, leftPos + (imageWidth - cityNameWidth) / 2, topPos + 40, 0xFFFFFF, false);

        // Уровень города
        guiGraphics.drawCenteredString(this.font, "§6Уровень: §f" + cityLevel, this.width / 2, topPos + 87, 0xFFFFFF);

        // Полоска прогресса
        int progressBarWidth = 160;
        int progressBarHeight = 12;
        int progressBarX = leftPos + (imageWidth - progressBarWidth) / 2;
        int progressBarY = topPos + 100;

        // Фон полоски прогресса
        guiGraphics.fill(progressBarX, progressBarY, progressBarX + progressBarWidth, progressBarY + progressBarHeight, 0xFF333333);

        // Заполненная часть
        int filledWidth = (int) (progressBarWidth * (cityProgress / 100.0));
        if (filledWidth > 0) {
            guiGraphics.fill(progressBarX, progressBarY, progressBarX + filledWidth, progressBarY + progressBarHeight, 0xFF3366FF);
        }

        // Обводка полоски прогресса
        guiGraphics.fill(progressBarX, progressBarY, progressBarX + progressBarWidth, progressBarY + 1, 0xFF555555);
        guiGraphics.fill(progressBarX, progressBarY + progressBarHeight - 1, progressBarX + progressBarWidth, progressBarY + progressBarHeight, 0xFF555555);
        guiGraphics.fill(progressBarX, progressBarY, progressBarX + 1, progressBarY + progressBarHeight, 0xFF555555);
        guiGraphics.fill(progressBarX + progressBarWidth - 1, progressBarY, progressBarX + progressBarWidth, progressBarY + progressBarHeight, 0xFF555555);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}