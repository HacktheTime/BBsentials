package de.hype.bbsentials.client.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ConfigManager implements Serializable {
    private static final File CONFIG_FOLDER = EnvironmentCore.utils.getConfigPath();
    //DO NOT Change any of the following unless you know what you are doing!
    private final transient Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager() {
        loadConfigs();
    }

    public static void loadConfigs() {
        for (Class<?> configClass : AToLoadBBsentialsConfigUtils.getAnnotatedClasses()) {
            File configFile = new File(CONFIG_FOLDER, configClass.getSimpleName() + ".json");
            File bakFile = new File(CONFIG_FOLDER, configClass.getSimpleName() + ".bak");

            try {
                Object configObject = null;
                if (!configFile.exists()) {
                    if (bakFile.exists()) {
                        restoreFromBackup(configFile, bakFile);
                    }
                    else {
                        createBackup(configFile, bakFile);
                        configObject = handleFileNotFound(configClass);
                    }
                }
                try (FileReader reader = new FileReader(configFile)) {
                    Gson gson = new Gson();
                    configObject = gson.fromJson(reader, configClass);

                    // Update the corresponding static field based on reflection
                } catch (FileNotFoundException ignored) {
                    updateConfigField(configClass, configObject);
                    System.err.println("BBsentials: Could not locate config for: " + configClass.getSimpleName());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Config File couldnt be loaded. Creating new one. Backed up old.");
                    try {
                        createBackup(configFile, bakFile);
                        handleFileNotFound(configClass);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        }

        public static void saveAll () {
            for (Class<?> configClass : AToLoadBBsentialsConfigUtils.getAnnotatedClasses()) {
                saveConfig(configClass);
            }
        }

        private static void updateConfigField (Class < ? > configClass, Object configObject){
            try {
                Field field = null;
                for (Field declaredField : BBsentials.class.getDeclaredFields()) {
                    if (declaredField.getName().toLowerCase().equals(configClass.getSimpleName().toLowerCase())){
                        field=declaredField;
                        break;
                    }
                }
                if (field==null){
                    throw new RuntimeException("Error didnt find");
                }
                field.setAccessible(true);

                if (configObject == null) {
                    // If the provided object is null, create a new instance using the default constructor
                    configObject = configClass.getDeclaredConstructor().newInstance();
                }

                field.set(null, configObject);
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }


        private static Object getConfigField (Class < ? > configClass){
            try {
                Field field = null;
                for (Field declaredField : BBsentials.class.getDeclaredFields()) {
                    if (declaredField.getName().toLowerCase().equals(configClass.getSimpleName().toLowerCase())){
                        field=declaredField;
                        break;
                    }
                }
                field.setAccessible(true);
                return field.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return null;
        }

        private static void saveConfig (Class < ? > configClass){
            File configFile = new File(CONFIG_FOLDER, configClass.getSimpleName() + ".json");

            try (FileWriter writer = new FileWriter(configFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(getConfigField(configClass), writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private static BBsentialsConfig handleFileNotFound(Class<?> configClass) {
        try {
            Object configInstance = configClass.getDeclaredConstructor().newInstance();
            System.out.println("VisualConfig ClassLoader: " + VisualConfig.class.getClassLoader());
            System.out.println("BBsentialsConfig ClassLoader: " + BBsentialsConfig.class.getClassLoader());

            System.out.println("ConfigInstance Class: " + configInstance.getClass());
            System.out.println("BBsentialsConfig Class: " + BBsentialsConfig.class);

            if (BBsentialsConfig.class.isAssignableFrom(configInstance.getClass())) {
                BBsentialsConfig bbsentialsConfig = (BBsentialsConfig) configInstance;
                bbsentialsConfig.setDefault();

                System.out.println("Config file not found. Using default values:");
                printConfigLines(configClass);

                return bbsentialsConfig;
            } else {
                System.err.println("Invalid config class provided. It does not extend BBsentialsConfig.");
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void printConfigLines (Class < ? > configClass){
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(CONFIG_FOLDER, configClass.getSimpleName() + ".json")));
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.print(line);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void createBackup (File configFile, File bakFile) throws IOException {
            try (FileInputStream fis = new FileInputStream(configFile);
                 FileOutputStream fos = new FileOutputStream(bakFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }catch (FileNotFoundException ignored){}
        }

        private static void restoreFromBackup (File configFile, File bakFile) throws IOException {
            try (FileInputStream fis = new FileInputStream(bakFile);
                 FileOutputStream fos = new FileOutputStream(configFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }
        }
    }
