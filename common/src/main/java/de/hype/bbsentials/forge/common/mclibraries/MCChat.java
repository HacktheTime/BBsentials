package de.hype.bbsentials.forge.common.mclibraries;

import de.hype.bbsentials.forge.common.chat.Message;

public interface MCChat {
    void init();
    void sendChatMessage(String message);
    void sendClientSideMessage(Message message);
}
