package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

/**
 * Plays the specified sound path on the client.
 */
public class PlaySoundAddonPacket extends AbstractAddonPacket {
    public final String namespace;
    public final String path;

    public PlaySoundAddonPacket(String path, String namespace) {
        super(1, 1); //Min and Max supported Version
        this.namespace = namespace;
        this.path = path;
    }
}
