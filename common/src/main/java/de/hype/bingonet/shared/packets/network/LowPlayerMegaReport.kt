package de.hype.bingonet.shared.packets.network;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;

public class LowPlayerMegaReport extends AbstractPacket {
    public final int playerCount;
    public final String serverId;

    public LowPlayerMegaReport(int playerCount, String serverId) {
        super(1, 1);

        this.playerCount = playerCount;
        this.serverId = serverId;
    }
}
