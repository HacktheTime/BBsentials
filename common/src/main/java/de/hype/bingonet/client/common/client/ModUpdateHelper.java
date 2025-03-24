package de.hype.bingonet.client.common.client;

import de.hype.bingonet.client.common.config.BingoNetConfig;

public class ModUpdateHelper {

    public static <T extends BingoNetConfig> void updateConfig(int currentVersion, int desiredVersion, Class<? extends BingoNetConfig> configClass, BingoNetConfig object) {
        //Unused as of now
    }
}
