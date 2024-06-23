package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

public class StatusUpdateAddonPacket extends AbstractAddonPacket {
    public Status status;

    public StatusUpdateAddonPacket(Status status) {
        super(1, 1);
        this.status = status;
    }
}

