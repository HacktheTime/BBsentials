package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

public class PausePacket extends AbstractAddonPacket {
    public boolean setPaused;

    public PausePacket(boolean setPaused) {
        super(1, 1);
        this.setPaused=setPaused;
    }
}
