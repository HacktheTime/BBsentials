package de.hype.bingonet.shared.packets.network;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;

/**
 * Server to Client. Displays a message on the Client.
 */
public class SystemMessagePacket extends AbstractPacket {
    public final String message;
    public final boolean important;
    public final boolean ping;

    /**
     * @param message   the Message
     * @param important whether the message is important
     * @param ping      whether the client shall play the ping sound
     */
    public SystemMessagePacket(String message, boolean important, boolean ping) {
        super(1, 1); //Min and Max supported Version
        this.message = message;
        this.important = important;
        this.ping = ping;
    }
}
