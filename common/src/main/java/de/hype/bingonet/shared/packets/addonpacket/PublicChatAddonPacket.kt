package de.hype.bingonet.shared.packets.addonpacket;

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket;

/**
 * Sends the specified message to the server.
 */
public class PublicChatAddonPacket extends AbstractAddonPacket {
    public final String message;
    public final double timing;

    public PublicChatAddonPacket(String message, double timing) {
        super(1, 1); //Min and Max supported Version
        this.message = message;
        this.timing = timing;
    }
}
