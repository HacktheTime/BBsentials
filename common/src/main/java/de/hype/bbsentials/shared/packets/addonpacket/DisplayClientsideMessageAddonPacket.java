package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

/**
 * Used to tell the client to generate a clientside message.
 */
public class DisplayClientsideMessageAddonPacket extends AbstractAddonPacket {
    public final String message;
    public final String formatting;

    public DisplayClientsideMessageAddonPacket(String message, String formatting) {
        super(1, 1); //Min and Max supported Version
        this.message = message;
        this.formatting = formatting;
    }
}
