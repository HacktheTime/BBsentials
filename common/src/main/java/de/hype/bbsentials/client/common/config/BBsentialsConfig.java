package de.hype.bbsentials.client.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hype.bbsentials.client.common.client.CustomGson;
import de.hype.bbsentials.client.common.client.ModUpdateHelper;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;

public abstract class BBsentialsConfig {
    public static final File configFolder = EnvironmentCore.utils.getConfigPath();
    public transient int shouldBeConfigVersion;
    public int configVersionCurrent;

    public BBsentialsConfig(int shouldBeConfigVersion) {
        this.shouldBeConfigVersion = shouldBeConfigVersion;
        this.configVersionCurrent = shouldBeConfigVersion;
    }

    /**
     * doInit can not be called in constructor because fields are loaded after super class. This means all loading will get reset to defaults
     */
    public void doInit() {
        loadConfiguration();
        doVersionCheck();
        onInit();
        registerConfig();
    }

    private void registerConfig() {
        ConfigManager.registerConfig(this);
    }

    private void doVersionCheck() {
        if (configVersionCurrent > shouldBeConfigVersion) {
            throw new RuntimeException("BBsentials Error: Your Config Version is newer than the current mod version supports! This is NOT supported. If you want to continue, please remove your current configs!");
        }
        else if (configVersionCurrent != shouldBeConfigVersion) {
            ModUpdateHelper.updateConfig(configVersionCurrent, shouldBeConfigVersion, this.getClass(), this);
        }
    }

    protected void loadConfiguration() {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (!java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    JsonObject jsonObject = loadJsonFile();
                    if (jsonObject.has(fieldName)) {
                        field.set(this, new Gson().fromJson(jsonObject.get(fieldName), field.getType()));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private JsonObject loadJsonFile() {
        String fileName = this.getClass().getSimpleName() + ".json";
        File configFile = new File(configFolder, fileName);

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new JsonObject();
    }

    public void save() {
        JsonObject jsonObject = new JsonObject();

        for (Field field : getClass().getDeclaredFields()) {
            if (!java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    jsonObject.add(field.getName(), CustomGson.create().toJsonTree(field.get(this)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        String fileName = getClass().getSimpleName() + ".json";
        File configFile = new File(configFolder, fileName);

        try (FileWriter writer = new FileWriter(configFile)) {
            Gson gson = CustomGson.create();
            String jsonOutput = gson.toJson(jsonObject);
            writer.write(jsonOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        loadConfiguration();
    }

    public void setDefault() {
    }

    public void onInit() {

    }
}
