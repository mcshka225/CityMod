package com.example.citymod.client.gui;

import com.example.citymod.network.CitymodModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

public class PlayerListScreen extends Screen {
    private final String cityName;
    private final int cityLevel;
    private final int cityProgress;
    private final String teamColor;
    private List<String> players;
    private String managerName;
    private int currentPage = 0;
    private int playersPerPage = 10;

    private int imageWidth = 200;
    private int imageHeight = 150;
    private int leftPos;
    private int topPos;

    public PlayerListScreen(String cityName, int cityLevel, int cityProgress, String teamColor) {
        super(Component.literal("Состав города"));
        this.cityName = cityName;
        this.cityLevel = cityLevel;
        this.cityProgress = cityProgress;
        this.teamColor = teamColor;
        loadPlayers();
    }

    public static void openScreen(String cityName, int cityLevel, int cityProgress, String teamColor) {
        Minecraft.getInstance().setScreen(new PlayerListScreen(cityName, cityLevel, cityProgress, teamColor));
    }

    private void loadPlayers() {
        players = new ArrayList<>();

        // Получаем имя управленца
        if (CitymodModVariables.jcity.has(cityName + "_Manager")) {
            managerName = CitymodModVariables.jcity.get(cityName + "_Manager").getAsString();
        }

        if (CitymodModVariables.jcity.has(cityName + "_Players")) {
            JsonArray playersArray = CitymodModVariables.jcity.get(cityName + "_Players").getAsJsonArray();
            for (int i = 0; i < playersArray.size(); i++) {
                players.add(playersArray.get(i).getAsString());
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        // Кнопка "Предыдущая страница" (всегда видна, но неактивна на первой странице)
        this.addRenderableWidget(Button.builder(Component.literal("◀ Пред."), button -> {
                    if (currentPage > 0) {
                        currentPage--;
                    }
                })
                .bounds(leftPos + 10, topPos + imageHeight - 25, 50, 20)
                .build());

        // Кнопка "Следующая страница" (всегда видна, но неактивна на последней странице)
        this.addRenderableWidget(Button.builder(Component.literal("След. ▶"), button -> {
                    if ((currentPage + 1) * playersPerPage < players.size()) {
                        currentPage++;
                    }
                })
                .bounds(leftPos + imageWidth - 60, topPos + imageHeight - 25, 50, 20)
                .build());

        // Кнопка "Назад" - закрывает меню
        this.addRenderableWidget(Button.builder(Component.literal("Назад"), button -> {
                    this.minecraft.setScreen(null);
                })
                .bounds(leftPos + (imageWidth - 60) / 2, topPos + imageHeight - 25, 60, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        // Рисуем фон GUI
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF404040);
        guiGraphics.fill(leftPos + 5, topPos + 5, leftPos + imageWidth - 5, topPos + imageHeight - 5, 0xFF202020);

        guiGraphics.drawCenteredString(this.font, "§aСостав города", this.width / 2, topPos + 12, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, "§e" + cityName, this.width / 2, topPos + 25, 0xFFFFFF);

        // Отображаем игроков на текущей странице
        int startIndex = currentPage * playersPerPage;
        int endIndex = Math.min(startIndex + playersPerPage, players.size());

        // Рассчитываем максимальное количество строк
        int maxVisiblePlayers = (imageHeight - 85) / 10;
        int actualPlayersToShow = Math.min(endIndex - startIndex, maxVisiblePlayers);

        for (int i = 0; i < actualPlayersToShow; i++) {
            String playerName = players.get(startIndex + i);
            int yPos = topPos + 45 + i * 10;

            // Если игрок - управленец, отображаем золотым цветом
            String playerDisplay = playerName.equals(managerName) ?
                    "§6★ " + playerName + " §7(Управленец)" :
                    "§7- §f" + playerName;

            guiGraphics.drawString(this.font, playerDisplay, leftPos + 20, yPos, 0xFFFFFF, false);
        }

        // Отображаем информацию о странице
        String pageInfo = "§7Страница " + (currentPage + 1) + "/" + ((players.size() - 1) / playersPerPage + 1);
        guiGraphics.drawCenteredString(this.font, pageInfo, this.width / 2, topPos + imageHeight - 50, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}