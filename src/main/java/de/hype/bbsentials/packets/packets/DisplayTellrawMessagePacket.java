package de.hype.bbsentials.packets.packets;


import de.hype.bbsentials.packets.AbstractPacket;

public class DisplayTellrawMessagePacket extends AbstractPacket {
    public final String rawJson;

    public DisplayTellrawMessagePacket(String rawJson) {
        super(1, 1); //Min and Max supported Version
        this.rawJson = rawJson;
    }
}
