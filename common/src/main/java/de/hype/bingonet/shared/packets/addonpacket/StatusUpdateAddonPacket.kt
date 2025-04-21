package de.hype.bingonet.shared.packets.addonpacket;

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket;

public class StatusUpdateAddonPacket extends AbstractAddonPacket {
    public Status status;

    public StatusUpdateAddonPacket(Status status) {
        super(1, 1);
        this.status = status;
    }
}

