package de.hype.bbsentials.client.common.mclibraries;

import de.hype.bbsentials.shared.objects.Message;
import de.hype.bbsentials.client.common.client.BBsentials;

public interface MCChat {
    void init();

    void sendChatMessage(String message);

    void sendClientSideMessage(Message message);

    default void sendClientSideMessage(Message message, boolean actionbar) {
        if (BBsentials.funConfig.swapActionBarChat && !BBsentials.funConfig.swapOnlyNormal) {
            actionbar = !actionbar;
        }
        else {
            sendClientSideMessage(message);
        }
    }

    void showActionBar(Message message);
}
