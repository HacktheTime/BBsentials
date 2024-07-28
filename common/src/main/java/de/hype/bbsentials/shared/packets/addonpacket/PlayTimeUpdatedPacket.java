package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.shared.constants.Islands;

import java.time.Instant;

public class PlayTimeUpdatedPacket extends ShareUpdateTime {
    public Islands islandType;

    public PlayTimeUpdatedPacket(Islands islandType, String serverId, Instant updateTime) {
        super(serverId, updateTime);
        this.islandType = islandType;
    }
}
