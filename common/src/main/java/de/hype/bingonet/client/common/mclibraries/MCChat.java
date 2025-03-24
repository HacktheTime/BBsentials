package de.hype.bingonet.client.common.mclibraries;

import de.hype.bingonet.shared.objects.Message;
import de.hype.bingonet.client.common.client.BingoNet;

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
