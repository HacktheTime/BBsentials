package de.hype.bbsentials.forge.common.mclibraries;

import java.io.File;

public interface MCUtils {
    boolean isWindowFocused();

    File getConfigPath();

    String getUsername();

    String getMCUUID();
    void playsound(String eventName);

    int getPotTime();
}
