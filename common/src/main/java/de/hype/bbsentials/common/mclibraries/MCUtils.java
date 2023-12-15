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

    // Leechers was originally inveneted by Calva but redone by me without access to the code, I made it since Calvas mod was private at that date
    List<String> getSplashLeechingPlayers();

    void registerSplashOverlay();
}
