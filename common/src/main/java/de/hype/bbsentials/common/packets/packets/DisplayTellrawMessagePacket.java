package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.packets.AbstractPacket;

public class DisplayTellrawMessagePacket extends AbstractPacket {
    public final String rawJson;

    public DisplayTellrawMessagePacket(String rawJson) {
        super(1, 1); //Min and Max supported Version
        this.rawJson = rawJson;
    }
}
