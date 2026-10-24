package ru.blagovisual;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Настройки мода. Хранятся в config/blagovisual.json. */
public class BlagoConfig {
    // --- Прицел ---
    public static boolean crosshairEnabled = true;
    /** 0 крест, 1 точка, 2 круг, 3 ромб, 4 крест с кольцом */
    public static int crosshairStyle = 0;
    /** 0 радуга, 1 циан, 2 розовый, 3 белый, 4 фиолетовый */
    public static int crosshairColor = 0;
    public static int crosshairSize = 5;
    public static int crosshairGap = 3;
    public static boolean crosshairDynamic = true;
    public static boolean crosshairTargetRed = true;

    // --- HUD ---
    public static boolean watermark = true;
    public static boolean infoPanel = true;
    public static boolean targetHud = true;
    public static boolean swapToast = true;

    // --- AutoSwap ---
    /** 0 тотем, 1 золотое яблоко, 2 щит, 3 зачарованное золотое яблоко, 4 жемчуг */
    public static int swapItem = 0;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("blagovisual.json");
    }

    private static class Data {
        boolean crosshairEnabled = true;
        int crosshairStyle = 0, crosshairColor = 0, crosshairSize = 5, crosshairGap = 3;
        boolean crosshairDynamic = true, crosshairTargetRed = true;
        boolean watermark = true, infoPanel = true, targetHud = true, swapToast = true;
        int swapItem = 0;
    }

    public static void load() {
        try {
            if (!Files.exists(path())) { save(); return; }
            Data d = GSON.fromJson(Files.readString(path()), Data.class);
            if (d == null) return;
            crosshairEnabled = d.crosshairEnabled;
            crosshairStyle = d.crosshairStyle;
            crosshairColor = d.crosshairColor;
            crosshairSize = d.crosshairSize;
            crosshairGap = d.crosshairGap;
            crosshairDynamic = d.crosshairDynamic;
            crosshairTargetRed = d.crosshairTargetRed;
            watermark = d.watermark;
            infoPanel = d.infoPanel;
            targetHud = d.targetHud;
            swapToast = d.swapToast;
            swapItem = d.swapItem;
        } catch (Exception ignored) {
        }
    }

    public static void save() {
        try {
            Data d = new Data();
            d.crosshairEnabled = crosshairEnabled;
            d.crosshairStyle = crosshairStyle;
            d.crosshairColor = crosshairColor;
            d.crosshairSize = crosshairSize;
            d.crosshairGap = crosshairGap;
            d.crosshairDynamic = crosshairDynamic;
            d.crosshairTargetRed = crosshairTargetRed;
            d.watermark = watermark;
            d.infoPanel = infoPanel;
            d.targetHud = targetHud;
            d.swapToast = swapToast;
            d.swapItem = swapItem;
            Files.writeString(path(), GSON.toJson(d));
        } catch (IOException ignored) {
        }
    }
}
