package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

import java.time.Instant;

public class ShareUpdateTime extends AbstractAddonPacket {
    public final String serverID;
    public final Instant updateTime;

    public ShareUpdateTime(String serverID, Instant updateTime) {
        super(1, 1);
        this.serverID = serverID;
        this.updateTime = updateTime;
    }
}
