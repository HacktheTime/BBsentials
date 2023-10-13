package de.hype.bbsentials.forge.common.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hype.bbsentials.forge.common.mclibraries.EnvironmentCore;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class ToDisplayConfig {
    public boolean disableAll = false;
    public boolean allChChestItem = true;
    public boolean allRoboPart = false;
    public boolean customChChestItem = false;

    public boolean prehistoricEgg = false;
    public boolean pickonimbus2000 = false;
    public boolean controlSwitch = false;
    public boolean electronTransmitter = false;
    public boolean ftx3070 = false;
    public boolean robotronReflector = false;
    public boolean superliteMotor = false;
    public boolean syntheticHeart = false;
    public boolean flawlessGemstone = false;
    public boolean jungleHeart = false;

    //Mining Events.
    public boolean allEvents = true;
    public boolean blockChEvents = false;

    public String betterTogether = "none";
    public String doublePowder = "none";
    public String goneWithTheWind = "none";
    public boolean goblinRaid = false;
    public boolean mithrilGourmand = false;
    public boolean raffle = false;

    // Serialize the object to JSON and save to file
    public void saveToFile() {
        File configFile = new File(EnvironmentCore.mcUtils.getConfigPath(), "BBsentials_display_Config.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Deserialize the object from JSON file
    public static ToDisplayConfig loadFromFile() {
        File configFile = new File(EnvironmentCore.mcUtils.getConfigPath(), "BBsentials_display_Config.json");
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
