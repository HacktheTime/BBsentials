package de.hype.bbsentials.client.common.mclibraries;

import de.hype.bbsentials.shared.objects.Message;
import de.hype.bbsentials.client.common.client.BBsentials;

public interface MCChat {
    void init();

    void sendClientSideMessage(Message message, boolean actionbar);

    void sendChatMessage(String message);

    default void sendClientSideMessage(Message message) {
        sendClientSideMessage(message, false);
    }

    default void showActionBar(Message message) {
        sendClientSideMessage(message, true);
    }
}
