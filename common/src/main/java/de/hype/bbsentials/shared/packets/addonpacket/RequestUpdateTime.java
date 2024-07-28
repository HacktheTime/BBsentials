package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

public class RequestUpdateTime extends AbstractAddonPacket {
    public final String serverID;

    public RequestUpdateTime(String serverID) {
        super(1,1);
        this.serverID = serverID;
    }
}
