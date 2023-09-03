package de.hype.bbsentials.packets.packets;


import de.hype.bbsentials.packets.AbstractPacket;

public class DisplayMessagePacket extends AbstractPacket {

    public final String message;

    public DisplayMessagePacket(String message) {
        super(1, 1); //Min and Max supported Version
        this.message = message;
    }
}
