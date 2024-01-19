package de.hype.bbsentials.client.common.client;

import de.hype.bbsentials.client.common.config.BBsentialsConfig;

public class ModUpdateHelper {

    public static <T extends BBsentialsConfig> void updateConfig(int currentVersion, int desiredVersion, Class<? extends BBsentialsConfig> configClass, BBsentialsConfig object) {
        //Unused as of now
    }
}
