package de.hype.bbsentials.client.common.mclibraries;

import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;

public interface MCChat {
    void init();
    void sendChatMessage(String message);
    void sendClientSideMessage(Message message);

    default void sendClientSideMessage(Message message, boolean actionbar) {
        if (BBsentials.config.swapActionBarChat && !BBsentials.config.swapOnlyNormal) {
            actionbar = !actionbar;
        }
        else {
            sendClientSideMessage(message);
        }
    }
    void showActionBar(Message message);
}
