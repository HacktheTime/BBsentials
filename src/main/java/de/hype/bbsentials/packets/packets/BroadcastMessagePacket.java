package de.hype.bbsentials.packets.packets;


import de.hype.bbsentials.packets.AbstractPacket;

public class BroadcastMessagePacket extends AbstractPacket {

    public final String message;
    public final String username;
    public final String prefix;

    public BroadcastMessagePacket(String prefix, String username, String message) {
        super(1, 1); //Min and Max supported Version
        this.message = message;
        this.username = username;
        this.prefix = prefix;
    }
}
