package de.hype.bingonet.client.common.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final List<BingoNetConfig> registeredConfigs = new ArrayList<>();

    public static void registerConfig(BingoNetConfig config) {
        registeredConfigs.add(config);
    }

    public static void reloadAllConfigs() {
        for (BingoNetConfig config : registeredConfigs) {
            config.loadConfiguration();
        }
    }

    public static List<Class<? extends BingoNetConfig>> getLoadedConfigClasses() {
        List<Class<? extends BingoNetConfig>> configClasses = new ArrayList<>();
        for (BingoNetConfig config : registeredConfigs) {
            configClasses.add(config.getClass());
        }
        return configClasses;
    }

    public static void saveAll() {
        for (BingoNetConfig config : registeredConfigs) {
            config.save();
        }
    }

    public static List<BingoNetConfig> getAllConfigs() {
        return new ArrayList<>(registeredConfigs);
    }
}
