package de.hype.bbsentials.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class ToDisplayConfig {
    private boolean disableAll = true;
    private boolean prehistoricEgg = true;
    private boolean pickonimbus2000 = true;
    private boolean controlSwitch = true;
    private boolean electronTransmitter = true;
    private boolean ftx3070 = true;
    private boolean robotronReflector = true;
    private boolean superliteMotor = true;
    private boolean syntheticHeart = true;
    private boolean flawlessGemstone = true;
    private boolean allRoboPartCustomChChestItem = true;
    private boolean allChChestItem = true;
    private boolean dwBetterTogether = true;
    private boolean dwDoublePowder = true;
    private boolean dwGoneWithTheWind = true;
    private boolean dwGoblinRaid = true;
    private boolean dwMithrilGourmand = true;
    private boolean dwRaffle = true;
    private boolean dwEvents = true;
    private boolean chBetterTogether = true;
    private boolean chDoublePowder = true;
    private boolean chGoneWithTheWind = true;
    private boolean chEvents = true;
    private boolean allBetterTogether = true;
    private boolean allDoublePowder = true;
    private boolean allGoneWithTheWind = true;
    private boolean allEvents = true;

    // Serialize the object to JSON and save to file
    public void saveToFile() {
        File configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "BBsentials_display_Config.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Deserialize the object from JSON file
    public static ToDisplayConfig loadFromFile() {
        File configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "BBsentials_display_Config.json");
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(configFile)) {
            return gson.fromJson(reader, ToDisplayConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If file doesn't exist or there's an error, return a new instance
        return new ToDisplayConfig();
    }

    public void setValueAndSave(String propertyName, Object newValue) {
        String lowerCasePropertyName = propertyName.toLowerCase();

        try {
            Field field = getClass().getDeclaredField(lowerCasePropertyName);
            field.setAccessible(true);

            field.set(this, newValue);
            saveToFile();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // Method to get a value based on property name
    public boolean getValue(String propertyName) {
        if (disableAll) return false;
        String lowerCasePropertyName = propertyName.toLowerCase();
        try {
            Field field = getClass().getDeclaredField(lowerCasePropertyName);
            field.setAccessible(true);
            return field.getBoolean(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
}
