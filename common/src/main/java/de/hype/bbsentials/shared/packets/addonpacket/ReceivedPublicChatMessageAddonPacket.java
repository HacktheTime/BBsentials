package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

/**
 * Used to tell the addon what message came in.
 */
public class ReceivedPublicChatMessageAddonPacket extends AbstractAddonPacket {
    public final Message message;

    public ReceivedPublicChatMessageAddonPacket(Message message) {
        super(1, 1); //Min and Max supported Version
        this.message = message;
    }
}
