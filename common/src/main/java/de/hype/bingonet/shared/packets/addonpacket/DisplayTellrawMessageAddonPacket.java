package de.hype.bingonet.shared.packets.addonpacket;

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket;

/**
 * Used to tell the client to generate a clientside tellraw message.
 * Disabled as of now due to the potential security issue clickable commands are.
 */
public class DisplayTellrawMessageAddonPacket extends AbstractAddonPacket {
    public final String rawJson;

    public DisplayTellrawMessageAddonPacket(String rawJson) {
        super(1, 1); //Min and Max supported Version
        this.rawJson = rawJson;
    }
}
