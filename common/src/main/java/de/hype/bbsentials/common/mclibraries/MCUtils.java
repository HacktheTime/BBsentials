package de.hype.bbsentials.common.mclibraries;

import de.hype.bbsentials.common.chat.Message;

import java.io.File;

public interface MCUtils {
    boolean isWindowFocused();

    File getConfigPath();

    String getUsername();

    String getMCUUID();

    void sendChatMessage(String message);

    void playsound(String eventName);

    void sendClientSideMessage(Message message);

    int getPotTime();
}
