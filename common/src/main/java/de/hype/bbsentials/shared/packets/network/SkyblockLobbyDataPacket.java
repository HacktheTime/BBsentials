package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.constants.Islands;

import java.util.List;

public class SkyblockLobbyDataPacket extends AbstractPacket {
    public final List<String> users;
    public final Long lobbyTime;
    public final String serverId;
    public final Islands island;

    public SkyblockLobbyDataPacket(List<String> users, Long lobbyTime, String serverId, Islands island) {
        super(1, 1);
        this.users = users;
        this.lobbyTime = lobbyTime;
        this.serverId = serverId;
        this.island = island;
    }
}
