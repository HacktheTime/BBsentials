package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.packets.AbstractPacket;

public class SystemMessagePacket extends AbstractPacket {
    public final String message;
    public final boolean important;
    public final boolean ping;

    public SystemMessagePacket(String message, boolean important, boolean ping) {
        super(1, 1); //Min and Max supported Version
        this.message = message;
        this.important = important;
        this.ping = ping;
    }
}
