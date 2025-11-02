package com.example.citymod.client.gui;

import com.example.citymod.network.CitymodModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuildingListScreen extends Screen {
    private final String cityName;
    private final int cityLevel;
    private final int cityProgress;
    private final String teamColor;
    private List<BuildingInfo> buildings;
    private int currentPage = 0;
    private int buildingsPerPage = 8;

    private int imageWidth = 250;
    private int imageHeight = 180;
    private int leftPos;
    private int topPos;

    private static class BuildingInfo {
        String name;
        double rating;

        BuildingInfo(String name, double rating) {
            this.name = name;
            this.rating = rating;
        }
    }

    public static void openScreen(String cityName, int cityLevel, int cityProgress, String teamColor) {
        Minecraft.getInstance().setScreen(new BuildingListScreen(cityName, cityLevel, cityProgress, teamColor));
    }

    public BuildingListScreen(String cityName, int cityLevel, int cityProgress, String teamColor) {
        super(Component.literal("Постройки города"));
        this.cityName = cityName;
        this.cityLevel = cityLevel;
        this.cityProgress = cityProgress;
        this.teamColor = teamColor;
        loadBuildings();
    }

    private void loadBuildings() {
        buildings = new ArrayList<>();
        double totalRating = 0;

        for (Map.Entry<String, JsonElement> entry : CitymodModVariables.jcity.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(cityName + "_") && entry.getValue().isJsonObject()) {
                JsonObject buildingData = entry.getValue().getAsJsonObject();
                String buildingName = buildingData.get("name").getAsString();
                double rating = buildingData.get("rating").getAsDouble();

                buildings.add(new BuildingInfo(buildingName, rating));
                totalRating += rating;
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        // Кнопка "Предыдущая страница"
        this.addRenderableWidget(Button.builder(Component.literal("◀ Пред."), button -> {
                    if (currentPage > 0) {
                        currentPage--;
                    }
                })
                .bounds(leftPos + 10, topPos + imageHeight - 25, 50, 20)
                .build());

        // Кнопка "Следующая страница"
        this.addRenderableWidget(Button.builder(Component.literal("След. ▶"), button -> {
                    if ((currentPage + 1) * buildingsPerPage < buildings.size()) {
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

        guiGraphics.drawCenteredString(this.font, "§6Постройки города", this.width / 2, topPos + 12, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, "§e" + cityName, this.width / 2, topPos + 25, 0xFFFFFF);

        // Отображаем постройки на текущей странице
        int startIndex = currentPage * buildingsPerPage;
        int endIndex = Math.min(startIndex + buildingsPerPage, buildings.size());

        int maxVisibleBuildings = (imageHeight - 105) / 15;
        int actualBuildingsToShow = Math.min(endIndex - startIndex, maxVisibleBuildings);

        for (int i = 0; i < actualBuildingsToShow; i++) {
            BuildingInfo building = buildings.get(startIndex + i);
            int yPos = topPos + 45 + i * 15;

            // Название постройки
            guiGraphics.drawString(this.font, "§7- §f" + building.name, leftPos + 15, yPos, 0xFFFFFF, false);

            // Оценка справа
            String ratingText = "§6" + String.format("%.1f", building.rating);
            int ratingWidth = this.font.width(ratingText);
            guiGraphics.drawString(this.font, ratingText, leftPos + imageWidth - ratingWidth - 15, yPos, 0xFFFFFF, false);
        }

        // Статистика внизу
        double averageRating = buildings.isEmpty() ? 0 : buildings.stream().mapToDouble(b -> b.rating).average().orElse(0);
        String stats = "§7Всего: §a" + buildings.size() + " §7| Средняя оценка: §6" + String.format("%.1f", averageRating);
        guiGraphics.drawCenteredString(this.font, stats, this.width / 2, topPos + imageHeight - 55, 0xFFFFFF);

        // Информация о странице
        String pageInfo = "§7Страница " + (currentPage + 1) + "/" + ((buildings.size() - 1) / buildingsPerPage + 1);
        guiGraphics.drawCenteredString(this.font, pageInfo, this.width / 2, topPos + imageHeight - 45, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}