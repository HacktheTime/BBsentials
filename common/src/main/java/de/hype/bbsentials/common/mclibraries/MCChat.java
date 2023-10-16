package de.hype.bbsentials.common.mclibraries;

import de.hype.bbsentials.common.chat.Message;

public interface MCChat {
    void init();
    void sendChatMessage(String message);
    void sendClientSideMessage(Message message);

    void sendClientSideMessage(Message message, boolean actionbar);

    void showActionBar(Message message);
}
