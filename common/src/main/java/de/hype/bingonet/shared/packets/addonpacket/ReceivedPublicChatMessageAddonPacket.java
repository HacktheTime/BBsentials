package de.hype.bingonet.shared.packets.addonpacket;

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bingonet.shared.objects.Message;

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
