package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

/**
 * Used to tell the addon what message came in.
 */
public class ReceivedPublicChatMessageAddonPacket extends AbstractAddonPacket {
    public final String rawJson;
    public final String unformattedString;

    public ReceivedPublicChatMessageAddonPacket(String rawJson, String unformattedString) {
        super(1, 1); //Min and Max supported Version
        this.rawJson = rawJson;
        this.unformattedString = unformattedString;
    }
}
