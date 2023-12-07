package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.packets.AbstractPacket;

/**
 * Used to tell the client to generate a clientside tellraw message.
 * Disabled as of now due to the potential security issue clickable commands are.
 */
public class DisplayTellrawMessagePacket extends AbstractPacket {
    public final String rawJson;

    public DisplayTellrawMessagePacket(String rawJson) {
        super(1, 1); //Min and Max supported Version
        this.rawJson = rawJson;
    }
}
