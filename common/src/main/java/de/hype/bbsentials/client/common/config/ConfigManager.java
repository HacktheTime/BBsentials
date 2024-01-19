package de.hype.bbsentials.client.common.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final List<BBsentialsConfig> registeredConfigs = new ArrayList<>();

    public static void registerConfig(BBsentialsConfig config) {
        registeredConfigs.add(config);
    }

    public static void reloadAllConfigs() {
        for (BBsentialsConfig config : registeredConfigs) {
            config.loadConfiguration();
        }
    }

    public static List<Class<? extends BBsentialsConfig>> getLoadedConfigClasses() {
        List<Class<? extends BBsentialsConfig>> configClasses = new ArrayList<>();
        for (BBsentialsConfig config : registeredConfigs) {
            configClasses.add(config.getClass());
        }
        return configClasses;
    }

    public static void saveAll() {
        for (BBsentialsConfig config : registeredConfigs) {
            config.save();
        }
    }

    public static List<BBsentialsConfig> getAllConfigs() {
        return new ArrayList<>(registeredConfigs);
    }
}
