package de.hype.bbsentials.common.mclibraries;

import java.io.File;
import java.util.List;

public interface MCUtils {
    boolean isWindowFocused();

    File getConfigPath();

    String getUsername();

    String getMCUUID();
    void playsound(String eventName);

    int getPotTime();

    String mojangAuth(String serverId);

    List<String> getSplashLeechingPlayers();
}
