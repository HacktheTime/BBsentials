package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;

/**
 * Used to find collect Data across Servers. Can be used to find Users or Lobbies by ID
 */
public class WantedSearchPacket extends AbstractPacket {
    public boolean targetFound = true;
    public String finder;
    public String username;
    public Long dcUserId;
    public String serverId;

    public WantedSearchPacket(String mcUsername, Long dcUserId, String serverId) {
        super(1, 1);
        this.username = mcUsername;
        this.dcUserId = dcUserId;
        this.serverId = serverId;
    }

    public static WantedSearchPacket findDCUser(Long id) {
        WantedSearchPacket packet = new WantedSearchPacket(null, id, null);
        packet.targetFound = false;
        return packet;
    }

    public static WantedSearchPacket findMcUser(String username) {
        WantedSearchPacket packet = new WantedSearchPacket(username, null, null);
        packet.targetFound = false;
        return packet;
    }

    public static WantedSearchPacket findServer(String serverId) {
        WantedSearchPacket packet = new WantedSearchPacket(null, null, serverId);
        packet.targetFound = false;
        return packet;
    }
}
